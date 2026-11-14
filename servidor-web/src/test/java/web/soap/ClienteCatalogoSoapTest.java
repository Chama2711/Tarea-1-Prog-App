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
import soap.ServicioCatalogo;
import static org.junit.Assert.*;

/** Usa HTTP y el servicio real, con datos controlados y sin acceder a MySQL. */
public class ClienteCatalogoSoapTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();
    private HttpServer http;
    private Endpoint endpoint;
    private URL wsdl;
    private final AtomicInteger llamadas = new AtomicInteger();
    private String docenteConsultado;
    private String nombreConsultado;
    private soap.dto.DTCursoConsulta detalleCurso = new soap.dto.DTCursoConsulta(
            "Robótica & Educación", "Descripción <inicial>", "Un mes", 20, 3, "https://example.test",
            java.time.LocalDate.of(2026, 11, 4), "Eléctrica", true, List.of("Previa"),
            List.of("Tecnología"), List.of("Programa ñ"), List.of(new soap.dto.DTEdicionResumen(
                    "Edición 2026", java.time.LocalDate.of(2026, 11, 5),
                    java.time.LocalDate.of(2026, 12, 5), -1, false)));
    private byte[] imagenCurso = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0, 1};

    private ClienteCatalogoSoap publicar(List<String> nombres, boolean fallar) throws Exception {
        IServidorCentral central = (IServidorCentral) Proxy.newProxyInstance(
                IServidorCentral.class.getClassLoader(), new Class<?>[]{IServidorCentral.class},
                (proxy, metodo, argumentos) -> {
                    if (metodo.getName().equals("consultarCurso") || metodo.getName().equals("obtenerImagenCurso")) {
                        nombreConsultado = (String) argumentos[0];
                        llamadas.incrementAndGet();
                        if (fallar) throw new IllegalStateException("Consulta no disponible");
                        return metodo.getName().equals("consultarCurso") ? detalleCurso : imagenCurso;
                    }
                    if (metodo.getName().equals("listarNombresCursosPorInstituto")) {
                        nombreConsultado = (String) argumentos[0];
                    } else if (metodo.getName().equals("listarNombresCursos")) {
                        // Listado general sin parámetros.
                    } else if (metodo.getName().equals("listarCategoriasCurso")) {
                        nombreConsultado = (String) argumentos[0];
                        llamadas.incrementAndGet();
                        if (fallar) throw new IllegalStateException("Consulta no disponible");
                        return nombres.stream().map(logica.Categoria::new).toList();
                    }
                    if (metodo.getName().equals("listarNombresCursos")
                            || metodo.getName().equals("listarNombresCursosPorInstituto")) {
                        // Se responde con la lista controlada de la prueba.
                    } else if (metodo.getName().equals("listarNombresCategoriasPrograma")) {
                        nombreConsultado = (String) argumentos[0];
                    } else if (metodo.getName().equals("listarNombresCategorias")) {
                        // Consulta general sin parámetros.
                    } else if (metodo.getName().equals("listarNombresInstitutosDocente")) {
                        docenteConsultado = (String) argumentos[0];
                    } else if (!metodo.getName().equals("listarNombresInstitutos")) {
                        throw new AssertionError(metodo.getName());
                    }
                    llamadas.incrementAndGet();
                    if (fallar) throw new IllegalStateException("Consulta no disponible");
                    return nombres;
                });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioCatalogo(central));
        endpoint.publish(http.createContext("/edext/catalogo"));
        http.start();
        wsdl = URI.create("http://127.0.0.1:"
                + http.getAddress().getPort() + "/edext/catalogo?wsdl").toURL();
        return new ClienteCatalogoSoap(wsdl);
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
        ClienteCatalogoSoap cliente = publicar(nombres, false);
        assertEquals(0, llamadas.get());
        assertEquals(nombres, cliente.listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void devuelveListaVaciaCuandoNoHayInstitutos() throws Exception {
        assertEquals(List.of(), publicar(List.of(), false).listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void consultaInstitutosDelDocentePorSoapConSuNickname() throws Exception {
        List<String> nombres = List.of("Eléctrica", "Educación & Extensión");
        ClienteCatalogoSoap cliente = publicar(nombres, false);
        assertEquals(nombres, cliente.listarInstitutosDocente("docente_ñ"));
        assertEquals("docente_ñ", docenteConsultado);
        assertEquals(1, llamadas.get());
    }

    @Test public void docenteSinInstitutosRecibeListaVacia() throws Exception {
        assertEquals(List.of(), publicar(List.of(), false).listarInstitutosDocente("docente"));
    }

    @Test public void falloDeConsultaDocenteSeComunicaAlCliente() throws Exception {
        ClienteCatalogoSoap cliente = publicar(List.of(), true);
        assertThrows(WebServiceException.class, () -> cliente.listarInstitutosDocente("inexistente"));
    }

    @Test public void comunicaElFalloDelServicioEnLugarDeDevolverListaVacia() throws Exception {
        ClienteCatalogoSoap cliente = publicar(List.of(), true);
        assertThrows(WebServiceException.class, cliente::listarInstitutos);
        assertEquals(1, llamadas.get());
    }

    @Test public void usaLaDireccionDelArchivoExternoParaInvocarElServicio() throws Exception {
        publicar(List.of("INCO", "Eléctrica"), false);
        Path archivo = temporal.newFile("web.properties").toPath();
        Files.writeString(archivo, "# Configuración web\nsoap.catalogo.wsdl=" + wsdl + "\n");
        ClienteCatalogoSoap cliente = new ClienteCatalogoSoap(archivo);
        assertEquals(List.of("INCO", "Eléctrica"), cliente.listarInstitutos());
        assertEquals(1, llamadas.get());
    }

    @Test public void propiedadAusenteOVaciaIndicaQueFaltaConfigurar() throws Exception {
        Path archivo = temporal.newFile("web.properties").toPath();
        for (String contenido : List.of("", "soap.catalogo.wsdl=   \n")) {
            Files.writeString(archivo, contenido);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> new ClienteCatalogoSoap(archivo));
            assertTrue(error.getMessage().contains("Falta configurar soap.catalogo.wsdl"));
        }
    }

    @Test public void rechazaDireccionesQueNoSeanHttpOHttps() throws Exception {
        Path archivo = temporal.newFile("web.properties").toPath();
        for (String direccion : List.of("archivo.wsdl", "file:///archivo.wsdl", "http:///sin-servidor")) {
            Files.writeString(archivo, "soap.catalogo.wsdl=" + direccion);
            assertThrows(IllegalArgumentException.class, () -> new ClienteCatalogoSoap(archivo));
        }
    }

    @Test public void archivoAusenteNoSeOcultaConUnaDireccionPorDefecto() {
        Path archivo = temporal.getRoot().toPath().resolve("inexistente.properties");
        assertThrows(java.nio.file.NoSuchFileException.class, () -> new ClienteCatalogoSoap(archivo));
    }
    @Test public void categoriasGeneralesConTildesYXmlViajanPorSoap() throws Exception {
        var nombres = List.of("Educación", "Ciencia & Tecnología <Sur>");
        assertEquals(nombres, publicar(nombres, false).listarCategorias());
    }

    @Test public void categoriasDeCursoYProgramaUsanElNombreSolicitado() throws Exception {
        var nombres = List.of("Robótica", "Tecnología");
        var cliente = publicar(nombres, false);
        assertEquals(nombres, cliente.listarCategoriasCurso("Curso\n& ñ"));
        assertEquals("Curso\n& ñ", nombreConsultado);
        assertEquals(nombres, cliente.listarCategoriasPrograma("Programa ñ"));
        assertEquals("Programa ñ", nombreConsultado);
    }

    @Test public void categoriasSinResultadosSonListasVacias() throws Exception {
        var cliente = publicar(List.of(), false);
        assertEquals(List.of(), cliente.listarCategorias());
        assertEquals(List.of(), cliente.listarCategoriasCurso("Curso"));
        assertEquals(List.of(), cliente.listarCategoriasPrograma("Programa"));
    }

    @Test public void erroresDeCategoriasNoSeDisfrazanComoListasVacias() throws Exception {
        var cliente = publicar(List.of(), true);
        assertThrows(WebServiceException.class, cliente::listarCategorias);
        assertThrows(WebServiceException.class, () -> cliente.listarCategoriasCurso("Curso"));
        assertThrows(WebServiceException.class, () -> cliente.listarCategoriasPrograma("Programa"));
    }
    @Test public void clienteGeneradoConsultaListasDeCursosYFiltroPorInstituto() throws Exception {
        var nombres = List.of("Robótica & Educación", "Curso <Inicial>");
        var cliente = publicar(nombres, false);
        assertEquals(nombres, cliente.listarCursos());
        assertEquals(nombres, cliente.listarCursosPorInstituto("Eléctrica & Extensión"));
        assertEquals("Eléctrica & Extensión", nombreConsultado);
    }

    @Test public void clienteGeneradoRecibeDetalleConFechasYRelaciones() throws Exception {
        var cliente = publicar(List.of(), false);
        var curso = cliente.consultarCurso("Robótica & Educación");
        assertEquals("Robótica & Educación", nombreConsultado);
        assertEquals(detalleCurso.getNombre(), curso.getNombre());
        assertEquals(detalleCurso.getDescripcion(), curso.getDescripcion());
        assertEquals("Un mes", curso.getDuracion()); assertEquals(20, curso.getCantidadHoras());
        assertEquals(3, curso.getCreditos()); assertEquals("https://example.test", curso.getUrl());
        assertEquals("2026-11-04", curso.getFechaRegistro());
        assertEquals("Eléctrica", curso.getNombreInstituto()); assertTrue(curso.isTieneImagen());
        assertEquals(List.of("Previa"), curso.getPrevia());
        assertEquals(List.of("Tecnología"), curso.getCategoria());
        assertEquals(List.of("Programa ñ"), curso.getPrograma());
        assertEquals("Edición 2026", curso.getEdicion().get(0).getNombre());
        assertEquals("2026-11-05", curso.getEdicion().get(0).getFechaInicio());
        assertEquals("2026-12-05", curso.getEdicion().get(0).getFechaFin());
        assertEquals(-1, curso.getEdicion().get(0).getCupo());
    }

    @Test public void clienteGeneradoRecibeLosBytesDeLaImagen() throws Exception {
        var cliente = publicar(List.of(), false);
        assertArrayEquals(imagenCurso, cliente.obtenerImagenCurso("Curso ñ"));
        assertEquals("Curso ñ", nombreConsultado);
    }

    @Test public void ausenciaDeCursosOImagenesSeConservaEnElCliente() throws Exception {
        detalleCurso = null; imagenCurso = null;
        var cliente = publicar(List.of(), false);
        assertEquals(List.of(), cliente.listarCursos());
        assertEquals(List.of(), cliente.listarCursosPorInstituto("Sin cursos"));
        assertNull(cliente.consultarCurso("No existe"));
        assertNull(cliente.obtenerImagenCurso("Sin imagen"));
    }

    @Test public void fallosDeLasOperacionesDeCursoNoSeOcultan() throws Exception {
        var cliente = publicar(List.of(), true);
        assertThrows(WebServiceException.class, cliente::listarCursos);
        assertThrows(WebServiceException.class, () -> cliente.listarCursosPorInstituto("INCO"));
        assertThrows(WebServiceException.class, () -> cliente.consultarCurso("Curso"));
        assertThrows(WebServiceException.class, () -> cliente.obtenerImagenCurso("Curso"));
    }

    @Test public void cursoUsaLaDireccionConfiguradaEnElArchivoExterno() throws Exception {
        publicar(List.of("Curso ñ"), false);
        Path archivo = temporal.newFile("cliente-cursos.properties").toPath();
        Files.writeString(archivo, "soap.catalogo.wsdl=" + wsdl);
        var cliente = new ClienteCatalogoSoap(archivo);
        assertEquals(List.of("Curso ñ"), cliente.listarCursos());
        assertEquals(detalleCurso.getNombre(), cliente.consultarCurso("Curso ñ").getNombre());
    }
}
