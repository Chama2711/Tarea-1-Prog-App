package web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logica.DTAutenticacion;
import logica.IServidorCentral;
import org.junit.Test;
import static org.junit.Assert.*;

/** Ejercita HTTP sin escribir registros en las bases usadas por los integrantes. */
public class AltasDocenteServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler comportamiento) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, comportamiento));
    }

    private static class Escenario {
        final Map<String, String[]> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();
        DTAutenticacion usuario = new DTAutenticacion("docente", "Docente", DTAutenticacion.Rol.DOCENTE);
        RuntimeException fallo;
        String redireccion, vista;
        int estado = 200, altas, consultas, logs;
        Object[] argumentosAlta;

        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, args) -> {
            if (m.getName().startsWith("alta")) {
                altas++; argumentosAlta = args;
                if (fallo != null) throw fallo;
                return null;
            }
            consultas++;
            return switch (m.getName()) {
                case "listarNombresInstitutosDocente" -> List.of("INCO");
                case "listarNombresCursosPorInstituto" -> {
                    assertEquals("INCO", args[0]); yield List.of("Curso");
                }
                case "listarNombresCategorias" -> List.of("Tecnología");
                case "listarNicknamesDocentesPorInstituto" -> List.of("docente");
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, args) -> {
            return switch (m.getName()) {
                case "getAttribute" -> central;
                case "log" -> { logs++; yield null; }
                default -> throw new AssertionError(m.getName());
            };
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, args) -> usuario);
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, args) -> {
            return switch (m.getName()) {
                case "getSession" -> usuario == null ? null : sesion;
                case "getContextPath" -> "/edext";
                case "getParameter" -> parametros.containsKey(args[0]) ? parametros.get(args[0])[0] : null;
                case "getParameterValues" -> parametros.get(args[0]);
                case "setAttribute" -> { atributos.put((String) args[0], args[1]); yield null; }
                case "getAttribute" -> atributos.get(args[0]);
                case "getRequestDispatcher" -> {
                    String destino = (String) args[0];
                    yield proxy(RequestDispatcher.class, (d, metodo, valores) -> { vista = destino; return null; });
                }
                default -> throw new AssertionError(m.getName());
            };
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, args) -> {
            switch (m.getName()) {
                case "sendRedirect": redireccion = (String) args[0]; estado = 302; break;
                case "sendError": estado = (Integer) args[0]; break;
                default: throw new AssertionError(m.getName());
            }
            return null;
        });

        Escenario() {
            valor("instituto", "INCO"); valor("curso", "Curso"); valor("nombre", " Curso nuevo ");
            valor("descripcion", "Descripción"); valor("duracion", "Un mes");
            valor("horas", "20"); valor("creditos", "3"); valor("url", "https://example.test");
            valor("fechaInicio", "2026-10-12"); valor("fechaFin", "2026-11-12");
            valor("categorias", "Tecnología"); valor("docentes", "docente");
        }
        void valor(String campo, String valor) { parametros.put(campo, new String[]{valor}); }
        void ejecutar(boolean edicion, boolean post) throws Exception {
            HttpServlet servlet = edicion ? new AltaEdicionCursoServlet() : new AltaCursoServlet();
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "alta-de-prueba";
                case "getInitParameter" -> null;
                default -> throw new AssertionError(m.getName());
            }));
            if (servlet instanceof AltaEdicionCursoServlet s) {
                if (post) s.doPost(request, response); else s.doGet(request, response);
            } else {
                AltaCursoServlet s = (AltaCursoServlet) servlet;
                if (post) s.doPost(request, response); else s.doGet(request, response);
            }
        }
    }

    @Test public void visitanteNoPuedeAbrirNiEnviarAltas() throws Exception {
        for (boolean edicion : List.of(false, true)) for (boolean post : List.of(false, true)) {
            Escenario s = new Escenario(); s.usuario = null; s.ejecutar(edicion, post);
            assertEquals("/edext/login", s.redireccion); assertEquals(0, s.altas + s.consultas);
        }
    }

    @Test public void estudianteNoPuedeAbrirNiEnviarAltas() throws Exception {
        for (boolean edicion : List.of(false, true)) for (boolean post : List.of(false, true)) {
            Escenario s = new Escenario();
            s.usuario = new DTAutenticacion("estudiante", "Estudiante", DTAutenticacion.Rol.ESTUDIANTE);
            s.ejecutar(edicion, post);
            assertEquals(403, s.estado); assertEquals(0, s.altas + s.consultas);
        }
    }

    @Test public void cursoConNumeroInvalidoConservaFormularioYSelecciones() throws Exception {
        Escenario s = new Escenario(); s.valor("horas", "muchas"); s.valor("previas", "Curso");
        s.ejecutar(false, true);
        assertEquals("/WEB-INF/vistas/altaCurso.jsp", s.vista);
        assertEquals("muchas", s.atributos.get("horas"));
        assertEquals(List.of("Tecnología"), s.atributos.get("categoriasSeleccionadas"));
        assertEquals(List.of("Curso"), s.atributos.get("previasSeleccionadas"));
        assertEquals(0, s.altas); assertNotNull(s.atributos.get("error"));
    }

    @Test public void cursoOfreceSoloPreviasDelInstitutoSeleccionado() throws Exception {
        Escenario s = new Escenario(); s.ejecutar(false, false);
        assertEquals(Map.of("INCO", List.of("Curso")), s.atributos.get("previasPorInstituto"));
        assertEquals("INCO", s.atributos.get("instituto"));
    }

    @Test public void cursoDuplicadoVuelveConDatosSinError500() throws Exception {
        Escenario s = new Escenario(); s.fallo = new IllegalArgumentException("Ya existe ese curso.");
        s.ejecutar(false, true);
        assertEquals("Ya existe ese curso.", s.atributos.get("error"));
        assertEquals(" Curso nuevo ", s.atributos.get("nombre")); assertEquals(200, s.estado);
    }

    @Test public void fechaInvalidaVuelveAlFormularioSinGuardar() throws Exception {
        for (String fecha : List.of("", "2026-02-31", "texto")) {
            Escenario s = new Escenario(); s.valor("fechaInicio", fecha); s.ejecutar(true, true);
            assertEquals("/WEB-INF/vistas/altaEdicionCurso.jsp", s.vista);
            assertEquals(fecha, s.atributos.get("fechaInicio")); assertEquals(0, s.altas);
            assertNotNull(s.atributos.get("error"));
        }
    }

    @Test public void cupoNoNumericoConservaDatosYNoGuarda() throws Exception {
        Escenario s = new Escenario(); s.valor("cupos", "tres"); s.ejecutar(true, true);
        assertEquals("tres", s.atributos.get("cupos"));
        assertEquals(List.of("docente"), s.atributos.get("docentesSeleccionados"));
        assertEquals(0, s.altas); assertNotNull(s.vista);
    }

    @Test public void cupoVacioMenosUnoYCeroRespetanElCentral() throws Exception {
        for (String valor : List.of("", "-1", "0")) {
            Escenario s = new Escenario(); s.valor("cupos", valor); s.ejecutar(true, true);
            assertEquals(valor.equals("0") ? 0 : -1, s.argumentosAlta[6]);
            assertEquals(302, s.estado);
        }
    }

    @Test public void lasAltasUsanIdentidadDeSesionYRedireccionAlDetalle() throws Exception {
        for (boolean edicion : List.of(false, true)) {
            Escenario s = new Escenario(); s.valor("nick", "otroDocente");
            s.valor("nombre", " Curso & edición "); s.ejecutar(edicion, true);
            assertEquals("docente", s.argumentosAlta[0]); assertEquals(1, s.altas);
            assertEquals(edicion ? "/edext/edicion?nombre=Curso+%26+edici%C3%B3n"
                    : "/edext/curso?nombre=Curso+%26+edici%C3%B3n", s.redireccion);
        }
    }

    @Test public void noConsultaCursosDeUnInstitutoAjenoDesdeGet() throws Exception {
        Escenario s = new Escenario(); s.valor("instituto", "Física"); s.ejecutar(true, false);
        assertEquals(403, s.estado); assertEquals(1, s.consultas); assertNull(s.vista);
    }

    @Test public void autorizacionCentralRechazadaDa403EnAmbasAltas() throws Exception {
        for (boolean edicion : List.of(false, true)) {
            Escenario s = new Escenario(); s.fallo = new SecurityException("Instituto ajeno.");
            s.ejecutar(edicion, true); assertEquals(403, s.estado); assertNull(s.vista);
        }
    }

    @Test public void falloInternoSeRegistraSinMostrarDetallesAlUsuario() throws Exception {
        for (boolean edicion : List.of(false, true)) {
            Escenario s = new Escenario(); s.fallo = new IllegalStateException("Detalle interno JDBC");
            s.ejecutar(edicion, true); assertEquals(1, s.logs); assertNotNull(s.vista);
            assertFalse(s.atributos.get("error").toString().contains("JDBC"));
        }
    }
}
