/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import logica.DTAutenticacion;
import logica.DTResultadoEvaluacion;

import org.junit.Test;
import static org.junit.Assert.*;

public class VerResultadosEvaluacionesServletTest {

    // Crea objetos simulados para las interfaces Servlet.
    private static <T> T proxy(
            Class<T> tipo,
            InvocationHandler manejador) {

        return tipo.cast(Proxy.newProxyInstance(
                tipo.getClassLoader(),
                new Class<?>[]{tipo},
                manejador
        ));
    }

    private static class Escenario {

        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();

        DTAutenticacion usuario = new DTAutenticacion(
                "docente",
                "Docente",
                DTAutenticacion.Rol.DOCENTE
        );

        List<String> ediciones =
                List.of("Edicion 2025", "Edicion 2026");

        List<DTResultadoEvaluacion> resultados = List.of(
                new DTResultadoEvaluacion(
                        15L,
                        "alumno1",
                        "Alumno Uno",
                        10,
                        LocalDate.of(2025, 11, 15)
                )
        );

        RuntimeException falloEdiciones;
        RuntimeException falloResultados;

        int estado = 200;
        int consultasEdiciones;
        int consultasResultados;
        int logs;

        String vista;
        String redireccion;
        String mensaje;
        String cache;
        String nickConsultado;
        String edicionConsultada;

