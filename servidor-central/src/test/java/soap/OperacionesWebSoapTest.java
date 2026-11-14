package soap;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.soap.*;
import jakarta.xml.ws.*;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Proxy;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import javax.xml.namespace.QName;
import logica.*;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/** Comprueba el contrato HTTP/SOAP de usuarios, acciones y consultas auxiliares. */
public class OperacionesWebSoapTest {
    private HttpServer http;
    private Endpoint endpoint;
    private Service servicio;
    private Exception fallo;
    private boolean docente = true;
    private final Map<String, Object[]> llamadas = new HashMap<>();
    private final LocalDate fecha = LocalDate.of(2026, 11, 5);
    private static final String NS = "http://soap.edext/";
    private DTUsuarioConsulta usuario() {
        return new DTUsuarioConsulta("ana", "Ana", "Pérez", "ana@test", fecha.minusYears(25),
                DTAutenticacion.Rol.DOCENTE, true, List.of("INCO"));
    }
    private DTInscripcion inscripcion() {
        return new DTInscripcion(17L, "estudiante", "Estudiante ñ", "Edición ñ", "Curso", fecha, EstadoInscripcion.ACEPTADA);
    }
    private DTEdicionConsulta edicion() {
        return new DTEdicionConsulta("Edición ñ", "Curso", fecha, fecha.plusDays(30), fecha.minusDays(1), -1, true, true, List.of(usuario()));
    }
    private void publicar() throws Exception {
        var central = (IServidorCentral) Proxy.newProxyInstance(IServidorCentral.class.getClassLoader(), new Class<?>[]{IServidorCentral.class},
                (p, m, a) -> {
                    llamadas.put(m.getName(), a);
                    if (fallo != null) throw fallo;
                    return switch (m.getName()) {
                        case "obtenerUsuario" -> docente ? new Docente() : new Estudiante();
                        case "autenticarUsuario" -> new DTAutenticacion("ana", "Ana", DTAutenticacion.Rol.DOCENTE);
                        case "listarUsuariosConsulta", "listarSeguidosConsulta" -> List.of(usuario());
                        case "esSeguidor" -> true;
                        case "obtenerImagenUsuario" -> new byte[]{1, 2, 3};
                        case "consultarPerfilUsuario" -> DTPerfilUsuario.crear(usuario(), (String) a[1], List.of(), List.of(edicion()), List.of(), List.of(inscripcion()));
                        case "listarNombresProgramas" -> List.of("Programa ñ");
                        case "listarNicknamesDocentesPorInstituto", "listarEdicionesDocente" -> List.of("ana");
                        case "listarEdicionesParaSeleccion" -> List.of(edicion());
                        case "listarAceptadosEdicion" -> List.of(inscripcion());
                        case "listarInscripcionesParaSeleccion" -> List.of(new DTRankingInscripcion(inscripcion(), 3));
                        case "registrarEstudiante", "registrarDocente", "modificarUsuario", "seguirUsuario", "dejarDeSeguirUsuario",
                             "altaCursoDocente", "altaEdicionCursoDocente", "altaProgramaFormacion", "agregarCursoAPrograma",
                             "inscribirEstudianteAEdicion", "inscribirEstudianteAPrograma", "seleccionarEstudiantes" -> null;
                        default -> throw new AssertionError(m.getName());
                    };
                });
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioCatalogo(central));
        endpoint.publish(http.createContext("/catalogo")); http.start();
        servicio = Service.create(URI.create("http://127.0.0.1:" + http.getAddress().getPort() + "/catalogo?wsdl").toURL(), new QName(NS, "CatalogoService"));
    }
    private SOAPMessage invocar(String nombre, Object... parametros) throws Exception {
        var mensaje = MessageFactory.newInstance().createMessage();
        var op = mensaje.getSOAPBody().addChildElement(new QName(NS, nombre, "svc"));
        for (int i = 0; i < parametros.length; i += 2) {
            String clave = (String) parametros[i]; Object valor = parametros[i + 1];
            if (valor instanceof List<?> valores) {
                for (Object v : valores) op.addChildElement(clave).addTextNode(v.toString());
            } else if (valor instanceof Map<?, ?> decisiones) {
                for (var d : decisiones.entrySet()) {
                    var nodo = op.addChildElement(clave);
                    nodo.addChildElement("id").addTextNode(d.getKey().toString());
                    nodo.addChildElement("estado").addTextNode(d.getValue().toString());
                }
            } else if (valor != null) op.addChildElement(clave).addTextNode(valor.toString());
        }
        mensaje.saveChanges();
        return servicio.createDispatch(new QName(NS, "CatalogoPort"), SOAPMessage.class, Service.Mode.MESSAGE).invoke(mensaje);
    }
    private <T> T leer(SOAPMessage respuesta, Class<T> tipo) throws Exception {
        return jakarta.xml.bind.JAXBContext.newInstance(tipo).createUnmarshaller().unmarshal(
                respuesta.getSOAPBody().getElementsByTagName("return").item(0), tipo).getValue();
    }
    @After public void cerrar() {
        try { if (endpoint != null) endpoint.stop(); }
        finally { if (http != null) http.stop(0); }
    }

    @Test public void autenticacionYUsuariosViajanSinContrasenas() throws Exception {
        publicar(); var respuesta = invocar("autenticarUsuario", "identificador", "ana@test", "clave", "secreto-no-retornado");
        var identidad = leer(respuesta, soap.dto.DTIdentidad.class);
        assertEquals("ana", identidad.nick); assertEquals("Ana", identidad.nombre); assertEquals("DOCENTE", identidad.rol);
        assertFalse(respuesta.getSOAPBody().getTextContent().contains("secreto-no-retornado"));
        for (String operacion : List.of("listarUsuariosConsulta", "listarSeguidosConsulta")) {
            var dto = leer(invocar(operacion, "nick", "ana"), soap.dto.DTUsuarioConsulta.class);
            assertEquals("ana", dto.nick); assertEquals("Pérez", dto.apellido); assertEquals(fecha.minusYears(25), dto.fechaNacimiento);
            assertEquals(List.of("INCO"), dto.institutos); assertTrue(dto.tieneImagen);
        }
        assertEquals("true", invocar("esSeguidor", "nickSeguidor", "ana", "nickSeguido", "ben").getSOAPBody().getTextContent());
        assertEquals("AQID", invocar("obtenerImagenUsuario", "nick", "ana").getSOAPBody().getTextContent());
    }

    @Test public void perfilPreservaEdicionesYPrivacidadDelListadoDeAceptados() throws Exception {
        publicar();
        var propio = leer(invocar("consultarPerfilUsuario", "nick", "ana", "nickConsultante", "ana"), soap.dto.DTPerfilConsulta.class);
        assertTrue(propio.propio); assertEquals(1, propio.aceptados.size());
        assertEquals("Edición ñ", propio.ediciones.get(0).getNombre());
        assertEquals("Ana Pérez", propio.ediciones.get(0).getDocentes().get(0).getNombreCompleto());
        var ajeno = leer(invocar("consultarPerfilUsuario", "nick", "ana", "nickConsultante", "otro"), soap.dto.DTPerfilConsulta.class);
        assertFalse(ajeno.propio); assertTrue(ajeno.aceptados.isEmpty());
    }

    @Test public void registrosYModificacionConservanFechasImagenYConfirmacion() throws Exception {
        publicar();
        for (String op : List.of("registrarEstudiante", "registrarDocente")) {
            invocar(op, "nick", "ana", "correo", "ana@test", "nombre", "Ana", "apellido", "Pérez", "fechaNacimiento", "2000-01-02",
                    "instituto", "INCO", "clave", "secreto", "confirmacion", "secreto", "imagen", "AQID");
            var args = llamadas.get(op); assertEquals(LocalDate.of(2000, 1, 2), args[4]);
            assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) args[args.length - 1]);
        }
        invocar("modificarUsuario", "nick", "ana", "nombre", "Ana", "apellido", "Pérez", "fechaNacimiento", "2000-01-02", "clave", "", "confirmacion", "");
        assertEquals(LocalDate.of(2000, 1, 2), llamadas.get("modificarUsuario")[3]);
        for (String op : List.of("seguirUsuario", "dejarDeSeguirUsuario")) {
            invocar(op, "nickSeguidor", "ana", "nickSeguido", "ben");
            assertArrayEquals(new Object[]{"ana", "ben"}, llamadas.get(op));
        }
    }

    @Test public void altasConservanCupoFechasListasEImagenes() throws Exception {
        publicar();
        invocar("altaCursoDocente", "nickDocente", "ana", "instituto", "INCO", "nombre", "Curso & ñ", "descripcion", "Descripción", "duracion", "Un mes", "horas", 20,
                "creditos", 3, "url", "https://example.test", "previas", List.of("Previa"), "categorias", List.of("Tecnología"), "imagen", "AQID");
        var curso = llamadas.get("altaCursoDocente"); assertEquals("ana", curso[0]); assertEquals(List.of("Previa"), curso[8]);
        assertEquals(List.of("Tecnología"), curso[9]); assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) curso[10]);
        invocar("altaEdicionCursoDocente", "nickDocente", "ana", "instituto", "INCO", "curso", "Curso", "nombre", "Edición", "inicio", fecha, "fin", fecha.plusDays(30),
                "cupo", -1, "docentes", List.of("ana"));
        assertEquals(fecha, llamadas.get("altaEdicionCursoDocente")[4]); assertEquals(-1, llamadas.get("altaEdicionCursoDocente")[6]);
        invocar("altaProgramaFormacion", "nickDocente", "ana", "nombre", "Programa", "descripcion", "Descripción", "inicio", fecha, "fin", fecha.plusDays(30), "alta", fecha);
        assertEquals(fecha, llamadas.get("altaProgramaFormacion")[2]);
        invocar("agregarCursoAPrograma", "nickDocente", "ana", "programa", "Programa", "curso", "Curso");
        assertArrayEquals(new Object[]{"Programa", "Curso"}, llamadas.get("agregarCursoAPrograma"));
    }

    @Test public void inscripcionesYSelectoresSoloEnvíanIdentificadores() throws Exception {
        publicar();
        for (String op : List.of("inscribirEstudianteAEdicion", "inscribirEstudianteAPrograma")) {
            invocar(op, "nickEstudiante", "estudiante", "nombre", "Formación ñ");
            assertArrayEquals(new Object[]{"estudiante", "Formación ñ"}, llamadas.get(op));
        }
        for (String op : List.of("listarNombresProgramas", "listarNicknamesDocentesPorInstituto", "listarEdicionesDocente")) {
            assertEquals(1, invocar(op, "instituto", "INCO", "nickDocente", "ana").getSOAPBody().getElementsByTagName("return").getLength());
        }
        var edicion = leer(invocar("listarEdicionesParaSeleccion", "nickDocente", "ana", "instituto", "INCO", "curso", "Curso"), soap.dto.DTEdicionConsulta.class);
        assertTrue(edicion.isVigente());
        var aceptado = leer(invocar("listarAceptadosEdicion", "nickDocente", "ana", "edicion", "Edición ñ"), soap.dto.DTInscripcionConsulta.class);
        assertEquals(Long.valueOf(17), aceptado.id); assertEquals(fecha, aceptado.fechaInscripcion); assertEquals("ACEPTADA", aceptado.estado);
        var ranking = leer(invocar("listarInscripcionesParaSeleccion", "nickDocente", "ana", "edicion", "Edición ñ", "orden", "PRIORIDAD"), soap.dto.DTRankingConsulta.class);
        assertEquals(3, ranking.rechazosAnteriores); assertEquals("estudiante", ranking.inscripcion.nicknameEstudiante);
    }

    @Test public void seleccionReconstruyeDecisionesYRechazaEstadosNoPermitidos() throws Exception {
        publicar();
        invocar("seleccionarEstudiantes", "nickDocente", "ana", "edicion", "Edición ñ", "decisiones", Map.of(17L, "ACEPTADA", 18L, "RECHAZADA"));
        assertEquals(Map.of(17L, EstadoInscripcion.ACEPTADA, 18L, EstadoInscripcion.RECHAZADA), llamadas.get("seleccionarEstudiantes")[2]);
        for (String estado : List.of("INSCRIPTO", "inventado")) {
            var error = assertThrows(SOAPFaultException.class, () -> invocar("seleccionarEstudiantes", "decisiones", Map.of(17L, estado)));
            assertTrue(error.getFault().getDetail().getTextContent().contains("VALIDACION"));
        }
        assertThrows(SOAPFaultException.class, () -> invocar("seleccionarEstudiantes"));
    }

    @Test public void erroresDeNegocioTienenCodigoYFallasTecnicasNoExponenDetalles() throws Exception {
        publicar();
        for (Exception e : List.of(new SecurityException("Permiso denegado"), new RecursoNoEncontradoException("No existe"),
                new IllegalArgumentException("Dato inválido"), new Exception("Curso repetido"))) {
            fallo = e;
            var error = assertThrows(SOAPFaultException.class, () -> invocar("seguirUsuario", "nickSeguidor", "ana", "nickSeguido", "ben"));
            assertNotNull(error.getFault().getDetail()); assertTrue(error.getFault().getDetail().getTextContent().contains(e.getMessage()));
        }
        fallo = new IllegalStateException("jdbc:ruta privada");
        var error = assertThrows(SOAPFaultException.class, () -> invocar("seguirUsuario"));
        assertFalse(error.getMessage().contains("jdbc:"));
        fallo = null; docente = false;
        assertThrows(SOAPFaultException.class, () -> invocar("altaProgramaFormacion", "nickDocente", "estudiante"));
        assertFalse(llamadas.containsKey("altaProgramaFormacion"));
        assertThrows(SOAPFaultException.class, () -> invocar("agregarCursoAPrograma", "nickDocente", "estudiante"));
        assertThrows(SOAPFaultException.class, () -> invocar("registrarEstudiante", "fechaNacimiento", "fecha inválida"));
    }
}
