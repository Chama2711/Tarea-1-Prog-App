package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Peticiones de valoración con transporte HTTP/SOAP real y datos controlados. */
public class ValorarCursoServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();
        DTAutenticacion usuario = new DTAutenticacion("alumno1", "Alumno Uno", DTAutenticacion.Rol.ESTUDIANTE);
        boolean sesionDisponible = true, participa = true, existeCurso = true, falloConsulta, perfilAusente;
        Integer puntaje;
        RuntimeException falloGuardado;
        int estado = 200, consultas, intentos, guardados;
        String vista, redireccion, mensaje, actor;
        int puntosEnviados;
        List<DTInscripcion> inscripciones = new ArrayList<>(List.of(
                fila(1, "Curso Java", EstadoInscripcion.ACEPTADA),
                fila(2, "Curso Java", EstadoInscripcion.ACEPTADA),
                fila(3, "Curso pendiente", EstadoInscripcion.INSCRIPTO),
                fila(4, "Curso rechazado", EstadoInscripcion.RECHAZADA)));
        web.soap.ClienteCatalogoSoap cliente;
        private static DTInscripcion fila(long id, String curso, EstadoInscripcion estado) {
            return new DTInscripcion(id, "alumno1", "Alumno Uno", "Edición " + id, curso, LocalDate.of(2026, 11, 1), estado);
        }
        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            consultas++;
            if (falloConsulta) throw new IllegalStateException("jdbc:detalle privado");
            return switch (m.getName()) {
                case "consultarPerfilUsuario" -> {
                    assertArrayEquals(new Object[]{"alumno1", "alumno1"}, a);
                    yield perfilAusente ? null : DTPerfilUsuario.crear(new DTUsuarioConsulta("alumno1", "Alumno", "Uno", null, null,
                            DTAutenticacion.Rol.ESTUDIANTE, false, List.of()), "alumno1", inscripciones, List.of(), List.of(), List.of());
                }
                case "buscarCurso" -> existeCurso ? new Curso() { @Override public Long getId() { return 7L; } } : null;
                case "obtenerPromedioValoraciones" -> puntaje == null ? 0.0 : puntaje.doubleValue();
                case "obtenerValoracionEstudiante" -> { assertEquals("alumno1", a[0]); yield puntaje; }
                case "registrarValoracionCurso" -> {
                    intentos++; actor = (String) a[0]; puntosEnviados = (Integer) a[2];
                    assertEquals(Long.valueOf(7), a[1]);
                    if (falloGuardado != null) throw falloGuardado;
                    if (!participa) throw new SecurityException("El estudiante no participa en este curso.");
                    if (puntaje != null) throw new ValoracionDuplicadaException("Ya valoraste este curso.");
                    puntaje = puntosEnviados; guardados++; yield null;
                }
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> {
                assertTrue("La web no puede acceder al central local", ((String) a[0]).endsWith(".clienteCatalogo")); yield cliente;
            }
            case "log" -> null;
            default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, a) -> usuario);
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getSession" -> sesionDisponible ? sesion : null;
            case "getParameter" -> parametros.get(a[0]);
            case "getContextPath" -> "/edext";
            case "setCharacterEncoding" -> null;
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> { vista = (String) a[0]; return null; });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setHeader": break;
                case "setStatus": estado = (Integer) a[0]; break;
                case "sendError": estado = (Integer) a[0]; mensaje = a.length > 1 ? (String) a[1] : null; break;
                case "sendRedirect": estado = 302; redireccion = (String) a[0]; break;
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        void ejecutar(boolean post) throws Exception {
            var servlet = new ValorarCursoServlet();
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "valorar-curso-soap-test";
                default -> null;
            }));
            try (var soap = new CatalogoSoapPrueba(central)) {
                cliente = soap.cliente;
                if (post) servlet.doPost(request, response); else servlet.doGet(request, response);
            }
        }
        void datosValidos() { parametros.put("curso", "Curso Java"); parametros.put("puntuacion", "5"); }
    }

    @Test public void visitanteOSesionSinUsuarioNoAbreNiEnviaValoraciones() throws Exception {
        for (boolean post : List.of(false, true)) for (boolean sinSesion : List.of(false, true)) {
            var s = new Escenario(); if (sinSesion) s.sesionDisponible = false; else s.usuario = null;
            s.ejecutar(post); assertEquals("/edext/login", s.redireccion); assertEquals(0, s.consultas);
        }
    }
    @Test public void docenteNoAbreNiEnviaValoraciones() throws Exception {
        for (boolean post : List.of(false, true)) {
            var s = new Escenario(); s.usuario = new DTAutenticacion("docente", "Docente", DTAutenticacion.Rol.DOCENTE);
            s.ejecutar(post); assertEquals(403, s.estado); assertEquals(0, s.consultas);
        }
    }
    @Test public void listaSoloCursosAceptadosSinRepetirLosDeVariasEdiciones() throws Exception {
        var s = new Escenario(); s.ejecutar(false);
        assertEquals(List.of("Curso Java"), s.atributos.get("cursosValorables"));
        assertEquals("", s.atributos.get("cursoSeleccionado")); assertEquals(1, s.consultas);
        assertEquals("/WEB-INF/vistas/valorarCurso.jsp", s.vista);
    }
    @Test public void estudianteSinParticipacionesVeUnaListaVacia() throws Exception {
        var s = new Escenario(); s.inscripciones = List.of(); s.ejecutar(false);
        assertEquals(List.of(), s.atributos.get("cursosValorables")); assertEquals(200, s.estado);
    }
    @Test public void cursoSeleccionadoMuestraPromedioYValoracionPropia() throws Exception {
        var s = new Escenario(); s.parametros.put("curso", "Curso Java"); s.puntaje = 4; s.ejecutar(false);
        assertEquals(4.0, (Double) s.atributos.get("promedioValoraciones"), 0.001);
        assertEquals(4, s.atributos.get("valoracionPropia")); assertEquals("Curso Java", s.atributos.get("cursoSeleccionado"));
    }
    @Test public void cursoSinValoracionesConservaPromedioCeroYPuntajePropioNulo() throws Exception {
        var s = new Escenario(); s.parametros.put("curso", "Curso Java"); s.ejecutar(false);
        assertEquals(0.0, (Double) s.atributos.get("promedioValoraciones"), 0.001);
        assertNull(s.atributos.get("valoracionPropia"));
    }
    @Test public void noPermiteConsultarCursoAjenoNiPerfilInexistente() throws Exception {
        var s = new Escenario(); s.parametros.put("curso", "Curso pendiente"); s.ejecutar(false);
        assertEquals(403, s.estado); assertNull(s.vista);
        s = new Escenario(); s.perfilAusente = true; s.ejecutar(false); assertEquals(403, s.estado);
    }
    @Test public void envioValidoUsaSoloIdentidadDeSesionYRedirigeParaNoRepetirPost() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.parametros.put("nickEstudiante", "otra-persona"); s.ejecutar(true);
        assertEquals("alumno1", s.actor); assertEquals(5, s.puntosEnviados); assertEquals(1, s.guardados);
        assertEquals("/edext/valorar-curso?curso=Curso+Java&guardada=1", s.redireccion);
        s.estado = 200; s.ejecutar(false); assertEquals(5, s.atributos.get("valoracionPropia")); assertEquals(1, s.guardados);
    }
    @Test public void caracteresEspecialesYSaltosDeLineaSeConservan() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.parametros.put("curso", "Curso & ñ\r\nUno"); s.ejecutar(true);
        assertEquals(302, s.estado); assertTrue(s.redireccion.contains("Curso+%26+%C3%B1%0AUno"));
    }
    @Test public void cursoVacioNoLlamaAlServicio() throws Exception {
        for (String nombre : Arrays.asList(null, "", " ")) {
            var s = new Escenario(); s.datosValidos(); s.parametros.put("curso", nombre); s.ejecutar(true);
            assertEquals(400, s.estado); assertEquals(0, s.consultas);
        }
    }
    @Test public void puntuacionVaciaNoLlamaAlServicio() throws Exception {
        for (String valor : Arrays.asList(null, "", " ")) {
            var s = new Escenario(); s.datosValidos(); s.parametros.put("puntuacion", valor); s.ejecutar(true);
            assertEquals(400, s.estado); assertEquals(0, s.consultas);
        }
    }
    @Test public void puntuacionNoEnteraOFueraDeRangoNoSeGuarda() throws Exception {
        for (String valor : List.of("texto", "2.5", "0", "6", "-1")) {
            var s = new Escenario(); s.datosValidos(); s.parametros.put("puntuacion", valor); s.ejecutar(true);
            assertEquals(400, s.estado); assertEquals(0, s.guardados); assertEquals(0, s.consultas);
        }
    }
    @Test public void extremosDeLaEscalaSeAceptan() throws Exception {
        for (String valor : List.of("1", "5")) {
            var s = new Escenario(); s.datosValidos(); s.parametros.put("puntuacion", valor); s.ejecutar(true);
            assertEquals(1, s.guardados); assertEquals(Integer.valueOf(valor), s.puntaje);
        }
    }
    @Test public void noParticipanteEsRechazadoPorElCentralInclusoConPostDirecto() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.participa = false; s.ejecutar(true);
        assertEquals(403, s.estado); assertEquals(0, s.guardados);
    }
    @Test public void duplicadoDa409YMuestraLaValoracionExistenteSinModificarla() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.puntaje = 3; s.ejecutar(true);
        assertEquals(409, s.estado); assertEquals(0, s.guardados); assertEquals(Integer.valueOf(3), s.puntaje);
        assertEquals("Ya valoraste este curso.", s.atributos.get("errorValoracion"));
        assertEquals(3, s.atributos.get("valoracionPropia")); assertNotNull(s.vista);
    }
    @Test public void cursoDesaparecidoDa404YDatosRechazadosDan400() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.existeCurso = false; s.ejecutar(true); assertEquals(404, s.estado);
        s = new Escenario(); s.datosValidos(); s.falloGuardado = new IllegalArgumentException("Dato rechazado");
        s.ejecutar(true); assertEquals(400, s.estado);
    }
    @Test public void falloDeComunicacionNoSeConfundeConRechazoDeNegocio() throws Exception {
        var s = new Escenario(); s.datosValidos(); s.falloGuardado = new IllegalStateException("jdbc:privado"); s.ejecutar(true);
        assertEquals(503, s.estado); assertEquals(0, s.guardados); assertFalse(s.mensaje.contains("jdbc:"));
        s = new Escenario(); s.falloConsulta = true; s.ejecutar(false); assertEquals(503, s.estado);
    }
}
