package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Recorre HTTP/SOAP real y rechaza cualquier acceso directo al central desde la web. */
public class FormacionSoapServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        final Map<String, Object> atributos = new HashMap<>();
        String nombre = "Formación & ñ", ruta = "/imagen-programa", vista, redireccion, tipoContenido;
        String estudianteConsultado;
        Long programaConsultado;
        DTAutenticacion usuario;
        boolean ausente, fallar, inscripto;
        int estado = 200, consultas, consultasInscripcion;
        byte[] imagen = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        final java.io.ByteArrayOutputStream salida = new java.io.ByteArrayOutputStream();
        web.soap.ClienteCatalogoSoap cliente;
        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            consultas++;
            if (fallar) throw new IllegalStateException("Detalle privado JDBC");
            return switch (m.getName()) {
                case "listarProgramasConsulta" -> ausente ? List.of() : List.of(programa());
                case "consultarPrograma" -> {
                    assertEquals(nombre, a[0]); yield ausente ? null : programa();
                }
                case "estaInscriptoAPrograma" -> {
                    consultasInscripcion++; estudianteConsultado = (String) a[0];
                    programaConsultado = (Long) a[1]; yield inscripto;
                }
                case "consultarEdicion" -> {
                    assertEquals(nombre, a[0]);
                    yield ausente ? null : new DTEdicionConsulta(nombre, "Curso <ñ>",
                            LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 1),
                            LocalDate.of(2026, 10, 1), -1, true, true,
                            List.of(new DTUsuarioConsulta("docente_ñ", "Ana", "Pérez", "privado@test", null,
                                    DTAutenticacion.Rol.DOCENTE, false, List.of())));
                }
                case "obtenerImagenPrograma", "obtenerImagenEdicion" -> {
                    assertEquals(nombre, a[0]); yield imagen;
                }
                default -> throw new AssertionError(m.getName());
            };
        });
        soap.dto.DTProgramaConsulta programa() {
            return new soap.dto.DTProgramaConsulta(17L, nombre, "Descripción <programa>",
                    LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 1), LocalDate.of(2026, 10, 1),
                    true, List.of(new soap.dto.DTCursoResumen("Curso <ñ>", "Descripción del curso")),
                    List.of("Tecnología"));
        }
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> {
                assertTrue("La web no debe solicitar el central local", ((String) a[0]).endsWith(".clienteCatalogo"));
                yield cliente;
            }
            case "log" -> null;
            default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> usuario;
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getParameter" -> "nombre".equals(a[0]) ? nombre : "identidad-formulario-ignorada";
            case "getServletPath" -> ruta;
            case "getSession" -> usuario == null ? null : sesion;
            case "getContextPath" -> "/edext";
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> { vista = (String) a[0]; return null; });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "sendError": estado = (Integer) a[0]; break;
                case "sendRedirect": estado = 302; redireccion = (String) a[0]; break;
                case "setContentType": tipoContenido = (String) a[0]; break;
                case "setHeader", "setContentLength": break;
                case "getOutputStream": return new ServletOutputStream() {
                    @Override public void write(int b) { salida.write(b); }
                    @Override public boolean isReady() { return true; }
                    @Override public void setWriteListener(WriteListener listener) { }
                };
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        void ejecutar(HttpServlet servlet) throws Exception {
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "formacion-soap-test";
                default -> null;
            }));
            try (var soap = new CatalogoSoapPrueba(central)) {
                cliente = soap.cliente;
                if (servlet instanceof ProgramasServlet s) s.doGet(request, response);
                else if (servlet instanceof DetalleProgramaServlet s) s.doGet(request, response);
                else if (servlet instanceof DetalleEdicionServlet s) s.doGet(request, response);
                else ((ImagenFormacionServlet) servlet).doGet(request, response);
            }
        }
    }

    @Test public void listadoRecibeProgramasCompletosDesdeSoap() throws Exception {
        var s = new Escenario(); s.ejecutar(new ProgramasServlet());
        var programas = (List<web.soap.generado.DtProgramaConsulta>) s.atributos.get("programas");
        var programa = programas.get(0);
        assertEquals(s.nombre, programa.getNombre()); assertEquals("Descripción <programa>", programa.getDescripcion());
        assertEquals("2026-11-01", programa.getFechaInicio()); assertEquals("2026-12-01", programa.getFechaFin());
        assertEquals("2026-10-01", programa.getFechaAlta()); assertTrue(programa.isTieneImagen());
        assertEquals("Curso <ñ>", programa.getCurso().get(0).getNombre());
        assertEquals("Descripción del curso", programa.getCurso().get(0).getDescripcion());
        assertEquals(List.of("Tecnología"), programa.getCategoria());
        assertEquals("/WEB-INF/vistas/programas.jsp", s.vista);
    }

    @Test public void detalleDeProgramaConsultaInscripcionSoloParaElEstudianteDeSesion() throws Exception {
        var s = new Escenario(); s.inscripto = true;
        s.usuario = new DTAutenticacion("estudiante_ñ", "Estudiante", DTAutenticacion.Rol.ESTUDIANTE);
        s.ejecutar(new DetalleProgramaServlet());
        assertEquals("estudiante_ñ", s.estudianteConsultado); assertEquals(Long.valueOf(17), s.programaConsultado);
        assertEquals(true, s.atributos.get("yaInscripto")); assertEquals(1, s.consultasInscripcion);
        assertEquals(List.of("Tecnología"), s.atributos.get("categorias"));
        assertEquals("/WEB-INF/vistas/detallePrograma.jsp", s.vista);
    }

    @Test public void visitanteYDocenteNoConsultanInscripcion() throws Exception {
        for (var rol : Arrays.asList(null, DTAutenticacion.Rol.DOCENTE)) {
            var s = new Escenario(); s.usuario = rol == null ? null : new DTAutenticacion("docente", "Docente", rol);
            s.ejecutar(new DetalleProgramaServlet());
            assertEquals(0, s.consultasInscripcion); assertEquals(false, s.atributos.get("yaInscripto"));
        }
    }

    @Test public void detalleEdicionRecibeFechasCupoVigenciaYDocentesSinCredenciales() throws Exception {
        var s = new Escenario(); s.ejecutar(new DetalleEdicionServlet());
        var edicion = (web.soap.generado.DtEdicionConsulta) s.atributos.get("edicion");
        assertEquals(s.nombre, edicion.getNombre()); assertEquals("Curso <ñ>", edicion.getNombreCurso());
        assertEquals("2026-11-01", edicion.getInicio()); assertEquals("2026-12-01", edicion.getFin());
        assertEquals("2026-10-01", edicion.getPublicacion()); assertEquals(-1, edicion.getCupo());
        assertTrue(edicion.isVigente()); assertTrue(edicion.isTieneImagen());
        assertEquals("docente_ñ", edicion.getDocente().get(0).getNick());
        assertEquals("Ana Pérez", edicion.getDocente().get(0).getNombreCompleto());
        assertEquals("/WEB-INF/vistas/detalleEdicion.jsp", s.vista);
    }

    @Test public void inexistentesDan404YListadoVacioNoEsUnError() throws Exception {
        for (var servlet : List.of(new DetalleProgramaServlet(), new DetalleEdicionServlet())) {
            var s = new Escenario(); s.ausente = true; s.ejecutar(servlet);
            assertEquals(404, s.estado); assertNull(s.vista);
        }
        var s = new Escenario(); s.ausente = true; s.ejecutar(new ProgramasServlet());
        assertEquals(List.of(), s.atributos.get("programas")); assertEquals(200, s.estado);
    }

    @Test public void faltaDeNombreRedirigeSinConsultarElCentral() throws Exception {
        for (var servlet : List.of(new DetalleProgramaServlet(), new DetalleEdicionServlet())) {
            var s = new Escenario(); s.nombre = " "; s.ejecutar(servlet);
            assertEquals(0, s.consultas); assertEquals(302, s.estado);
        }
    }

    @Test public void erroresSoapDan503EnLugarDeConsultarLaBaseLocal() throws Exception {
        for (var servlet : List.of(new ProgramasServlet(), new DetalleProgramaServlet(), new DetalleEdicionServlet())) {
            var s = new Escenario(); s.fallar = true; s.ejecutar(servlet);
            assertEquals(503, s.estado); assertNull(s.vista);
        }
    }

    @Test public void imagenesDeProgramaYEdicionLleganPorSoap() throws Exception {
        for (String ruta : List.of("/imagen-programa", "/imagen-edicion")) {
            var s = new Escenario(); s.ruta = ruta; s.ejecutar(new ImagenFormacionServlet());
            assertEquals("image/png", s.tipoContenido); assertArrayEquals(s.imagen, s.salida.toByteArray());
        }
    }

    @Test public void imagenAusenteInvalidaOFalloSoapConservanLaAlternativa() throws Exception {
        for (String ruta : List.of("/imagen-programa", "/imagen-edicion")) for (int caso = 0; caso < 3; caso++) {
            var s = new Escenario(); s.ruta = ruta;
            s.imagen = caso == 0 ? null : new byte[]{1}; s.fallar = caso == 2;
            s.ejecutar(new ImagenFormacionServlet());
            assertEquals("/edext/imagenes/formacion.svg", s.redireccion); assertEquals(0, s.salida.size());
        }
    }
}
