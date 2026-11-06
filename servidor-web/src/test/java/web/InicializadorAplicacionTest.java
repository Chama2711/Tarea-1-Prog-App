package web;

import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.*;
import jakarta.xml.ws.Endpoint;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.*;
import logica.IServidorCentral;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import soap.ServicioCatalogo;
import soap.ServicioEvaluaciones;
import static org.junit.Assert.*;

/** El arranque web prepara ambos clientes SOAP sin parámetros JDBC ni central local. */
public class InicializadorAplicacionTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();
    private HttpServer http;
    private Endpoint catalogo, evaluaciones;
    private final Map<String, Object> atributos = new HashMap<>();
    private static <T> T proxy(Class<T> tipo, java.lang.reflect.InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
        case "getAttribute" -> atributos.get(a[0]);
        case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
        case "removeAttribute" -> { atributos.remove(a[0]); yield null; }
        case "log" -> null;
        default -> throw new AssertionError("El arranque no debe leer configuración JDBC: " + m.getName());
    });
    private String publicar() throws Exception {
        var central = proxy(IServidorCentral.class, (p, m, a) -> {
            throw new AssertionError("El arranque no debe consultar datos ni abrir persistencia");
        });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        catalogo = Endpoint.create(new ServicioCatalogo(central));
        evaluaciones = Endpoint.create(new ServicioEvaluaciones(central));
        catalogo.publish(http.createContext("/catalogo"));
        evaluaciones.publish(http.createContext("/evaluaciones"));
        http.start();
        return "http://127.0.0.1:" + http.getAddress().getPort();
    }
    private void conConfiguracion(String contenido, Runnable prueba) throws Exception {
        Path home = temporal.newFolder().toPath();
        Path carpeta = Files.createDirectory(home.resolve(".edEXT"));
        Files.writeString(carpeta.resolve("web.properties"), contenido);
        String anterior = System.getProperty("user.home");
        try {
            System.setProperty("user.home", home.toString());
            prueba.run();
        } finally {
            System.setProperty("user.home", anterior);
        }
    }
    @After public void cerrar() {
        try { if (evaluaciones != null) evaluaciones.stop(); }
        finally {
            try { if (catalogo != null) catalogo.stop(); }
            finally { if (http != null) http.stop(0); }
        }
    }
    @Test public void preparaAmbosClientesSinCrearCentralYLosRetiraAlCerrar() throws Exception {
        String base = publicar();
        conConfiguracion("soap.catalogo.wsdl=" + base + "/catalogo?wsdl\nsoap.evaluaciones.wsdl=" + base + "/evaluaciones?wsdl\n", () -> {
            var inicializador = new InicializadorAplicacion();
            inicializador.contextInitialized(new ServletContextEvent(contexto));
            assertNotNull(InicializadorAplicacion.getClienteCatalogo(contexto));
            assertNotNull(InicializadorAplicacion.getClienteEvaluaciones(contexto));
            assertEquals(2, atributos.size());
            assertFalse(atributos.containsKey(InicializadorAplicacion.class.getName() + ".servidor"));
            inicializador.contextDestroyed(new ServletContextEvent(contexto));
            assertTrue(atributos.isEmpty());
        });
    }
    @Test public void falloDelSegundoClienteNoDejaUnArranqueParcial() throws Exception {
        String base = publicar();
        conConfiguracion("soap.catalogo.wsdl=" + base + "/catalogo?wsdl\n", () -> {
            var error = assertThrows(IllegalStateException.class,
                    () -> new InicializadorAplicacion().contextInitialized(new ServletContextEvent(contexto)));
            assertTrue(error.getCause().getMessage().contains("soap.evaluaciones.wsdl"));
            assertTrue(atributos.isEmpty());
        });
    }
    @Test public void configuracionAusenteNoSeSustituyePorUnCentralLocal() throws Exception {
        conConfiguracion("", () -> {
            assertThrows(IllegalStateException.class,
                    () -> new InicializadorAplicacion().contextInitialized(new ServletContextEvent(contexto)));
            assertTrue(atributos.isEmpty());
        });
        assertThrows(IllegalStateException.class, () -> InicializadorAplicacion.getClienteCatalogo(contexto));
        assertThrows(IllegalStateException.class, () -> InicializadorAplicacion.getClienteEvaluaciones(contexto));
    }
}
