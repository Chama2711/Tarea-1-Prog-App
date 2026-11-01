package web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ListarAceptadosServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, manejador));
    }

    private static DTEdicionConsulta edicion(String nombre, String curso) {
        return new DTEdicionConsulta(nombre, curso, LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 2, 1), LocalDate.of(2019, 12, 1), 10,
                false, false, List.of());
    }

    private static class Escenario {
        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();
        DTAutenticacion usuario = new DTAutenticacion("docente", "Docente", DTAutenticacion.Rol.DOCENTE);
        List<DTEdicionConsulta> ediciones = List.of(edicion("Edicion finalizada", "Curso"), edicion("Otra", "Otro curso"));
        List<DTInscripcion> aceptados = List.of(new DTInscripcion(1L, "alumno", "Alumno",
                "Edicion finalizada", "Curso", LocalDate.of(2020, 1, 2), EstadoInscripcion.ACEPTADA));
        RuntimeException fallo;
        int estado = 200, consultas, consultasAceptados, logs;
        String redireccion, vista, mensaje, cache;

        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            consultas++;
            if (fallo != null) throw fallo;
            return switch (m.getName()) {
                case "listarNombresInstitutosDocente" -> {
                    assertEquals("docente", a[0]); yield List.of("INCO");
                }
                case "listarNombresCursosPorInstituto" -> {
                    assertEquals("INCO", a[0]); yield List.of("Curso");
                }
                case "consultarPerfilUsuario" -> {
                    assertArrayEquals(new Object[]{"docente", "docente"}, a);
                    var datos = new DTUsuarioConsulta("docente", "Docente", "Apellido", "docente@example.test",
                            LocalDate.of(1980, 1, 1), DTAutenticacion.Rol.DOCENTE, false, List.of("INCO"));
                    yield new DTPerfilUsuario(datos, true, List.of(), ediciones, List.of(), List.of());
                }
                case "listarAceptadosEdicion" -> {
                    consultasAceptados++;
                    assertArrayEquals(new Object[]{"docente", "Edicion finalizada"}, a);
                    yield aceptados;
                }
                default -> throw new AssertionError("Consulta inesperada o acceso a entidades: " + m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> central;
            case "log" -> { logs++; yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, a) -> usuario);
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getSession" -> usuario == null ? null : sesion;
            case "getContextPath" -> "/edext";
            case "getParameter" -> parametros.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> {
                String destino = (String) a[0];
                yield proxy(RequestDispatcher.class, (q, n, b) -> { vista = destino; return null; });
            }
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setHeader": cache = (String) a[1]; break;
                case "sendRedirect": estado = 302; redireccion = (String) a[0]; break;
                case "sendError": estado = (Integer) a[0]; mensaje = (String) a[1]; break;
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        void seleccionar() {
            parametros.put("instituto", "INCO"); parametros.put("curso", "Curso");
            parametros.put("edicion", "Edicion finalizada");
        }
        void ejecutar() throws Exception {
            var servlet = new ListarAceptadosServlet();
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "listar-aceptados-test";
                case "getInitParameter" -> null;
                default -> throw new AssertionError(m.getName());
            }));
            servlet.doGet(request, response);
        }
    }

    @Test public void visitanteEsRedirigidoYEstudianteNoConsultaDatos() throws Exception {
        var visitante = new Escenario(); visitante.usuario = null; visitante.ejecutar();
        assertEquals("/edext/login", visitante.redireccion); assertEquals(0, visitante.consultas);
        var estudiante = new Escenario();
        estudiante.usuario = new DTAutenticacion("alumno", "Alumno", DTAutenticacion.Rol.ESTUDIANTE);
        estudiante.ejecutar(); assertEquals(403, estudiante.estado); assertEquals(0, estudiante.consultas);
    }
    @Test public void aperturaInicialNoConsultaAceptados() throws Exception {
        var s = new Escenario(); s.ejecutar();
        assertEquals("/WEB-INF/vistas/listar-aceptados.jsp", s.vista);
        assertEquals(List.of("INCO"), s.atributos.get("institutos"));
        assertEquals(List.of(), s.atributos.get("ediciones")); assertEquals(1, s.consultas);
        assertEquals("no-store", s.cache);
    }
    @Test public void consultaEdicionFinalizadaSoloConDatosDeConsulta() throws Exception {
        var s = new Escenario(); s.seleccionar(); s.parametros.put("nick", "otroDocente"); s.ejecutar();
        assertEquals(200, s.estado); assertEquals(1, s.consultasAceptados);
        assertEquals(s.aceptados, s.atributos.get("aceptados"));
        assertEquals(List.of(s.ediciones.get(0)), s.atributos.get("ediciones"));
        assertSame(s.ediciones.get(0), s.atributos.get("edicionElegida"));
    }
    @Test public void institutoAjenoYCursoFueraDelInstitutoSeRechazan() throws Exception {
        var s = new Escenario(); s.seleccionar(); s.parametros.put("instituto", "Ajeno"); s.ejecutar();
        assertEquals(403, s.estado); assertEquals(1, s.consultas); assertNull(s.vista);
        s = new Escenario(); s.seleccionar(); s.parametros.put("curso", "Otro curso"); s.ejecutar();
        assertEquals(400, s.estado); assertEquals(0, s.consultasAceptados);
    }
    @Test public void edicionAjenaODeOtroCursoNoConsultaAceptados() throws Exception {
        for (String nombre : List.of("Edicion ajena", "Otra")) {
            var s = new Escenario(); s.seleccionar(); s.parametros.put("edicion", nombre); s.ejecutar();
            assertEquals(403, s.estado); assertEquals(0, s.consultasAceptados); assertNull(s.vista);
        }
    }
    @Test public void filtrosIncompletosNoPermitenConsultarUnaEdicion() throws Exception {
        var s = new Escenario(); s.parametros.put("edicion", "Edicion finalizada"); s.ejecutar();
        assertEquals(403, s.estado); assertEquals(0, s.consultasAceptados);
        s = new Escenario(); s.parametros.put("curso", "Curso"); s.ejecutar();
        assertEquals(400, s.estado); assertEquals(0, s.consultasAceptados);
    }
    @Test public void listasVaciasSeMuestranSinError() throws Exception {
        var s = new Escenario(); s.seleccionar(); s.aceptados = List.of(); s.ejecutar();
        assertEquals(200, s.estado); assertEquals(List.of(), s.atributos.get("aceptados"));
        s = new Escenario(); s.seleccionar(); s.parametros.remove("edicion"); s.ediciones = List.of(); s.ejecutar();
        assertEquals(200, s.estado); assertEquals(List.of(), s.atributos.get("ediciones"));
        assertEquals(0, s.consultasAceptados);
    }
    @Test public void falloInternoSeRegistraSinExponerDetalles() throws Exception {
        var s = new Escenario(); s.fallo = new IllegalStateException("Detalle privado JDBC"); s.ejecutar();
        assertEquals(500, s.estado); assertEquals(1, s.logs); assertNull(s.vista);
        assertEquals("No se pudo cargar el listado. Intentá nuevamente.", s.mensaje);
    }
}
