package web.soap;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.WebServiceException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import logica.IServidorCentral;
import org.junit.After;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import soap.ServicioInstitutos;
import static org.junit.Assert.*;

/** Usa HTTP y el servicio real, con datos controlados y sin acceder a MySQL. */
public class ClienteInstitutosSoapTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();
    private HttpServer http;
    private Endpoint endpoint;
    private URL wsdl;
    private final AtomicInteger llamadas = new AtomicInteger();

    private ClienteInstitutosSoap publicar(List<String> nombres, boolean fallar) throws Exception {
        IServidorCentral central = (IServidorCentral) Proxy.newProxyInstance(
                IServidorCentral.class.getClassLoader(), new Class<?>[]{IServidorCentral.class},
                (proxy, metodo, argumentos) -> {
                    if (!metodo.getName().equals("listarNombresInstitutos")) {
                        throw new AssertionError(metodo.getName());
                    }
                    llamadas.incrementAndGet();
                    if (fallar) throw new IllegalStateException("Consulta no disponible");
                    return nombres;
                });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioInstitutos(central));
        endpoint.publish(http.createContext("/edext/institutos"));
        http.start();
        wsdl = URI.create("http://127.0.0.1:"
                + http.getAddress().getPort() + "/edext/institutos?wsdl").toURL();
        return new ClienteInstitutosSoap(wsdl);
    }

    @After public void detenerServicio() {
        try {
            if (endpoint != null) endpoint.stop();
        } finally {
            if (http != null) http.stop(0);
        }
    }

    @Test public void obtieneInstitutosPorSoapConTildesYCaracteresXml() throws Exception {
        List<String> nombres = List.of("INCO", "Eléctrica", "Educación & Extensión <Sur>");
        ClienteInstitutosSoap cliente = publicar(nombres, false);
        assertEquals(0, llamadas.get());
        assertEquals(nombres, cliente.listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void devuelveListaVaciaCuandoNoHayInstitutos() throws Exception {
        assertEquals(List.of(), publicar(List.of(), false).listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void comunicaElFalloDelServicioEnLugarDeDevolverListaVacia() throws Exception {
        ClienteInstitutosSoap cliente = publicar(List.of(), true);
        assertThrows(WebServiceException.class, cliente::listarInstitutos);
        assertEquals(1, llamadas.get());
    }

    @Test public void usaLaDireccionDelArchivoExternoParaInvocarElServicio() throws Exception {
        publicar(List.of("INCO", "Eléctrica"), false);
        Path archivo = temporal.newFile("web.properties").toPath();
        Files.writeString(archivo, "# Configuración web\nsoap.institutos.wsdl=" + wsdl + "\n");
        ClienteInstitutosSoap cliente = new ClienteInstitutosSoap(archivo);
        assertEquals(List.of("INCO", "Eléctrica"), cliente.listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void propiedadAusenteOVaciaIndicaQueFaltaConfigurar() throws Exception {
        Path archivo = temporal.newFile("web.properties").toPath();
        for (String contenido : List.of("", "soap.institutos.wsdl=   \n")) {
            Files.writeString(archivo, contenido);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> new ClienteInstitutosSoap(archivo));
            assertTrue(error.getMessage().contains("Falta configurar soap.institutos.wsdl"));
        }
    }

    @Test public void rechazaDireccionesQueNoSeanHttpOHttps() throws Exception {
        Path archivo = temporal.newFile("web.properties").toPath();
        for (String direccion : List.of("archivo.wsdl", "file:///archivo.wsdl", "http:///sin-servidor")) {
            Files.writeString(archivo, "soap.institutos.wsdl=" + direccion);
            assertThrows(IllegalArgumentException.class, () -> new ClienteInstitutosSoap(archivo));
        }
    }

    @Test public void archivoAusenteNoSeOcultaConUnaDireccionPorDefecto() {
        Path archivo = temporal.getRoot().toPath().resolve("inexistente.properties");
        assertThrows(java.nio.file.NoSuchFileException.class, () -> new ClienteInstitutosSoap(archivo));
    }
}
