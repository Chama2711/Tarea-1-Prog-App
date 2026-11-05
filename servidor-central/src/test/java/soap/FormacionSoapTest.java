package soap;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Proxy;
import java.net.*;
import java.time.LocalDate;
import java.util.List;
import javax.xml.namespace.QName;
import logica.*;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class FormacionSoapTest {
    private HttpServer http;
    private Endpoint endpoint;
    private Service servicio;
    private boolean ausente, fallar;
    private final LocalDate fecha = LocalDate.of(2026, 11, 5);
    private final byte[] imagen = new byte[]{1, 2, 3, 4};
    private static final String NS = "http://soap.edext/";

    private void publicar() throws Exception {
        var central = (IServidorCentral) Proxy.newProxyInstance(IServidorCentral.class.getClassLoader(),
                new Class<?>[]{IServidorCentral.class}, (p, m, a) -> {
                    if (fallar) throw new java.io.IOException("ruta privada");
                    var programa = new soap.dto.DTProgramaConsulta(17L, "Programa ñ", "Texto <&>",
                            fecha, fecha.plusDays(30), fecha.minusDays(1), true,
                            List.of(new soap.dto.DTCursoResumen("Curso ñ", "Descripción")), List.of("Tecnología"));
                    return switch (m.getName()) {
                        case "listarProgramasConsulta" -> ausente ? List.of() : List.of(programa);
                        case "consultarPrograma" -> ausente ? null : programa;
                        case "consultarEdicion" -> ausente ? null : new DTEdicionConsulta("Edición ñ", "Curso ñ",
                                fecha, fecha.plusDays(30), fecha.minusDays(1), -1, true, true,
                                List.of(new DTUsuarioConsulta("ana", "Ana", "Pérez", "correo-privado", null,
                                        DTAutenticacion.Rol.DOCENTE, false, List.of())));
                        case "obtenerImagenPrograma", "obtenerImagenEdicion" -> ausente ? null : imagen;
                        case "estaInscriptoAPrograma" -> {
                            assertEquals("estudiante_ñ", a[0]); assertEquals(17L, a[1]); yield true;
                        }
                        default -> throw new AssertionError(m.getName());
                    };
                });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioCatalogo(central));
        endpoint.publish(http.createContext("/catalogo")); http.start();
        servicio = Service.create(URI.create("http://127.0.0.1:" + http.getAddress().getPort()
                + "/catalogo?wsdl").toURL(), new QName(NS, "CatalogoService"));
    }

    private SOAPMessage invocar(String operacion, String... parametros) throws Exception {
        var peticion = MessageFactory.newInstance().createMessage();
        var op = peticion.getSOAPBody().addChildElement(new QName(NS, operacion, "svc"));
        for (int i = 0; i < parametros.length; i += 2) op.addChildElement(parametros[i]).addTextNode(parametros[i + 1]);
        peticion.saveChanges();
        return servicio.createDispatch(new QName(NS, "CatalogoPort"), SOAPMessage.class, Service.Mode.MESSAGE).invoke(peticion);
    }

    private <T> T leer(SOAPMessage mensaje, Class<T> tipo) throws Exception {
        return JAXBContext.newInstance(tipo).createUnmarshaller().unmarshal(
                mensaje.getSOAPBody().getElementsByTagName("return").item(0), tipo).getValue();
    }

    @After public void cerrar() {
        try { if (endpoint != null) endpoint.stop(); }
        finally { if (http != null) http.stop(0); }
    }

    @Test public void programasViajanConRelacionesYFechasSinEntidadesNiRutas() throws Exception {
        publicar();
        for (String operacion : List.of("listarProgramas", "consultarPrograma")) {
            var dto = leer(invocar(operacion, operacion.equals("consultarPrograma")
                    ? new String[]{"nombrePrograma", "Programa ñ"} : new String[]{}), soap.dto.DTProgramaConsulta.class);
            assertEquals(Long.valueOf(17), dto.getId()); assertEquals("Programa ñ", dto.getNombre());
            assertEquals("Texto <&>", dto.getDescripcion()); assertEquals(fecha, dto.getFechaInicio());
            assertEquals(fecha.plusDays(30), dto.getFechaFin()); assertEquals(fecha.minusDays(1), dto.getFechaAlta());
            assertTrue(dto.isTieneImagen()); assertEquals(List.of("Tecnología"), dto.getCategorias());
            assertEquals("Curso ñ", dto.getCursos().get(0).getNombre());
            assertEquals("Descripción", dto.getCursos().get(0).getDescripcion());
        }
        assertEquals("true", invocar("estaInscriptoAPrograma", "nickEstudiante", "estudiante_ñ", "programaId", "17")
                .getSOAPBody().getElementsByTagName("return").item(0).getTextContent());
    }

    @Test public void edicionViajaConDocentesPublicosYConservaSusDatos() throws Exception {
        publicar(); var respuesta = invocar("consultarEdicion", "nombreEdicion", "Edición ñ");
        var dto = leer(respuesta, soap.dto.DTEdicionConsulta.class);
        assertEquals("Edición ñ", dto.getNombre()); assertEquals("Curso ñ", dto.getNombreCurso());
        assertEquals(fecha, dto.getInicio()); assertEquals(fecha.plusDays(30), dto.getFin());
        assertEquals(fecha.minusDays(1), dto.getPublicacion()); assertEquals(-1, dto.getCupo());
        assertTrue(dto.isVigente()); assertTrue(dto.isTieneImagen());
        assertEquals("ana", dto.getDocentes().get(0).getNick());
        assertEquals("Ana Pérez", dto.getDocentes().get(0).getNombreCompleto());
        assertFalse(respuesta.getSOAPBody().getTextContent().contains("correo-privado"));
    }

    @Test public void imagenesViajanEnBase64YFallosNoExponenRutas() throws Exception {
        publicar();
        for (String operacion : List.of("obtenerImagenPrograma", "obtenerImagenEdicion")) {
            String parametro = operacion.endsWith("Programa") ? "nombrePrograma" : "nombreEdicion";
            var respuesta = invocar(operacion, parametro, "Formación ñ");
            assertArrayEquals(imagen, java.util.Base64.getDecoder().decode(respuesta.getSOAPBody()
                    .getElementsByTagName("return").item(0).getTextContent()));
            fallar = true;
            var error = assertThrows(SOAPFaultException.class, () -> invocar(operacion, parametro, "Formación ñ"));
            assertFalse(error.getMessage().contains("ruta privada")); fallar = false;
        }
    }

    @Test public void ausenciaDeDatosConservaListaVaciaODetalleNulo() throws Exception {
        ausente = true; publicar();
        assertEquals(0, invocar("listarProgramas").getSOAPBody().getElementsByTagName("return").getLength());
        for (String operacion : List.of("consultarPrograma", "consultarEdicion")) {
            var nodos = invocar(operacion).getSOAPBody().getElementsByTagName("return");
            assertTrue(nodos.getLength() == 0 || ((org.w3c.dom.Element) nodos.item(0))
                    .getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "nil").equals("true"));
        }
    }
}
