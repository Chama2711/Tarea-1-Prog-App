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
import jakarta.xml.ws.WebServiceException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logica.DTAutenticacion;
import logica.DTInscripcion;
import logica.EstadoInscripcion;

import org.junit.Test;
import static org.junit.Assert.*;

import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.ws.soap.SOAPFaultException;
import javax.xml.namespace.QName;

    
public class IngresarEvaluacionesServletTest {

    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
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
                "docente", "Docente", DTAutenticacion.Rol.DOCENTE
        );

        List<String> ediciones = List.of("Edicion 2025", "Edicion 2026");

        List<DTInscripcion> aceptados = List.of(
                new DTInscripcion(
                        15L,
                        "alumno1",
                        "Alumno Uno",
                        "Edicion 2025",
                        "Curso Java",
                        LocalDate.of(2025, 4, 10),
                        EstadoInscripcion.ACEPTADA
                )
        );

        RuntimeException falloEdiciones;
        RuntimeException falloAceptados;
        RuntimeException falloRegistro;
        RuntimeException falloCierre;

        int cierres;
        String nickCierre;
        String edicionCierre;
        int estado = 200;
        int consultasEdiciones;
        int consultasAceptados;
        int registrosCalificacion;
        int logs;

        String redireccion;
        String vista;
        String mensaje;
        String cache;

        String nickRegistrado;
        String edicionRegistrada;
        Long idRegistrado;
        Integer notaRegistrada;

        final ServletContext contexto = proxy(
                ServletContext.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {
                        case "log" -> {
                            logs++;
                            yield null;
                        }
                        default -> throw new AssertionError(
                                "Método de contexto no simulado: " + metodo.getName()
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
                                "Método de sesión no simulado: " + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletRequest request = proxy(
                HttpServletRequest.class,
                (p, metodo, argumentos) -> {
                    return switch (metodo.getName()) {
                        case "getSession" -> usuario == null ? null : sesion;
                        case "getContextPath" -> "/edext";
                        case "getParameter" -> parametros.get((String) argumentos[0]);
                        case "setCharacterEncoding" -> null;
                        case "setAttribute" -> {
                            atributos.put((String) argumentos[0], argumentos[1]);
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
                                                "Método de dispatcher no simulado: " + m.getName()
                                        );
                                    }
                            );
                        }
                        default -> throw new AssertionError(
                                "Método de request no simulado: " + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletResponse response = proxy(
                HttpServletResponse.class,
                (p, metodo, argumentos) -> {
                    // Es un switch statement: no hay return dentro de un switch expression.
                    if ("encodeRedirectURL".equals(metodo.getName())) {
                        return (String) argumentos[0];
                    }
                    switch (metodo.getName()) {
                        case "setHeader" -> cache = (String) argumentos[1];
                        case "sendRedirect" -> {
                            estado = 302;
                            redireccion = (String) argumentos[0];
                        }
                        case "sendError" -> {
                            estado = (Integer) argumentos[0];
                            if (argumentos.length > 1) {
                                mensaje = (String) argumentos[1];
                            }
                        }
                        default -> throw new AssertionError(
                                "Método de response no simulado: " + metodo.getName()
                        );
                    }
                    return null;
                }
        );

        void seleccionarEdicion() {
            parametros.put("edicion", "Edicion 2025");
        }

        void seleccionarNotaValida() {
            seleccionarEdicion();
            parametros.put("idInscripcion", "15");
            parametros.put("nota", "10");
        }
        
        
        void seleccionarCierre() {
            seleccionarEdicion();
            parametros.put("accion", "cerrar");
        }


        void ejecutarGet() throws Exception {
            ejecutar(false);
        }

        void ejecutarPost() throws Exception {
            ejecutar(true);
        }

        private void ejecutar(boolean post) throws Exception {
            var servlet = new IngresarEvaluacionesServlet() {
                @Override
                protected String prepararSeleccion(HttpServletRequest request, String nickDocente,
                        List<String> ediciones, String edicion) {
                    return edicion;
                }

                @Override
                protected List<String> consultarEdiciones(String nickDocente) {
                    consultasEdiciones++;
                    assertEquals("docente", nickDocente);
                    if (falloEdiciones != null) {
                        throw falloEdiciones;
                    }
                    return ediciones;
                }

                @Override
                protected List<DTInscripcion> consultarAceptados(
                        String nickDocente, String nombreEdicion) {
                    consultasAceptados++;
                    assertEquals("docente", nickDocente);
                    assertEquals("Edicion 2025", nombreEdicion);
                    if (falloAceptados != null) {
                        throw falloAceptados;
                    }
                    return aceptados;
                }

                @Override
                protected void registrarCalificacion(
                        String nickDocente, String nombreEdicion,
                        Long idInscripcion, int nota) {
                    registrosCalificacion++;
                    nickRegistrado = nickDocente;
                    edicionRegistrada = nombreEdicion;
                    idRegistrado = idInscripcion;
                    notaRegistrada = nota;
                    if (falloRegistro != null) {
                        throw falloRegistro;
                    }
                }
                
                
                @Override
                protected void cerrarEdicion(
                        String nickDocente,
                        String nombreEdicion) {

                    cierres++;
                    nickCierre = nickDocente;
                    edicionCierre = nombreEdicion;

                    if (falloCierre != null) {
                        throw falloCierre;
                    }
                }

            };

            servlet.init(proxy(
                    ServletConfig.class,
                    (p, metodo, argumentos) -> {
                        return switch (metodo.getName()) {
                            case "getServletContext" -> contexto;
                            case "getServletName" -> "ingresar-evaluaciones-test";
                            case "getInitParameter" -> null;
                            default -> throw new AssertionError(
                                    "Método de configuración no simulado: " + metodo.getName()
                            );
                        };
                    }
            ));

            if (post) {
                servlet.doPost(request, response);
            } else {
                servlet.doGet(request, response);
            }
        }
    }

    @Test
    public void visitanteEsRedirigidoAlLogin() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = null;
        escenario.ejecutarGet();

        assertEquals(302, escenario.estado);
        assertEquals("/edext/login", escenario.redireccion);
        assertEquals(0, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void estudianteNoPuedeAcceder() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = new DTAutenticacion(
                "estudiante", "Estudiante", DTAutenticacion.Rol.ESTUDIANTE
        );
        escenario.ejecutarGet();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.consultasEdiciones);
    }

    @Test
    public void aperturaInicialMuestraEdiciones() throws Exception {
        var escenario = new Escenario();
        escenario.ejecutarGet();

        assertEquals(200, escenario.estado);
        assertEquals("/WEB-INF/vistas/ingresarEvaluaciones.jsp", escenario.vista);
        assertEquals(escenario.ediciones, escenario.atributos.get("ediciones"));
        assertEquals("", escenario.atributos.get("edicion"));
        assertEquals(List.of(), escenario.atributos.get("aceptados"));
        assertEquals(1, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
        assertEquals("no-store", escenario.cache);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void docenteConsultaEstudiantesAceptados() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarEdicion();
        escenario.ejecutarGet();

        assertEquals(200, escenario.estado);
        assertEquals(1, escenario.consultasAceptados);
        List<DTInscripcion> recibidos =
                (List<DTInscripcion>) escenario.atributos.get("aceptados");
        assertEquals(escenario.aceptados.size(), recibidos.size());
        assertNotNull(recibidos.get(0));
        assertEquals(escenario.aceptados.get(0).getNicknameEstudiante(),
                recibidos.get(0).getNicknameEstudiante());
        assertEquals(escenario.aceptados.get(0).getEstado(),
                recibidos.get(0).getEstado());
        assertEquals(escenario.aceptados.get(0).getFechaInscripcion(),
                recibidos.get(0).getFechaInscripcion());
        assertEquals("Edicion 2025", escenario.atributos.get("edicion"));
    }

    @Test
    public void docenteNoPuedeConsultarEdicionAjena() throws Exception {
        var escenario = new Escenario();
        escenario.parametros.put("edicion", "Edicion ajena");
        escenario.ejecutarGet();

        assertEquals(403, escenario.estado);
        assertEquals(1, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void consultaConDatosInvalidosDevuelve400() throws Exception {
        var escenario = new Escenario();
        escenario.falloEdiciones = new IllegalArgumentException("Solicitud inválida");
        escenario.ejecutarGet();

        assertEquals(400, escenario.estado);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void consultaSinServicioSoapDevuelve503() throws Exception {
        var escenario = new Escenario();
        escenario.falloEdiciones = new WebServiceException("SOAP no disponible");
        escenario.ejecutarGet();

        assertEquals(503, escenario.estado);
        assertEquals(1, escenario.logs);
    }

    @Test
    public void consultaConErrorInternoDevuelve500() throws Exception {
        var escenario = new Escenario();
        escenario.falloEdiciones = new IllegalStateException("Error de prueba");
        escenario.ejecutarGet();

        assertEquals(500, escenario.estado);
        assertEquals(1, escenario.logs);
    }

    @Test
    public void docenteGuardaCalificacionValida() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.ejecutarPost();

        assertEquals(1, escenario.registrosCalificacion);
        assertEquals("docente", escenario.nickRegistrado);
        assertEquals("Edicion 2025", escenario.edicionRegistrada);
        assertEquals(Long.valueOf(15L), escenario.idRegistrado);
        assertEquals(Integer.valueOf(10), escenario.notaRegistrada);
        assertEquals(302, escenario.estado);
        assertEquals("/edext/ingresar-evaluaciones?edicion=Edicion+2025&guardada=1",
                escenario.redireccion);
        assertEquals(1, escenario.consultasEdiciones);
        assertEquals(1, escenario.consultasAceptados);
    }

    @Test
    public void visitanteNoPuedeGuardar() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = null;
        escenario.seleccionarNotaValida();
        escenario.ejecutarPost();

        assertEquals(302, escenario.estado);
        assertEquals("/edext/login", escenario.redireccion);
        assertEquals(0, escenario.registrosCalificacion);
    }

    @Test
    public void estudianteNoPuedeGuardar() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = new DTAutenticacion(
                "estudiante", "Estudiante", DTAutenticacion.Rol.ESTUDIANTE
        );
        escenario.seleccionarNotaValida();
        escenario.ejecutarPost();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.registrosCalificacion);
    }

    @Test
    public void noGuardaSiFaltanDatos() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarEdicion();
        escenario.parametros.put("idInscripcion", "15");
        escenario.ejecutarPost();

        assertEquals(400, escenario.estado);
        assertEquals(0, escenario.registrosCalificacion);
    }

    @Test
    public void noGuardaNotaFueraDeRango() throws Exception {
        for (String notaInvalida : List.of("0", "13", "abc")) {
            var escenario = new Escenario();
            escenario.seleccionarNotaValida();
            escenario.parametros.put("nota", notaInvalida);
            escenario.ejecutarPost();

            assertEquals(400, escenario.estado);
            assertEquals(0, escenario.registrosCalificacion);
        }
    }

    @Test
    public void noGuardaInscripcionInvalida() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.parametros.put("idInscripcion", "-2");
        escenario.ejecutarPost();

        assertEquals(400, escenario.estado);
        assertEquals(0, escenario.registrosCalificacion);
    }

    @Test
    public void docenteNoPuedeGuardarEnEdicionAjena() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.parametros.put("edicion", "Edicion ajena");
        escenario.ejecutarPost();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.registrosCalificacion);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void noGuardaInscripcionQueNoEstaAceptada() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.parametros.put("idInscripcion", "99");
        escenario.ejecutarPost();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.registrosCalificacion);
        assertEquals(1, escenario.consultasAceptados);
    }

    @Test
    public void errorSoapAlGuardarDevuelve503() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.falloRegistro = new WebServiceException("SOAP no disponible");
        escenario.ejecutarPost();

        assertEquals(503, escenario.estado);
        assertEquals(1, escenario.registrosCalificacion);
        assertEquals(1, escenario.logs);
    }

    @Test
    public void errorInternoAlGuardarDevuelve500() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarNotaValida();
        escenario.falloRegistro = new IllegalStateException("Error técnico");
        escenario.ejecutarPost();

        assertEquals(500, escenario.estado);
        assertEquals(1, escenario.logs);
    }
    
    
    @Test
    public void docenteCierraEdicionAutorizada() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarCierre();

        escenario.ejecutarPost();

        assertEquals(302, escenario.estado);
        assertEquals(1, escenario.cierres);
        assertEquals("docente", escenario.nickCierre);
        assertEquals("Edicion 2025", escenario.edicionCierre);

        assertEquals(
                "/edext/ingresar-evaluaciones"
                + "?edicion=Edicion+2025&cerrada=1",
                escenario.redireccion
        );

        assertEquals(1, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
        assertEquals(0, escenario.registrosCalificacion);
        assertEquals("no-store", escenario.cache);
    }

    @Test
    public void cierreSinEdicionDevuelve400() throws Exception {
        var escenario = new Escenario();
        escenario.parametros.put("accion", "cerrar");

        escenario.ejecutarPost();

        assertEquals(400, escenario.estado);
        assertEquals(0, escenario.cierres);
        assertEquals(0, escenario.consultasEdiciones);
    }

    @Test
    public void visitanteNoPuedeCerrar() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = null;
        escenario.seleccionarCierre();

        escenario.ejecutarPost();

        assertEquals(302, escenario.estado);
        assertEquals("/edext/login", escenario.redireccion);
        assertEquals(0, escenario.cierres);
    }

    @Test
    public void estudianteNoPuedeCerrar() throws Exception {
        var escenario = new Escenario();
        escenario.usuario = new DTAutenticacion(
                "estudiante",
                "Estudiante",
                DTAutenticacion.Rol.ESTUDIANTE
        );
        escenario.seleccionarCierre();

        escenario.ejecutarPost();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.cierres);
    }

    @Test
    public void docenteNoPuedeCerrarEdicionAjena() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarCierre();
        escenario.parametros.put("edicion", "Edicion ajena");

        escenario.ejecutarPost();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.cierres);
        assertEquals(1, escenario.consultasEdiciones);
    }

    @Test
    public void rechazoSoapAlCerrarDevuelve409() throws Exception {
        var escenario = new Escenario();
        escenario.seleccionarCierre();

        escenario.falloCierre = new SOAPFaultException(
                SOAPFactory.newInstance().createFault(
                        "Hay estudiantes sin calificación",
                        new QName(
                                "http://schemas.xmlsoap.org/soap/envelope/",
                                "Server"
                        )
                )
        );

        escenario.ejecutarPost();

        assertEquals(409, escenario.estado);
        assertEquals(1, escenario.cierres);
        assertEquals(1, escenario.logs);
        assertNull(escenario.redireccion);
    }

    @Test
    public void servicioSoapNoDisponibleAlCerrarDevuelve503()
            throws Exception {

        var escenario = new Escenario();
        escenario.seleccionarCierre();
        escenario.falloCierre = new WebServiceException(
                "Servicio no disponible"
        );

        escenario.ejecutarPost();

        assertEquals(503, escenario.estado);
        assertEquals(1, escenario.cierres);
        assertEquals(1, escenario.logs);
    }

    @Test
    public void datosInvalidosAlCerrarDevuelven400()
            throws Exception {

        var escenario = new Escenario();
        escenario.seleccionarCierre();
        escenario.falloCierre = new IllegalArgumentException(
                "Edición inválida"
        );

        escenario.ejecutarPost();

        assertEquals(400, escenario.estado);
        assertEquals(1, escenario.cierres);
        assertEquals(0, escenario.logs);
    }

    @Test
    public void errorInternoAlCerrarDevuelve500()
            throws Exception {

        var escenario = new Escenario();
        escenario.seleccionarCierre();
        escenario.falloCierre = new IllegalStateException(
                "Error interno simulado"
        );

        escenario.ejecutarPost();

        assertEquals(500, escenario.estado);
        assertEquals(1, escenario.cierres);
        assertEquals(1, escenario.logs);
    }

}
