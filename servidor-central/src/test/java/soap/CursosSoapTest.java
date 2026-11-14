package soap;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import logica.IServidorCentral;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/** Verifica las operaciones publicadas sin generar todavía el cliente de la web. */
public class CursosSoapTest {
    private HttpServer http;
    private Endpoint endpoint;
    private Service servicio;
    private List<String> nombres = List.of("Robótica & Educación", "Curso <Inicial>");
    private String institutoConsultado;
    private boolean fallar;
    private String cursoConsultado;
    private byte[] imagen = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0, 1, 2};
    private soap.dto.DTCursoConsulta detalle = new soap.dto.DTCursoConsulta(
            "Robótica & Educación", "Texto <inicial>", "Un mes", 20, 3,
            "https://example.test", java.time.LocalDate.of(2026, 11, 4), "Eléctrica", true,
            List.of("Previa"), List.of("Tecnología"), List.of("Programa ñ"),
            List.of(new soap.dto.DTEdicionResumen("Edición 2026", java.time.LocalDate.of(2026, 11, 5),
                    java.time.LocalDate.of(2026, 12, 5), -1, false)));
    private static final String NS = "http://soap.edext/";

    private void publicar() throws Exception {
        IServidorCentral central = (IServidorCentral) Proxy.newProxyInstance(
                IServidorCentral.class.getClassLoader(), new Class<?>[]{IServidorCentral.class},
                (p, m, a) -> {
                    if (m.getName().equals("consultarCurso") || m.getName().equals("obtenerImagenCurso")) {
                        cursoConsultado = (String) a[0];
                        if (fallar) throw new java.io.IOException("ruta privada del servidor");
                        return m.getName().equals("consultarCurso") ? detalle : imagen;
                    }
                    if (m.getName().equals("listarNombresCursosPorInstituto")) {
                        institutoConsultado = (String) a[0];
                    } else if (!m.getName().equals("listarNombresCursos")) {
                        throw new AssertionError(m.getName());
                    }
                    if (fallar) throw new IllegalStateException("Consulta no disponible");
                    return nombres;
                });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioCatalogo(central));
        endpoint.publish(http.createContext("/servicio"));
        http.start();
        var wsdl = URI.create("http://127.0.0.1:" + http.getAddress().getPort() + "/servicio?wsdl").toURL();
        String contrato;
        try (var entrada = wsdl.openStream()) {
            contrato = new String(entrada.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
        assertTrue(contrato.contains("operation name=\"listarCursos\""));
        assertTrue(contrato.contains("operation name=\"listarCursosPorInstituto\""));
        assertTrue(contrato.contains("operation name=\"consultarCurso\""));
        assertTrue(contrato.contains("operation name=\"obtenerImagenCurso\""));
        servicio = Service.create(wsdl, new QName(NS, "CatalogoService"));
    }

    private SOAPMessage invocar(String operacion, String parametro, String valor) throws Exception {
        SOAPMessage peticion = MessageFactory.newInstance().createMessage();
        var operacionXml = peticion.getSOAPBody().addChildElement(new QName(NS, operacion, "svc"));
        if (valor != null) operacionXml.addChildElement(parametro).addTextNode(valor);
        peticion.saveChanges();
        var puerto = servicio.createDispatch(new QName(NS, "CatalogoPort"), SOAPMessage.class, Service.Mode.MESSAGE);
        return puerto.invoke(peticion);
    }

    private List<String> consultar(String operacion, String instituto) throws Exception {
        var respuesta = invocar(operacion, "nombreInstituto", instituto);
        var elementos = respuesta.getSOAPBody().getElementsByTagName("return");
        List<String> resultado = new ArrayList<>();
        for (int i = 0; i < elementos.getLength(); i++) resultado.add(elementos.item(i).getTextContent());
        return resultado;
    }

    @After public void cerrar() {
        try { if (endpoint != null) endpoint.stop(); }
        finally { if (http != null) http.stop(0); }
    }

    @Test public void listadoGeneralPublicadoConservaTildesYCaracteresXml() throws Exception {
        publicar();
        assertEquals(nombres, consultar("listarCursos", null));
    }

    @Test public void filtroEnviaElInstitutoExactoAlCentral() throws Exception {
        publicar();
        assertEquals(nombres, consultar("listarCursosPorInstituto", "Eléctrica & Extensión"));
        assertEquals("Eléctrica & Extensión", institutoConsultado);
    }

    @Test public void ambasOperacionesDevuelvenListaVaciaSinResultados() throws Exception {
        nombres = List.of(); publicar();
        assertEquals(List.of(), consultar("listarCursos", null));
        assertEquals(List.of(), consultar("listarCursosPorInstituto", "Sin cursos"));
    }

    @Test public void falloDelCentralSeComunicaComoFalloSoap() throws Exception {
        fallar = true; publicar();
        assertThrows(SOAPFaultException.class, () -> consultar("listarCursos", null));
        assertThrows(SOAPFaultException.class, () -> consultar("listarCursosPorInstituto", "INCO"));
    }
    @Test public void detalleViajaComoDtoCompletoEnSoap() throws Exception {
        publicar();
        var respuesta = invocar("consultarCurso", "nombreCurso", "Robótica & Educación");
        var elemento = respuesta.getSOAPBody().getElementsByTagName("return").item(0);
        var recibido = jakarta.xml.bind.JAXBContext.newInstance(soap.dto.DTCursoConsulta.class)
                .createUnmarshaller().unmarshal(elemento, soap.dto.DTCursoConsulta.class).getValue();
        assertEquals("Robótica & Educación", cursoConsultado);
        assertEquals(detalle.getNombre(), recibido.getNombre());
        assertEquals(detalle.getDescripcion(), recibido.getDescripcion());
        assertEquals(detalle.getFechaRegistro(), recibido.getFechaRegistro());
        assertEquals(detalle.getNombreInstituto(), recibido.getNombreInstituto());
        assertEquals(detalle.getPrevias(), recibido.getPrevias());
        assertEquals(detalle.getCategorias(), recibido.getCategorias());
        assertEquals(detalle.getProgramas(), recibido.getProgramas());
        assertEquals(-1, recibido.getEdiciones().get(0).getCupo());
        assertEquals(detalle.getEdiciones().get(0).getFechaInicio(), recibido.getEdiciones().get(0).getFechaInicio());
    }

    @Test public void imagenViajaPorSoapComoBytesBase64() throws Exception {
        publicar();
        var respuesta = invocar("obtenerImagenCurso", "nombreCurso", "Curso ñ");
        var texto = respuesta.getSOAPBody().getElementsByTagName("return").item(0).getTextContent();
        assertArrayEquals(imagen, java.util.Base64.getMimeDecoder().decode(texto));
        assertEquals("Curso ñ", cursoConsultado);
    }

    @Test public void cursoInexistenteOImagenAusenteDevuelvenAusencia() throws Exception {
        detalle = null; imagen = null; publicar();
        for (String operacion : List.of("consultarCurso", "obtenerImagenCurso")) {
            var respuesta = invocar(operacion, "nombreCurso", "Sin resultado");
            var resultado = respuesta.getSOAPBody().getElementsByTagName("return");
            assertTrue(resultado.getLength() == 0 || ((org.w3c.dom.Element) resultado.item(0))
                    .getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "nil").equals("true"));
        }
    }

    @Test public void falloDeImagenSeComunicaSinExponerRutasPrivadas() throws Exception {
        fallar = true; publicar();
        var error = assertThrows(SOAPFaultException.class,
                () -> invocar("obtenerImagenCurso", "nombreCurso", "Curso"));
        assertEquals("No se pudo consultar la imagen del curso.", error.getFault().getFaultString());
        assertFalse(error.getMessage().contains("ruta privada"));
    }
}
