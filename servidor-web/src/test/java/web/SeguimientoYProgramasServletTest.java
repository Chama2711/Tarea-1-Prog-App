package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SeguimientoYProgramasServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, manejador));
    }
    private static class Escenario {
        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>(), datosSesion = new HashMap<>();
        DTAutenticacion usuario = new DTAutenticacion("ana", "Ana", DTAutenticacion.Rol.DOCENTE);
        RuntimeException fallo;
        int estado = 200, consultas, cambios, logs;
        String redireccion, vista, operacion;
        Object[] argumentos;
        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            consultas++;
            return switch (m.getName()) {
                case "seguirUsuario", "dejarDeSeguirUsuario", "agregarCursoAPrograma" -> {
                    operacion = m.getName(); argumentos = a;
                    if (fallo != null) throw fallo;
                    cambios++; yield null;
                }
                case "listarNombresProgramas" -> List.of("Programa\nUno");
                case "listarNombresCursos" -> List.of("Curso\nUno");
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> central;
            case "log" -> { logs++; yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> "usuario".equals(a[0]) ? usuario : datosSesion.get(a[0]);
            case "setAttribute" -> { datosSesion.put((String) a[0], a[1]); yield null; }
            case "removeAttribute" -> { datosSesion.remove(a[0]); yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getSession" -> usuario == null ? null : sesion;
            case "setCharacterEncoding" -> null;
            case "getContextPath" -> "/edext";
            case "getParameter" -> parametros.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getAttribute" -> atributos.get(a[0]);
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> { vista = (String) a[0]; return null; });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setHeader": break;
                case "sendRedirect": estado = 302; redireccion = (String) a[0]; break;
                case "sendError": estado = (Integer) a[0]; break;
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        Escenario() { parametros.put("nickDestino", "ben"); parametros.put("accion", "seguir"); }
        void ejecutar(boolean programa, boolean post) throws Exception {
            HttpServlet servlet = programa ? new AgregarCursoProgramaServlet() : new Seguir();
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "seguimiento-programa-test";
                case "getInitParameter" -> null;
                default -> throw new AssertionError(m.getName());
            }));
            if (servlet instanceof Seguir s) s.doPost(request, response);
            else if (post) ((AgregarCursoProgramaServlet) servlet).doPost(request, response);
            else ((AgregarCursoProgramaServlet) servlet).doGet(request, response);
        }
    }

    @Test public void visitanteNoRealizaSeguimientos() throws Exception {
        var s = new Escenario(); s.usuario = null; s.ejecutar(false, true);
        assertEquals("/edext/login", s.redireccion); assertEquals(0, s.consultas);
    }
    @Test public void ambosRolesUsanSoloLaIdentidadDeSesionYCodificanElDestino() throws Exception {
        for (var rol : DTAutenticacion.Rol.values()) for (String accion : List.of("seguir", "dejar_seguir")) {
            var s = new Escenario(); s.usuario = new DTAutenticacion("ana", "Ana", rol);
            s.parametros.put("nickSeguidor", "otra"); s.parametros.put("nickDestino", "ben&ñ");
            s.parametros.put("accion", accion); s.ejecutar(false, true);
            assertArrayEquals(new Object[]{"ana", "ben&ñ"}, s.argumentos);
            assertEquals(accion.equals("seguir") ? "seguirUsuario" : "dejarDeSeguirUsuario", s.operacion);
            assertEquals("/edext/usuario?nick=ben%26%C3%B1", s.redireccion);
            assertEquals(1, s.cambios); assertNotNull(s.datosSesion.get("mensajeExito"));
        }
    }
    @Test public void destinoOAccionInvalidosNoLlamanAlCentral() throws Exception {
        for (String destino : List.of("", " ")) {
            var s = new Escenario(); s.parametros.put("nickDestino", destino); s.ejecutar(false, true);
            assertEquals(400, s.estado); assertEquals(0, s.consultas);
        }
        var s = new Escenario(); s.parametros.put("accion", "inventada"); s.ejecutar(false, true);
        assertEquals(400, s.estado); assertEquals(0, s.consultas);
    }
    @Test public void rechazoDelCentralNoMuestraUnExitoAnterior() throws Exception {
        var s = new Escenario(); s.datosSesion.put("mensajeExito", "mensaje anterior");
        s.fallo = new IllegalArgumentException("El usuario seleccionado no existe."); s.ejecutar(false, true);
        assertEquals(0, s.cambios); assertNull(s.datosSesion.get("mensajeExito"));
        assertEquals(s.fallo.getMessage(), s.datosSesion.get("mensajeError"));
        assertEquals("/edext/usuario?nick=ana", s.redireccion);
    }
    @Test public void identidadRechazadaProduce403YFalloTecnicoNoExponeDetalles() throws Exception {
        var s = new Escenario(); s.fallo = new SecurityException("Identidad inválida."); s.ejecutar(false, true);
        assertEquals(403, s.estado); assertNull(s.datosSesion.get("mensajeExito"));
        var tecnico = new Escenario(); tecnico.fallo = new IllegalStateException("detalle de conexión privado");
        tecnico.ejecutar(false, true); assertEquals(1, tecnico.logs);
        assertEquals("No se pudo realizar la acción. Intentá nuevamente.", tecnico.datosSesion.get("mensajeError"));
        assertEquals("/edext/usuario?nick=ana", tecnico.redireccion);
    }
    @Test public void agregarCursoNormalizaIdentificadoresSinPerderTildes() throws Exception {
        var s = new Escenario(); s.parametros.put("curso", "Curso\r\nUno");
        s.parametros.put("programa", "Formación\rIntegral"); s.ejecutar(true, true);
        assertArrayEquals(new Object[]{"Formación\nIntegral", "Curso\nUno"}, s.argumentos);
        assertEquals(1, s.cambios); assertTrue(s.redireccion.contains("Formaci%C3%B3n%0AIntegral"));
    }
    @Test public void programaSeleccionadoSeConservaTrasErrorConSaltosNormalizados() throws Exception {
        var s = new Escenario(); s.parametros.put("curso", "Curso\r\nUno");
        s.parametros.put("programa", "Programa\r\nUno"); s.fallo = new IllegalArgumentException("Curso repetido.");
        s.ejecutar(true, true); assertEquals(0, s.cambios);
        assertEquals("Programa\nUno", s.atributos.get("programaSeleccionado"));
        assertEquals("/WEB-INF/vistas/agregarCursoPrograma.jsp", s.vista);
    }
}
