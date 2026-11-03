package web;

import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.ws.Endpoint;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import logica.Categoria;
import logica.IServidorCentral;
import org.junit.After;
import org.junit.Test;
import soap.ServicioInstitutos;
import web.soap.ClienteInstitutosSoap;
import static org.junit.Assert.*;

/** Comprueba el filtro usando HTTP/SOAP real, sin conectar a MySQL. */
public class NavegacionFilterTest {
    private HttpServer http;
    private Endpoint endpoint;

    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, manejador));
    }

    private ClienteInstitutosSoap publicar(List<String> nombres, AtomicInteger llamadas,
            boolean fallar) throws Exception {
        IServidorCentral remoto = proxy(IServidorCentral.class, (p, m, a) -> {
            if (!m.getName().equals("listarNombresInstitutos")) throw new AssertionError(m.getName());
            llamadas.incrementAndGet();
            if (fallar) throw new IllegalStateException("Servicio no disponible");
            return nombres;
        });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioInstitutos(remoto));
        endpoint.publish(http.createContext("/edext/institutos"));
        http.start();
        return new ClienteInstitutosSoap(URI.create("http://127.0.0.1:"
                + http.getAddress().getPort() + "/edext/institutos?wsdl").toURL());
    }

    @After public void cerrar() {
        try {
            if (endpoint != null) endpoint.stop();
        } finally {
            if (http != null) http.stop(0);
        }
    }

    private static class Escenario {
        final Map<String, Object> contextoDatos = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();
        final List<Categoria> categorias = List.of(new Categoria("Educación"));
        final AtomicInteger consultasCategorias = new AtomicInteger();
        final AtomicInteger peticionesContinuadas = new AtomicInteger();
        final AtomicInteger erroresRegistrados = new AtomicInteger();
        String ruta = "/inicio";

        final IServidorCentral local = proxy(IServidorCentral.class, (p, m, a) -> {
            // La consulta local de institutos ya no debe utilizarse.
            if (!m.getName().equals("listarCategorias")) throw new AssertionError(m.getName());
            consultasCategorias.incrementAndGet();
            return categorias;
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> {
            if (m.getName().equals("log")) {
                erroresRegistrados.incrementAndGet();
                return null;
            }
            if (!m.getName().equals("getAttribute")) throw new AssertionError(m.getName());
            return contextoDatos.get(a[0]);
        });
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getServletPath" -> ruta;
            case "getServletContext" -> contexto;
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final ServletResponse response = proxy(ServletResponse.class, (p, m, a) -> {
            throw new AssertionError(m.getName());
        });
        final FilterChain siguiente = (request, response) -> peticionesContinuadas.incrementAndGet();

        Escenario(ClienteInstitutosSoap cliente) {
            contextoDatos.put(InicializadorAplicacion.class.getName() + ".servidor", local);
            contextoDatos.put(InicializadorAplicacion.class.getName() + ".clienteInstitutos", cliente);
        }
        void ejecutar() throws Exception {
            new NavegacionFilter().doFilter(request, response, siguiente);
        }
    }

    @Test public void institutosDelMenuVienenDeSoapYCategoriasSiguenLocales() throws Exception {
        var llamadas = new AtomicInteger();
        List<String> nombres = List.of("INCO", "Eléctrica", "Educación & Extensión");
        var s = new Escenario(publicar(nombres, llamadas, false));
        s.ejecutar();
        assertEquals(nombres, s.atributos.get("navInstitutos"));
        assertSame(s.categorias, s.atributos.get("navCategorias"));
        assertEquals(1, llamadas.get());
        assertEquals(1, s.consultasCategorias.get());
        assertEquals(1, s.peticionesContinuadas.get());
        assertFalse(s.atributos.containsKey("errorInstitutos"));
    }

    @Test public void recursosExcluidosNoNecesitanClienteNiConsultanDatos() throws Exception {
        var s = new Escenario(null);
        for (String ruta : List.of("/css/estilos.css", "/js/perfil.js", "/imagenes/avatar.svg", "/imagen-usuario")) {
            s.ruta = ruta;
            s.ejecutar();
        }
        assertTrue(s.atributos.isEmpty());
        assertEquals(0, s.consultasCategorias.get());
        assertEquals(4, s.peticionesContinuadas.get());
    }

    @Test public void errorSoapNoSeOcultaConUnaConsultaLocalDeInstitutos() throws Exception {
        var llamadas = new AtomicInteger();
        var s = new Escenario(publicar(List.of(), llamadas, true));
        s.ejecutar();
        assertEquals(1, llamadas.get());
        assertEquals(1, s.peticionesContinuadas.get());
        assertEquals(List.of(), s.atributos.get("navInstitutos"));
        assertEquals("No se pudieron cargar los institutos. Intentá nuevamente más tarde.",
                s.atributos.get("errorInstitutos"));
        assertEquals(1, s.erroresRegistrados.get());
        assertSame(s.categorias, s.atributos.get("navCategorias"));
    }

    @Test public void centralApagadoMuestraAvisoYPermiteContinuarLaPagina() throws Exception {
        var s = new Escenario(publicar(List.of("INCO"), new AtomicInteger(), false));
        cerrar();
        s.ejecutar();
        assertEquals(List.of(), s.atributos.get("navInstitutos"));
        assertNotNull(s.atributos.get("errorInstitutos"));
        assertEquals(1, s.erroresRegistrados.get());
        assertEquals(1, s.peticionesContinuadas.get());
    }
}
