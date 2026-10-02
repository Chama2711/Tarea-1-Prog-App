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
        String clave = " clave de prueba ";
        control.registrarEstudiante(" alumno ", " alumno@example.test ",
                " Ana ", " Perez ", NACIMIENTO, clave, clave);
        assertEquals(clave, cuenta.get().getContrasena());
        assertEquals("Ana", control.autenticarUsuario("alumno", clave).getNombre());
        assertEquals("alumno", control.autenticarUsuario(" ALUMNO@EXAMPLE.TEST ", clave).getNick());
        assertEquals(DTAutenticacion.Rol.ESTUDIANTE,
                control.autenticarUsuario("alumno", clave).getRol());
        assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("alumno", "clave de prueba"));
    }

    @Test
    public void registroDocenteDevuelveSuRol() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        String clave = "prueba-docente";
        control.registrarDocente("docente", "docente@example.test", "Dora", "Perez",
                NACIMIENTO, "Instituto", clave, clave);
        assertEquals(DTAutenticacion.Rol.DOCENTE,
                control.autenticarUsuario("docente", clave).getRol());
    }

    @Test
    public void altaInvalidaNoGuardaUsuarios() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        String clave = "prueba";
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, clave, "otra"));
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, "", ""));
        assertThrows(IllegalArgumentException.class, () -> control.registrarEstudiante(
                "nick", "a@example.test", "Ana", "Perez", LocalDate.now().plusDays(1), clave, clave));
        assertThrows(IllegalArgumentException.class, () -> control.registrarDocente(
                "nick", "a@example.test", "Ana", "Perez", NACIMIENTO, null, clave, clave));
        assertNull(cuenta.get());
    }

    @Test
    public void credencialesInvalidasNoAutentican() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>(
                new Estudiante("antiguo", "a@example.test", "Ana", "Perez", NACIMIENTO));
        ControladorUsuario control = controlador(cuenta);
        String mensaje = assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("antiguo", "prueba")).getMessage();
        assertEquals(mensaje, assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("desconocido", "prueba")).getMessage());
        assertThrows(IllegalArgumentException.class, () -> control.autenticarUsuario(null, null));
        assertThrows(IllegalArgumentException.class, () -> control.autenticarUsuario("antiguo", ""));
    }

    @Test
    public void cambioDeClaveYEdicionDePerfilConservanLaContrasena() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>(
                new Estudiante("antiguo", "a@example.test", "Ana", "Perez", NACIMIENTO));
        ControladorUsuario control = controlador(cuenta);
        String clave = "nueva clave";
        control.modificarUsuario("antiguo", "Ana", "Perez", NACIMIENTO, clave, clave);
        assertEquals("antiguo", control.autenticarUsuario("antiguo", clave).getNick());
        String contrasena = cuenta.get().getContrasena();
        control.modificarUsuario("antiguo", "Ana Maria", "Perez", NACIMIENTO, "", "");
        assertEquals(contrasena, cuenta.get().getContrasena());
        assertEquals("Ana Maria", control.autenticarUsuario("antiguo", clave).getNombre());
        assertThrows(IllegalArgumentException.class, () -> control.modificarUsuario(
                "antiguo", "Ana", "Perez", NACIMIENTO, "otra", clave));
        assertEquals(contrasena, cuenta.get().getContrasena());
    }

    @Test
    public void rechazaTiposQueNoSonEstudianteODocente() {
        Usuario base = new Usuario("base", "base@example.test", "Ana", "Perez", NACIMIENTO);
        base.setContrasena("prueba");
        ControladorUsuario control = controlador(new AtomicReference<>(base));
        assertThrows(IllegalArgumentException.class,
                () -> control.autenticarUsuario("base", "prueba"));
    }

    @Test
    public void permiteUnaContrasenaDeUnSoloCaracter() {
        AtomicReference<Usuario> cuenta = new AtomicReference<>();
        ControladorUsuario control = controlador(cuenta);
        control.registrarEstudiante("alumno", "a@example.test", "Ana", "Perez",
                NACIMIENTO, "1", "1");
        assertEquals("1", cuenta.get().getContrasena());
        assertEquals("alumno", control.autenticarUsuario("alumno", "1").getNick());
    }
}
