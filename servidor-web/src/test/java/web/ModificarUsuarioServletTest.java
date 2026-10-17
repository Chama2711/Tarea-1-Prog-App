package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModificarUsuarioServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, manejador));
    }

    private static class Escenario {
        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> sesionDatos = new HashMap<>(), atributos = new HashMap<>();
        final Estudiante estudiante = new Estudiante("ana", "ana@example.com", "Ana", "Pérez", LocalDate.of(2000, 1, 1));
        final DTAutenticacion autenticado = new DTAutenticacion("ana", "Ana", DTAutenticacion.Rol.ESTUDIANTE);
        boolean conSesion = true, fallarGuardado;
        int escrituras, consultas;
        String redireccion, vista, actor;
        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            consultas++;
            return switch (m.getName()) {
                // Se ejecuta la validación real del central; solo el almacenamiento se sustituye.
                case "modificarUsuario" -> {
                    actor = (String) a[0];
                    new ControladorUsuario((IServidorCentral) p).modificarUsuario(actor,
                            (String) a[1], (String) a[2], (LocalDate) a[3], (String) a[4], (String) a[5]);
                    yield null;
                }
                case "obtenerUsuario" -> estudiante;
                case "editarUsuario" -> {
                    if (fallarGuardado) throw new IllegalStateException("detalle interno de conexión");
                    escrituras++; yield null;
                }
                case "consultarPerfilUsuario" -> DTPerfilUsuario.crear(new DTUsuarioConsulta(
                        (String) a[0], estudiante.getNombre(), estudiante.getApellido(), estudiante.getMail(),
                        estudiante.getFechaNacimiento(), DTAutenticacion.Rol.ESTUDIANTE, false, List.of()),
                        (String) a[1], List.of(), List.of(), List.of(), List.of());
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> central;
            case "log" -> null;
            default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion = proxy(HttpSession.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> sesionDatos.get(a[0]);
            case "setAttribute" -> { sesionDatos.put((String) a[0], a[1]); yield null; }
            case "removeAttribute" -> { sesionDatos.remove(a[0]); yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletRequest request = proxy(HttpServletRequest.class, (p, m, a) -> switch (m.getName()) {
            case "getSession" -> conSesion ? sesion : null;
            case "setCharacterEncoding" -> null;
            case "getContextPath" -> "/edext";
            case "getParameter" -> parametros.get(a[0]);
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> { vista = (String) a[0]; return null; });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setHeader": break;
                case "sendRedirect": redireccion = (String) a[0]; break;
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        Escenario() {
            estudiante.setContrasena("anterior");
            sesionDatos.put("usuario", autenticado);
            parametros.putAll(Map.of("nombre", " Ana María ", "apellido", " López ",
                    "fechaNacimiento", "2001-02-03", "clave", "", "confirmacionClave", ""));
        }
        void iniciar(HttpServlet servlet) throws ServletException {
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "perfil-test";
                case "getInitParameter" -> null;
                default -> throw new AssertionError(m.getName());
            }));
        }
        void guardar() throws Exception {
            var servlet = new ModificarUsuarioServlet(); iniciar(servlet); servlet.doPost(request, response);
        }
        void consultar(String nick) throws Exception {
            parametros.put("nick", nick);
            var servlet = new DetalleUsuarioServlet(); iniciar(servlet); servlet.doGet(request, response);
        }
    }

    @Test public void guardaDatosNormalizadosYConservaClaveVacia() throws Exception {
        var s = new Escenario(); s.parametros.put("nickname", "otro"); s.guardar();
        assertEquals("ana", s.actor); assertEquals(1, s.escrituras);
        assertEquals("Ana María", s.estudiante.getNombre());
        assertEquals(s.estudiante.getNombre(), s.autenticado.getNombre());
        assertEquals("López", s.estudiante.getApellido());
        assertEquals(LocalDate.of(2001, 2, 3), s.estudiante.getFechaNacimiento());
        assertEquals("anterior", s.estudiante.getContrasena());
        assertEquals("/edext/usuario?nick=ana#perfil-general", s.redireccion);
        assertNotNull(s.sesionDatos.get("mensajeExito"));
    }

    @Test public void cambiaClaveCuandoLaConfirmacionCoincide() throws Exception {
        var s = new Escenario(); s.parametros.put("clave", "nueva"); s.parametros.put("confirmacionClave", "nueva");
        s.guardar(); assertEquals(1, s.escrituras); assertEquals("nueva", s.estudiante.getContrasena());
    }

    @Test public void clavesIncompletasODistintasNoGuardanNiSeConservan() throws Exception {
        for (String[] claves : List.of(new String[]{"nueva", "distinta"}, new String[]{"nueva", ""}, new String[]{"", "nueva"})) {
            var s = new Escenario(); s.parametros.put("clave", claves[0]); s.parametros.put("confirmacionClave", claves[1]);
            s.guardar(); assertEquals(0, s.escrituras); assertEquals("anterior", s.estudiante.getContrasena());
            assertEquals("Ana", s.autenticado.getNombre());
            var borrador = (Map<?, ?>) s.sesionDatos.get(ModificarUsuarioServlet.BORRADOR_PERFIL);
            assertEquals(Set.of("nick", "nombre", "apellido", "fechaNacimiento"), borrador.keySet());
            assertEquals(" Ana María ", borrador.get("nombre"));
        }
    }

    @Test public void erroresDeDatosConservanEdicionSinGuardar() throws Exception {
        for (String fecha : List.of("fecha inválida", LocalDate.now().plusDays(1).toString(), "")) {
            var s = new Escenario(); s.parametros.put("fechaNacimiento", fecha); s.guardar();
            assertEquals(0, s.escrituras); assertNotNull(s.sesionDatos.get("mensajeError"));
            assertEquals(fecha, ((Map<?, ?>) s.sesionDatos.get(ModificarUsuarioServlet.BORRADOR_PERFIL)).get("fechaNacimiento"));
        }
        var s = new Escenario(); s.parametros.put("nombre", " "); s.guardar(); assertEquals(0, s.escrituras);
    }

    @Test public void falloTecnicoNoActualizaSesionNiMuestraDetallesInternos() throws Exception {
        var s = new Escenario(); s.fallarGuardado = true; s.guardar();
        assertEquals("Ana", s.autenticado.getNombre()); assertNull(s.sesionDatos.get("mensajeExito"));
        assertEquals("No se pudo modificar el perfil. Intentá nuevamente.", s.sesionDatos.get("mensajeError"));
        assertNotNull(s.sesionDatos.get(ModificarUsuarioServlet.BORRADOR_PERFIL));
    }

    @Test public void visitanteNoPuedeModificar() throws Exception {
        var s = new Escenario(); s.conSesion = false; s.guardar();
        assertEquals(0, s.consultas); assertEquals("/edext/inicio", s.redireccion);
    }

    @Test public void borradorSeRecuperaSoloEnElPerfilPropioYUnaVez() throws Exception {
        var s = new Escenario(); s.parametros.put("fechaNacimiento", "2099-01-01"); s.guardar();
        s.consultar("otra"); assertNull(s.atributos.get("datosEdicion"));
        assertNotNull(s.sesionDatos.get(ModificarUsuarioServlet.BORRADOR_PERFIL));
        s.consultar("ana"); assertNotNull(s.atributos.get("datosEdicion"));
        assertEquals("/WEB-INF/vistas/detalleUsuario.jsp", s.vista);
        assertNull(s.sesionDatos.get(ModificarUsuarioServlet.BORRADOR_PERFIL));
        s.atributos.clear(); s.consultar("ana"); assertNull(s.atributos.get("datosEdicion"));
    }
}
