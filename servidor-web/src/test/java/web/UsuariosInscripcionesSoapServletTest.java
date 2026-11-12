package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Ejecuta usuarios e inscripciones a través del servicio SOAP real, sin MySQL. */
public class UsuariosInscripcionesSoapServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>(), sesionDatos = new HashMap<>();
        final Map<String, Object[]> llamadas = new HashMap<>();
        RuntimeException fallo;
        boolean perfilAusente;
        String vista, redireccion, contenido;
        int estado = 200;
        byte[] imagen = new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff, 1};
        final java.io.ByteArrayOutputStream salida = new java.io.ByteArrayOutputStream();
        web.soap.ClienteCatalogoSoap cliente;
        final DTUsuarioConsulta datos = new DTUsuarioConsulta("ana", "Ana", "Pérez", "ana@test", LocalDate.of(2000, 1, 1), DTAutenticacion.Rol.ESTUDIANTE, true, List.of());
        final IServidorCentral central = proxy(IServidorCentral.class, (p, m, a) -> {
            llamadas.put(m.getName(), a);
            if (fallo != null) throw fallo;
            return switch (m.getName()) {
                case "autenticarUsuario" -> new DTAutenticacion("ana", "Ana", DTAutenticacion.Rol.ESTUDIANTE);
                case "listarUsuariosConsulta", "listarSeguidosConsulta" -> List.of(datos);
                case "esSeguidor" -> true;
                case "obtenerImagenUsuario" -> imagen;
                case "listarNombresInstitutos" -> List.of("INCO");
                case "consultarPerfilUsuario" -> perfilAusente ? null : DTPerfilUsuario.crear(datos, (String) a[1], List.of(), List.of(), List.of("Programa"), List.of());
                case "registrarEstudiante", "registrarDocente", "inscribirEstudianteAEdicion", "inscribirEstudianteAPrograma" -> null;
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto = proxy(ServletContext.class, (p, m, a) -> switch (m.getName()) {
            case "getAttribute" -> {
                assertTrue("No se permite acceso directo al central", ((String) a[0]).endsWith(".clienteCatalogo"));
                yield cliente;
            }
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
            case "getParameter" -> parametros.get(a[0]);
            case "getSession" -> a == null || a.length == 0 || Boolean.TRUE.equals(a[0]) || sesionDatos.containsKey("usuario") ? sesion : null;
            case "getContextPath" -> "/edext";
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class, (q, n, b) -> { vista = (String) a[0]; return null; });
            case "getPart" -> proxy(Part.class, (q, n, b) -> switch (n.getName()) {
                case "getSize" -> (long) imagen.length;
                case "getInputStream" -> new java.io.ByteArrayInputStream(imagen);
                default -> throw new AssertionError(n.getName());
            });
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response = proxy(HttpServletResponse.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setStatus", "sendError": estado = (Integer) a[0]; break;
                case "sendRedirect": estado = 302; redireccion = (String) a[0]; break;
                case "setHeader", "setContentLength": break;
                case "setContentType": contenido = (String) a[0]; break;
                case "getOutputStream": return new ServletOutputStream() {
                    @Override public void write(int b) { salida.write(b); }
                    @Override public boolean isReady() { return true; }
                    @Override public void setWriteListener(WriteListener listener) { }
                };
                default: throw new AssertionError(m.getName());
            }
            return null;
        });
        void autenticar(DTAutenticacion.Rol rol) {
            sesionDatos.put("usuario", new DTAutenticacion("ana", "Ana", rol));
        }
        void ejecutar(HttpServlet servlet) throws Exception {
            servlet.init(proxy(ServletConfig.class, (p, m, a) -> switch (m.getName()) {
                case "getServletContext" -> contexto;
                case "getServletName" -> "usuarios-inscripciones-soap";
                default -> null;
            }));
            try (var soap = new CatalogoSoapPrueba(central)) {
                cliente = soap.cliente;
                if (servlet instanceof LoginServlet s) s.doPost(request, response);
                else if (servlet instanceof RegistroServlet s) s.doPost(request, response);
                else if (servlet instanceof UsuariosServlet s) s.doGet(request, response);
                else if (servlet instanceof DetalleUsuarioServlet s) s.doGet(request, response);
                else if (servlet instanceof ImagenUsuarioServlet s) s.doGet(request, response);
                else if (servlet instanceof InscripcionEdicionServlet s) s.doPost(request, response);
                else ((InscripcionProgramaServlet) servlet).doPost(request, response);
            }
        }
    }

    @Test public void loginConservaIdentidadSinGuardarLaClaveEnSesion() throws Exception {
        var s = new Escenario(); s.parametros.putAll(Map.of("identificador", "ana@test", "clave", "secreto"));
        s.ejecutar(new LoginServlet());
        assertArrayEquals(new Object[]{"ana@test", "secreto"}, s.llamadas.get("autenticarUsuario"));
        assertEquals("ana", ((DTAutenticacion) s.sesionDatos.get("usuario")).getNick());
        assertEquals(Set.of("usuario"), s.sesionDatos.keySet()); assertEquals("/edext/inicio", s.redireccion);
    }
    @Test public void loginDistingueCredencialesInvalidasDeServicioCaido() throws Exception {
        for (boolean tecnico : List.of(false, true)) {
            var s = new Escenario(); s.parametros.put("identificador", "ana@test");
            s.fallo = tecnico ? new IllegalStateException("jdbc:privado") : new IllegalArgumentException("Credenciales incorrectas.");
            s.ejecutar(new LoginServlet()); assertEquals(tecnico ? 503 : 200, s.estado);
            assertEquals("/WEB-INF/vistas/login.jsp", s.vista); assertTrue(s.sesionDatos.isEmpty());
            assertEquals("ana@test", s.atributos.get("identificador"));
            assertFalse(s.atributos.get("error").toString().contains("jdbc:"));
        }
    }
    @Test public void registroDeAmbosRolesEnviaImagenFechaYConfirmacionSinDevolverClaves() throws Exception {
        for (String rol : List.of("estudiante", "docente")) {
            var s = new Escenario(); s.parametros.putAll(Map.of("tipo", rol, "nick", "ana", "correo", "ana@test", "nombre", "Ana", "apellido", "Pérez",
                    "fechaNacimiento", "2000-01-01", "instituto", "INCO", "clave", "secreto", "confirmacion", "secreto"));
            s.ejecutar(new RegistroServlet());
            var args = s.llamadas.get(rol.equals("docente") ? "registrarDocente" : "registrarEstudiante");
            assertEquals(LocalDate.of(2000, 1, 1), args[4]); assertArrayEquals(s.imagen, (byte[]) args[args.length - 1]);
            assertFalse(s.atributos.containsKey("clave")); assertFalse(s.atributos.containsKey("confirmacion"));
            assertEquals("/edext/login?registro=correcto", s.redireccion);
        }
    }
    @Test public void perfilesYListadosRecibenSoloDtosYUsanConsultanteDeSesion() throws Exception {
        var s = new Escenario(); s.autenticar(DTAutenticacion.Rol.ESTUDIANTE);
        s.parametros.putAll(Map.of("nick", "ana", "nickConsultante", "otra-persona"));
        s.ejecutar(new DetalleUsuarioServlet());
        assertArrayEquals(new Object[]{"ana", "ana"}, s.llamadas.get("consultarPerfilUsuario"));
        assertTrue(((DTPerfilUsuario) s.atributos.get("perfil")).propio());
        assertEquals("ana", ((List<DTUsuarioConsulta>) s.atributos.get("listaSeguidos")).get(0).nick());
        s = new Escenario(); s.ejecutar(new UsuariosServlet());
        assertEquals("ana", ((List<DTUsuarioConsulta>) s.atributos.get("usuarios")).get(0).nick());
    }
    @Test public void perfilInexistenteDa404YServicioCaidoDa503() throws Exception {
        var s = new Escenario(); s.parametros.put("nick", "ausente"); s.perfilAusente = true;
        s.ejecutar(new DetalleUsuarioServlet()); assertEquals(404, s.estado); assertNotNull(s.atributos.get("usuarios"));
        s = new Escenario(); s.parametros.put("nick", "ana"); s.fallo = new IllegalStateException("detalle privado");
        s.ejecutar(new DetalleUsuarioServlet()); assertEquals(503, s.estado); assertNull(s.vista);
    }
    @Test public void avatarViajaPorSoapYAusenciaConservaLaAlternativa() throws Exception {
        var s = new Escenario(); s.parametros.put("nick", "ana"); s.ejecutar(new ImagenUsuarioServlet());
        assertEquals("image/jpeg", s.contenido); assertArrayEquals(s.imagen, s.salida.toByteArray());
        s = new Escenario(); s.parametros.put("nick", "ana"); s.fallo = new IllegalStateException("ruta privada");
        s.ejecutar(new ImagenUsuarioServlet()); assertEquals("/edext/imagenes/avatar.svg", s.redireccion);
    }
    @Test public void ambasInscripcionesUsanLaIdentidadDeSesionYConservanSaltosDeLinea() throws Exception {
        for (boolean programa : List.of(false, true)) {
            var s = new Escenario(); s.autenticar(DTAutenticacion.Rol.ESTUDIANTE);
            s.parametros.put(programa ? "programa" : "edicion", "Formación\r\nñ"); s.parametros.put("nick", "otro");
            s.ejecutar(programa ? new InscripcionProgramaServlet() : new InscripcionEdicionServlet());
            assertArrayEquals(new Object[]{"ana", "Formación\nñ"}, s.llamadas.get(programa ? "inscribirEstudianteAPrograma" : "inscribirEstudianteAEdicion"));
            assertTrue(s.redireccion.endsWith("&inscripcion=ok"));
        }
    }
    @Test public void inscripcionesConservan403404YValidacionSinConfundirErroresTecnicos() throws Exception {
        for (boolean programa : List.of(false, true)) for (int caso = 0; caso < 4; caso++) {
            var s = new Escenario(); s.autenticar(DTAutenticacion.Rol.ESTUDIANTE); s.parametros.put(programa ? "programa" : "edicion", "Formación");
            s.fallo = switch (caso) {
                case 0 -> new SecurityException("No es estudiante.");
                case 1 -> new RecursoNoEncontradoException("No existe.");
                case 2 -> new IllegalArgumentException("Ya está inscripto.");
                default -> new IllegalStateException("jdbc:privado");
            };
            s.ejecutar(programa ? new InscripcionProgramaServlet() : new InscripcionEdicionServlet());
            assertEquals(new int[]{403, 404, 302, 503}[caso], s.estado);
            if (caso == 2) assertTrue(s.redireccion.contains("errorInscripcion="));
        }
    }
    @Test public void visitanteYDocenteNoEnvíanInscripciones() throws Exception {
        for (boolean programa : List.of(false, true)) for (boolean docente : List.of(false, true)) {
            var s = new Escenario(); if (docente) s.autenticar(DTAutenticacion.Rol.DOCENTE);
            s.ejecutar(programa ? new InscripcionProgramaServlet() : new InscripcionEdicionServlet());
            assertEquals(docente ? 403 : 302, s.estado); assertTrue(s.llamadas.isEmpty());
        }
    }
}
