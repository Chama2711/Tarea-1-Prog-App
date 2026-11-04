package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Comprueba las consultas del catálogo por HTTP/SOAP y las consultas de programas aún locales. */
public class CategoriasServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }

    private static class Escenario {
        final Map<String, Object> atributos = new HashMap<>();
        final Map<String, String> parametros = new HashMap<>();
        final Curso curso = new Curso();
        final ProgramaFormacion programa = new ProgramaFormacion();
        final List<String> categorias = List.of("Educación & Tecnología");
        String nombre = "Formación ñ", vista, mensaje;
        boolean falloSoap, listadoVacio, cursoAusente;
        byte[] imagen = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        final java.io.ByteArrayOutputStream salida = new java.io.ByteArrayOutputStream();
        String tipoContenido, redireccion, institutoConsultado;
        int consultasCategorias;
        int estado = 200, consultasRemotas, logs;
        web.soap.ClienteCatalogoSoap cliente;

        final IServidorCentral local = proxy(IServidorCentral.class, (p, m, a) -> switch (m.getName()) {
            case "obtenerDetallePrograma" -> programa;
            case "obtenerProgramas" -> List.of(programa);
            case "listarNombresProgramas" -> List.of();
            default -> throw new AssertionError("Consulta local inesperada: " + m.getName());
        });
        final IServidorCentral remoto = proxy(IServidorCentral.class, (p, m, a) -> {
            consultasRemotas++;
            if (falloSoap) throw new IllegalStateException("Detalle privado JDBC");
            return switch (m.getName()) {
                case "listarNombresCursos" -> listadoVacio ? List.of() : List.of(nombre);
                case "listarNombresCursosPorInstituto" -> {
                    institutoConsultado = (String) a[0];
                    yield "Eléctrica".equals(institutoConsultado) && !listadoVacio ? List.of(nombre) : List.of();
                }
                case "consultarCurso" -> {
                    assertEquals(nombre, a[0]);
                    yield cursoAusente ? null : new soap.dto.DTCursoConsulta(nombre, "Descripción remota", "Un mes",
                            20, 3, "https://example.test", java.time.LocalDate.of(2026, 11, 4), "Eléctrica", true,
                            List.of("Previa"), categorias, List.of("Programa ñ"), List.of(new soap.dto.DTEdicionResumen(
                                    "Edición 2026", java.time.LocalDate.of(2026, 11, 5),
                                    java.time.LocalDate.of(2026, 12, 5), -1, false)));
                }
                case "obtenerImagenCurso" -> { assertEquals(nombre, a[0]); yield imagen; }
                case "listarCategoriasCurso" -> {
                    consultasCategorias++; assertEquals(nombre, a[0]);
                    yield categorias.stream().map(Categoria::new).toList();
                }
                case "listarNombresCategoriasPrograma" -> {
                    consultasCategorias++; assertEquals(nombre, a[0]); yield categorias;
                }
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> ((String) a[0]).endsWith(".clienteCatalogo") ? cliente : local;
            case "log" -> { logs++; yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getParameter" -> "nombre".equals(a[0]) ? nombre : parametros.get(a[0]);
            case "getSession" -> null;
            case "getContextPath" -> "/edext";
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> {
                vista = (String) a[0]; return null;
            });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "sendError": estado = (Integer) a[0]; mensaje = a.length > 1 ? (String) a[1] : null; break;
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

        Escenario() {
            curso.setNombre(nombre); programa.setNombre(nombre);
            programa.getCursos().put(nombre, curso);
        }

        void ejecutar(HttpServlet servlet) throws Exception {
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "categorias-test";
                default -> null;
            }));
            try (var soap = new CatalogoSoapPrueba(remoto)) {
                cliente = soap.cliente;
                if (servlet instanceof DetalleCursoServlet detalle) detalle.doGet(request, response);
                else if (servlet instanceof DetalleProgramaServlet detalle) detalle.doGet(request, response);
                else if (servlet instanceof BusquedaServlet busqueda) busqueda.doGet(request, response);
                else if (servlet instanceof InicioServlet inicio) inicio.doGet(request, response);
                else if (servlet instanceof CursosServlet cursos) cursos.doGet(request, response);
                else ((ImagenCursoServlet) servlet).doGet(request, response);
            }
        }
    }

    @Test public void detallesDeCursoYProgramaObtienenNombresRemotos() throws Exception {
        for (HttpServlet servlet : List.of(new DetalleCursoServlet(), new DetalleProgramaServlet())) {
            var s = new Escenario(); s.ejecutar(servlet);
            assertEquals(s.categorias, s.atributos.get("categorias"));
            assertEquals(1, s.consultasRemotas); assertNotNull(s.vista); assertEquals(200, s.estado);
        }
    }

    @Test public void falloSoapEnDetallesDevuelve503SinExponerDatosInternos() throws Exception {
        for (HttpServlet servlet : List.of(new DetalleCursoServlet(), new DetalleProgramaServlet())) {
            var s = new Escenario(); s.falloSoap = true; s.ejecutar(servlet);
            assertEquals(503, s.estado); assertNull(s.vista); assertEquals(1, s.logs);
            assertFalse(s.mensaje.contains("JDBC"));
        }
    }

    @Test public void inicioReutilizaLasCategoriasYaConsultadasPorElFiltro() throws Exception {
        var s = new Escenario(); s.atributos.put("navCategorias", s.categorias);
        s.ejecutar(new InicioServlet());
        assertEquals(1, s.atributos.get("cantidadCategorias"));
        assertEquals(0, s.consultasCategorias); assertEquals("/WEB-INF/vistas/inicio.jsp", s.vista);
    }
    @Test public void busquedaFiltraCursosYProgramasPorLasCategoriasRemotas() throws Exception {
        var s = new Escenario(); s.parametros.put("categoria", s.categorias.get(0));
        assertTrue(s.curso.getCategorias().isEmpty());
        s.ejecutar(new BusquedaServlet());
        var resultados = (List<BusquedaServlet.Resultado>) s.atributos.get("resultados");
        assertEquals(2, resultados.size()); assertTrue(s.consultasRemotas > 0);
        assertEquals("/WEB-INF/vistas/busqueda.jsp", s.vista);
    }

    @Test public void falloSoapAlFiltrarCategoriasDevuelve503() throws Exception {
        var s = new Escenario(); s.parametros.put("categoria", "Tecnología"); s.falloSoap = true;
        s.ejecutar(new BusquedaServlet());
        assertEquals(503, s.estado); assertNull(s.vista);
    }

    @Test public void busquedaSinFiltroNoConsultaCategoriasPorSeparado() throws Exception {
        var s = new Escenario(); s.ejecutar(new BusquedaServlet());
        assertEquals(0, s.consultasCategorias); assertNotNull(s.vista);
    }
    @Test public void listadoEInicioRecibenDtosRemotosSinConsultarCursosLocales() throws Exception {
        for (HttpServlet servlet : List.of(new CursosServlet(), new InicioServlet())) {
            var s = new Escenario(); s.ejecutar(servlet);
            var cursos = (List<web.soap.generado.DtCursoConsulta>) s.atributos.get("cursos");
            assertEquals(1, cursos.size()); assertEquals("Descripción remota", cursos.get(0).getDescripcion());
            assertEquals(200, s.estado); assertNotNull(s.vista);
        }
    }

    @Test public void detalleRecibeRelacionesDesdeElDtoYCursoAusenteDa404() throws Exception {
        var s = new Escenario(); s.ejecutar(new DetalleCursoServlet());
        var curso = (web.soap.generado.DtCursoConsulta) s.atributos.get("curso");
        assertEquals(List.of("Previa"), curso.getPrevia());
        assertEquals(List.of("Programa ñ"), curso.getPrograma());
        assertEquals("2026-11-04", curso.getFechaRegistro());
        assertEquals(-1, curso.getEdicion().get(0).getCupo());
        s = new Escenario(); s.cursoAusente = true; s.ejecutar(new DetalleCursoServlet());
        assertEquals(404, s.estado); assertNull(s.vista);
    }

    @Test public void faltaDeNombreRedirigeSinConsultarSoap() throws Exception {
        var s = new Escenario(); s.nombre = " "; s.ejecutar(new DetalleCursoServlet());
        assertEquals("/edext/cursos", s.redireccion); assertEquals(0, s.consultasRemotas);
    }

    @Test public void listadosVaciosOCursoDesaparecidoSeMuestranSinError() throws Exception {
        for (boolean desaparecido : List.of(false, true)) {
            var s = new Escenario(); s.cursoAusente = desaparecido; s.listadoVacio = !desaparecido;
            s.ejecutar(new CursosServlet());
            assertEquals(List.of(), s.atributos.get("cursos")); assertEquals(200, s.estado);
        }
    }

    @Test public void falloSoapDelListadoNoSeReemplazaConDatosLocales() throws Exception {
        for (HttpServlet servlet : List.of(new CursosServlet(), new InicioServlet())) {
            var s = new Escenario(); s.falloSoap = true; s.ejecutar(servlet);
            assertEquals(503, s.estado); assertNull(s.vista); assertEquals(1, s.logs);
        }
    }

    @Test public void busquedaPorInstitutoYCategoriaUtilizaDatosRemotos() throws Exception {
        var s = new Escenario(); s.parametros.put("instituto", "Eléctrica");
        s.parametros.put("categoria", s.categorias.get(0)); s.ejecutar(new BusquedaServlet());
        assertEquals("Eléctrica", s.institutoConsultado);
        assertEquals(2, ((List<?>) s.atributos.get("resultados")).size());
        assertEquals(0, s.consultasCategorias);
        s = new Escenario(); s.parametros.put("categoria", "Ajena"); s.ejecutar(new BusquedaServlet());
        assertEquals(List.of(), s.atributos.get("resultados"));
    }

    @Test public void imagenDelCursoLlegaPorSoapYSeSirveComoPng() throws Exception {
        var s = new Escenario(); s.ejecutar(new ImagenCursoServlet());
        assertEquals("image/png", s.tipoContenido); assertArrayEquals(s.imagen, s.salida.toByteArray());
    }

    @Test public void imagenAusenteInvalidaOFalloSoapMuestranAlternativa() throws Exception {
        for (int caso = 0; caso < 3; caso++) {
            var s = new Escenario();
            s.imagen = caso == 0 ? null : new byte[]{1, 2, 3}; s.falloSoap = caso == 2;
            s.ejecutar(new ImagenCursoServlet());
            assertEquals("/edext/imagenes/formacion.svg", s.redireccion); assertEquals(0, s.salida.size());
        }
    }
}