        final ServletContext contexto = proxy(
                ServletContext.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {
                        case "log" -> {
                            logs++;
                            yield null;
                        }
                        default -> throw new AssertionError(
                                "Contexto no simulado: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final HttpSession sesion = proxy(
                HttpSession.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {
                        case "getAttribute" -> usuario;
                        default -> throw new AssertionError(
                                "Sesión no simulada: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletRequest request = proxy(
                HttpServletRequest.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {
                        case "getSession" ->
                            usuario == null ? null : sesion;

                        case "getContextPath" -> "/edext";

                        case "getParameter" ->
                            parametros.get((String) argumentos[0]);

                        case "setAttribute" -> {
                            atributos.put(
                                    (String) argumentos[0],
                                    argumentos[1]
                            );
                            yield null;
                        }

                        case "getRequestDispatcher" -> {
                            String destino = (String) argumentos[0];

                            yield proxy(
                                    RequestDispatcher.class,
                                    (q, m, a) -> {
                                        if ("forward".equals(m.getName())) {
                                            vista = destino;
                                            return null;
                                        }
                                        throw new AssertionError(
                                                "Dispatcher no simulado: "
                                                + m.getName()
                                        );
                                    }
                            );
                        }

                        default -> throw new AssertionError(
                                "Request no simulado: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletResponse response = proxy(
                HttpServletResponse.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {

                        case "setHeader" -> {
                            cache = (String) argumentos[1];
                            yield null;
                        }

                        case "sendError" -> {
                            estado = (Integer) argumentos[0];
                            if (argumentos.length > 1) {
                                mensaje = (String) argumentos[1];
                            }
                            yield null;
                        }

                        case "sendRedirect" -> {
                            estado = 302;
                            redireccion = (String) argumentos[0];
                            yield null;
                        }

                        case "encodeRedirectURL" ->
                            (String) argumentos[0];

                        default -> throw new AssertionError(
                                "Response no simulado: "
                                + metodo.getName()
                        );
                    };
                }
        );

        void seleccionarEdicion() {
            parametros.put("edicion", "Edicion 2025");
        }

        void ejecutarGet() throws Exception {

            var servlet = new VerResultadosEvaluacionesServlet() {
                @Override
                protected String prepararSeleccion(HttpServletRequest request, String nickDocente,
                        List<String> ediciones, String edicion) {
                    return edicion;
                }

                @Override
                protected List<String> consultarEdiciones(
                        String nickDocente) {

                    consultasEdiciones++;
                    assertEquals("docente", nickDocente);

                    if (falloEdiciones != null) {
                        throw falloEdiciones;
                    }

                    return ediciones;
                }

                @Override
                protected List<DTResultadoEvaluacion> consultarResultados(
                        String nickDocente,
                        String nombreEdicion) {

                    consultasResultados++;
                    nickConsultado = nickDocente;
                    edicionConsultada = nombreEdicion;

                    if (falloResultados != null) {
                        throw falloResultados;
                    }

                    return resultados;
                }
            };

            servlet.init(proxy(
                    ServletConfig.class,
                    (p, metodo, argumentos) -> {
                        return switch (metodo.getName()) {
                            case "getServletContext" -> contexto;
                            case "getServletName" ->
                                "ver-resultados-evaluaciones-test";
                            case "getInitParameter" -> null;
                            default -> throw new AssertionError(
                                    "Configuración no simulada: "
                                    + metodo.getName()
                            );
                        };
                    }
            ));

            servlet.doGet(request, response);
        }
    }

    @Test
    public void visitanteEsRedirigidoAlLogin() throws Exception {
        var e = new Escenario();
        e.usuario = null;

        e.ejecutarGet();

        assertEquals(302, e.estado);
        assertEquals("/edext/login", e.redireccion);
        assertEquals(0, e.consultasEdiciones);
        assertEquals(0, e.consultasResultados);
    }

    @Test
    public void estudianteNoPuedeAcceder() throws Exception {
        var e = new Escenario();
        e.usuario = new DTAutenticacion(
                "estudiante",
                "Estudiante",
                DTAutenticacion.Rol.ESTUDIANTE
        );

        e.ejecutarGet();

        assertEquals(403, e.estado);
        assertEquals(0, e.consultasEdiciones);
        assertEquals(0, e.consultasResultados);
    }

    @Test
    public void aperturaInicialMuestraSelector() throws Exception {
        var e = new Escenario();

        e.ejecutarGet();

        assertEquals(200, e.estado);
        assertEquals(
                "/WEB-INF/vistas/verResultadosEvaluaciones.jsp",
                e.vista
        );
        assertEquals(e.ediciones, e.atributos.get("ediciones"));
        assertEquals("", e.atributos.get("edicion"));
        assertEquals(List.of(), e.atributos.get("resultados"));
        assertEquals(1, e.consultasEdiciones);
        assertEquals(0, e.consultasResultados);
        assertEquals("no-store", e.cache);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void docenteConsultaResultadosDeSuEdicion() throws Exception {
        var e = new Escenario();
        e.seleccionarEdicion();

        e.ejecutarGet();

        assertEquals(200, e.estado);
        assertEquals(1, e.consultasResultados);
        assertEquals("docente", e.nickConsultado);
        assertEquals("Edicion 2025", e.edicionConsultada);

        List<DTResultadoEvaluacion> recibidos =
                (List<DTResultadoEvaluacion>)
                        e.atributos.get("resultados");

        assertEquals(1, recibidos.size());
        assertEquals("alumno1",
                recibidos.get(0).getNicknameEstudiante());
        assertEquals(Integer.valueOf(10),
                recibidos.get(0).getCalificacion());
        assertEquals(LocalDate.of(2025, 11, 15),
                recibidos.get(0).getFechaCalificacion());
    }

    @Test
    public void edicionSinResultadosMuestraListaVacia()
            throws Exception {

        var e = new Escenario();
        e.seleccionarEdicion();
        e.resultados = List.of();

        e.ejecutarGet();

        assertEquals(200, e.estado);
        assertEquals(List.of(), e.atributos.get("resultados"));
        assertEquals(1, e.consultasResultados);
    }

    @Test
    public void docenteNoPuedeConsultarEdicionAjena()
            throws Exception {

        var e = new Escenario();
        e.parametros.put("edicion", "Edicion ajena");

        e.ejecutarGet();

        assertEquals(403, e.estado);
        assertEquals(1, e.consultasEdiciones);
        assertEquals(0, e.consultasResultados);
    }

    @Test
    public void edicionAbiertaMuestraAdvertencia()
            throws Exception {

        var e = new Escenario();
        e.seleccionarEdicion();

        var fault = SOAPFactory.newInstance().createFault(
                "La edición todavía está abierta.",
                new QName(
                        "http://schemas.xmlsoap.org/soap/envelope/",
                        "Client"
                )
        );

        e.falloResultados = new SOAPFaultException(fault);

        e.ejecutarGet();

        assertEquals(200, e.estado);
        assertEquals(1, e.consultasResultados);
        assertEquals(
                "/WEB-INF/vistas/verResultadosEvaluaciones.jsp",
                e.vista
        );

        assertEquals(
                List.of(),
                e.atributos.get("resultados")
        );

        String aviso = (String)
                e.atributos.get("avisoResultados");

        assertNotNull(aviso);
        assertTrue(aviso.contains("todavía está abierta"));
    }

    @Test
    public void consultaInvalidaDevuelve400() throws Exception {
        var e = new Escenario();
        e.falloEdiciones =
                new IllegalArgumentException("Datos inválidos");

        e.ejecutarGet();

        assertEquals(400, e.estado);
        assertEquals(0, e.consultasResultados);
    }

    @Test
    public void servicioSoapNoDisponibleDevuelve503()
            throws Exception {

        var e = new Escenario();
        e.seleccionarEdicion();
        e.falloResultados =
                new WebServiceException("SOAP no disponible");

        e.ejecutarGet();

        assertEquals(503, e.estado);
        assertEquals(1, e.logs);
    }

    @Test
    public void errorInesperadoDevuelve500() throws Exception {
        var e = new Escenario();
        e.seleccionarEdicion();
        e.falloResultados =
                new IllegalStateException("Error técnico");

        e.ejecutarGet();

        assertEquals(500, e.estado);
        assertEquals(1, e.logs);
    }

    @Test
    public void errorDePermisosDevuelve403() throws Exception {
        var e = new Escenario();
        e.seleccionarEdicion();
        e.falloResultados =
                new SecurityException("Acceso denegado");

        e.ejecutarGet();

        assertEquals(403, e.estado);
        assertEquals(1, e.consultasResultados);
    }
}
