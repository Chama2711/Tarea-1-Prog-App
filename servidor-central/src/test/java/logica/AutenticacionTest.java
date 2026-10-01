package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;

public class AutenticacionTest {
    private static final LocalDate NACIMIENTO = LocalDate.of(2000, 1, 2);

    private ControladorUsuario controlador(AtomicReference<Usuario> cuenta) {
        IServidorCentral servidor = (IServidorCentral) Proxy.newProxyInstance(
                IServidorCentral.class.getClassLoader(), new Class<?>[]{IServidorCentral.class},
                (proxy, metodo, argumentos) -> {
                    switch (metodo.getName()) {
                        case "crearUsuario":
                            if (cuenta.get() != null) {
                                throw new IllegalArgumentException("Ya existe ese nickname.");
                            }
                            cuenta.set((Usuario) argumentos[0]);
                            return null;
                        case "obtenerUsuarioPorIdentificador":
                            Usuario u = cuenta.get();
                            String id = (String) argumentos[0];
                            return u != null && (u.getNick().equals(id)
                                    || u.getMail().equalsIgnoreCase(id)) ? u : null;
                        case "obtenerUsuario": return cuenta.get();
                        case "editarUsuario": cuenta.set((Usuario) argumentos[0]); return null;
                        case "obtenerUsuarios": return List.of(cuenta.get());
                        default: throw new AssertionError("Operación inesperada: " + metodo.getName());
                    }
                });
        return new ControladorUsuario(servidor);
    }

    @Test
    public void registroYAccesoPorNicknameOCorreo() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        char[] clave = " clave de prueba ".toCharArray();
        control.registrarEstudiante(" alumno ", " alumno@example.test ",
                " Ana ", " Perez ", NACIMIENTO, clave, clave);
        assertNotEquals(new String(clave), cuenta.get().getHashContrasena());
        assertEquals("Ana", control.autenticarUsuario("alumno", clave).getNombre());
        assertEquals("alumno", control.autenticarUsuario(" ALUMNO@EXAMPLE.TEST ", clave).getNick());
        assertEquals(DTAutenticacion.Rol.ESTUDIANTE,
                control.autenticarUsuario("alumno", clave).getRol());
        assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("alumno", "clave de prueba".toCharArray()));
    }

    @Test
    public void registroDocenteDevuelveSuRol() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        char[] clave = "prueba-docente".toCharArray();
        control.registrarDocente("docente", "docente@example.test", "Dora", "Perez",
                NACIMIENTO, "Instituto", clave, clave);
        assertEquals(DTAutenticacion.Rol.DOCENTE,
                control.autenticarUsuario("docente", clave).getRol());
    }

    @Test
    public void altaInvalidaNoGuardaUsuarios() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        char[] clave = "prueba".toCharArray();
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, clave, "otra".toCharArray()));
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, new char[0], new char[0]));
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", LocalDate.now().plusDays(1), clave, clave));
        assertThrows(IllegalArgumentException.class, () -> control.registrarDocente(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, null, clave, clave));
        assertNull(cuenta.get());
    }

    @Test
    public void credencialesInvalidasYUsuarioAntiguoNoAutentican() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>(
                new Estudiante("antiguo", "a@example.test", "Ana", "Perez", NACIMIENTO));
        ControladorUsuario control = controlador(cuenta);
        String mensaje = assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("antiguo", "prueba".toCharArray())).getMessage();
        assertEquals(mensaje, assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("desconocido", "prueba".toCharArray())).getMessage());
        assertThrows(IllegalArgumentException.class, () -> control.autenticarUsuario(null, null));
        assertThrows(IllegalArgumentException.class, () -> control.autenticarUsuario("antiguo", new char[0]));
    }

    @Test
    public void seAsignaClaveAlUsuarioAnteriorYSeConservaAlEditarPerfil() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>(
                new Estudiante("antiguo", "a@example.test", "Ana", "Perez", NACIMIENTO));
        ControladorUsuario control = controlador(cuenta);
        char[] clave = "nueva clave".toCharArray();
        control.modificarUsuario("antiguo", "Ana", "Perez", NACIMIENTO, clave, clave);
        assertEquals("antiguo", control.autenticarUsuario("antiguo", clave).getNick());
        String hash = cuenta.get().getHashContrasena();
        control.modificarUsuario("antiguo", "Ana Maria", "Perez", NACIMIENTO, new char[0], new char[0]);
        assertEquals(hash, cuenta.get().getHashContrasena());
        assertEquals("Ana Maria", control.autenticarUsuario("antiguo", clave).getNombre());
        assertThrows(IllegalArgumentException.class, () -> control.modificarUsuario(
                "antiguo", "Ana", "Perez", NACIMIENTO, "otra".toCharArray(), clave));
        assertEquals(hash, cuenta.get().getHashContrasena());
    }

    @Test
    public void rechazaTiposQueNoSonEstudianteODocente() {
        Usuario base = new Usuario("base", "base@example.test", "Ana", "Perez", NACIMIENTO);
        base.setHashContrasena(Contrasenas.generarHash("prueba".toCharArray()));
        ControladorUsuario control = controlador(new AtomicReference<>(base));
        assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("base", "prueba".toCharArray()));
    }

    @Test
    public void hashUsaSalesDiferentesYRechazaDatosDañados() {
        char[] clave = "prueba".toCharArray();
        String hash = Contrasenas.generarHash(clave);
        assertNotEquals(hash, Contrasenas.generarHash(clave));
        assertTrue(Contrasenas.verificar(clave, hash));
        assertFalse(Contrasenas.verificar("otra".toCharArray(), hash));
        assertFalse(Contrasenas.verificar(clave, null));
        assertFalse(Contrasenas.verificar(null, hash));
        assertFalse(Contrasenas.verificar(clave, "pbkdf2-sha256$600000$???$???"));
        assertFalse(Contrasenas.verificar(clave, "texto sin hash"));
        assertFalse(Contrasenas.verificar(clave, "pbkdf2-sha256$600000$YQ==$YQ=="));
        assertThrows(IllegalArgumentException.class, () -> Contrasenas.generarHash("   ".toCharArray()));
    }
}
