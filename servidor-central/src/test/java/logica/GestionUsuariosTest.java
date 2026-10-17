package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class GestionUsuariosTest {
    private static final LocalDate NACIMIENTO = LocalDate.of(2000, 2, 3);

    @Test public void altasDeAmbosRolesConYSinImagenPermitenAutenticarse() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        byte[] foto = {1, 2, 3};
        central.registrarEstudiante(" ana ", " ana@test ", " Ana ", " Pérez ", NACIMIENTO, "clave", "clave");
        central.registrarEstudiante("ben", "ben@test", "Ben", "Dos", NACIMIENTO, "otra", "otra", foto);
        assertArrayEquals(foto, p.ultimaImagen);
        central.registrarDocente("doc", "doc@test", "Docente", "Uno", NACIMIENTO, "INCO", "doc", "doc");
        central.registrarDocente("doc2", "doc2@test", "Docente", "Dos", NACIMIENTO, "INCO", "doc2", "doc2", foto);
        assertEquals(4, p.escrituras); assertEquals("INCO", p.ultimoInstituto);
        assertArrayEquals(foto, p.ultimaImagen);
        assertEquals("ana@test", central.obtenerUsuario("ana").getMail());
        assertEquals("Pérez", central.obtenerUsuario("ana").getApellido());
        assertEquals(NACIMIENTO, central.obtenerUsuario("ana").getFechaNacimiento());
        assertEquals(DTAutenticacion.Rol.ESTUDIANTE, central.autenticarUsuario("ANA@test", "clave").getRol());
        assertEquals(DTAutenticacion.Rol.DOCENTE, central.autenticarUsuario("doc", "doc").getRol());
    }

    @Test public void modificacionDesdeFachadaYRecorridoAdministrativoMantienenIdentidad() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        central.registrarEstudiante("ana", "ana@test", "Ana", "Pérez", NACIMIENTO, "anterior", "anterior");
        central.modificarUsuario("ana", " Ana María ", " López ", NACIMIENTO, "nueva", "nueva");
        assertEquals("Ana María", central.autenticarUsuario("ana", "nueva").getNombre());
        var control = new ControladorUsuario(central);
        control.modificarDatosUsuario("ana", "Ana", "Pérez", NACIMIENTO);
        assertEquals("nueva", p.editado.getContrasena()); assertEquals("ana@test", p.editado.getMail());
        assertEquals("ana", p.editado.getNick()); assertEquals("Ana", p.editado.getNombre());
        assertSame(p.editado, control.obtenerUsuarioPorNickname("ana"));
    }

    @Test public void listadosExistenciaYCorreoUsanLasCuentasDisponibles() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        var control = new ControladorUsuario(central);
        assertTrue(control.obtenerNicknamesUsuarios().isEmpty()); assertFalse(control.existeCorreo("ana@test"));
        central.registrarEstudiante("ana", "ana@test", "Ana", "Pérez", NACIMIENTO, "1", "1");
        central.registrarDocente("doc", "doc@test", "Docente", "Uno", NACIMIENTO, "INCO", "1", "1");
        assertEquals(List.of("ana", "doc"), control.obtenerNicknamesUsuarios());
        assertEquals(List.of("ana", "doc"), control.listarNicknamesUsuarios());
        assertEquals(List.of("ana", "doc"), central.listarNicknamesUsuarios());
        assertTrue(control.existeNickname("ana")); assertFalse(control.existeNickname("inexistente"));
        assertTrue(control.existeCorreo("DOC@TEST")); assertFalse(control.existeCorreo("otro@test"));
        assertEquals(1, central.obtenerEstudiantes().size());
        assertEquals("doc", central.listarDocentes().get(0).getNick());
    }

    @Test public void consultasAdministrativasPasanElNicknameSinConfundirListados() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        var control = new ControladorUsuario(central);
        assertEquals(List.of("Curso"), control.listarCursosOEdicionesUsuario("ana")); assertEquals("ana", p.ultimoActor);
        assertEquals(List.of("Programa"), control.listarProgramasUsuario("ben")); assertEquals("ben", p.ultimoActor);
        assertEquals(List.of("Edición"), control.listarEdicionesDocente("doc")); assertEquals("doc", p.ultimoActor);
        p.institutos.put("INCO", new Instituto("INCO"));
        assertEquals(List.of("INCO"), control.obtenerNombresInstitutos());
        var sinInstitutos = (IServidorCentral) Proxy.newProxyInstance(IServidorCentral.class.getClassLoader(),
                new Class<?>[]{IServidorCentral.class}, (q, m, a) -> null);
        assertTrue(new ControladorUsuario(sinInstitutos).obtenerNombresInstitutos().isEmpty());
    }

    @Test public void erroresDeAltaNoSeOcultanComoExito() {
        for (boolean foto : new boolean[]{false, true}) {
            var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
            p.fallo = new IllegalArgumentException("Nickname duplicado.");
            assertSame(p.fallo, assertThrows(IllegalArgumentException.class, () -> central.registrarEstudiante(
                    "ana", "ana@test", "Ana", "Pérez", NACIMIENTO, "1", "1", foto ? new byte[]{1} : null)));
            assertTrue(p.usuarios.isEmpty());
            p.fallo = new IllegalStateException("Fallo del almacenamiento.");
            var e = assertThrows(IllegalStateException.class, () -> central.registrarEstudiante(
                    "ana", "ana@test", "Ana", "Pérez", NACIMIENTO, "1", "1", foto ? new byte[]{1} : null));
            assertEquals("No se pudo guardar el usuario.", e.getMessage()); assertSame(p.fallo, e.getCause());
        }
    }

    @Test public void perfilInexistenteOErrorDeGuardadoNoDevuelvenExito() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        assertThrows(IllegalArgumentException.class, () -> central.modificarUsuario("ausente", "Ana", "Pérez", NACIMIENTO, "", ""));
        assertEquals(0, p.escrituras);
        p.usuarios.put("ana", new Estudiante("ana", "ana@test", "Ana", "Pérez", NACIMIENTO));
        p.fallo = new IllegalStateException("Falló guardar.");
        var e = assertThrows(IllegalStateException.class, () -> central.modificarUsuario("ana", "Ana", "Pérez", NACIMIENTO, "", ""));
        assertEquals("No se pudo modificar el usuario.", e.getMessage()); assertSame(p.fallo, e.getCause());
        p.fallo = new IllegalArgumentException("Dato rechazado.");
        assertSame(p.fallo, assertThrows(IllegalArgumentException.class, () -> central.modificarUsuario("ana", "Ana", "Pérez", NACIMIENTO, "", "")));
    }

    @Test public void datosInvalidosEnModificacionNoLleganAlAlmacenamiento() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        for (String valor : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> central.modificarUsuario("ana", valor, "Pérez", NACIMIENTO, "", ""));
            assertThrows(IllegalArgumentException.class, () -> central.modificarUsuario("ana", "Ana", valor, NACIMIENTO, "", ""));
        }
        for (LocalDate fecha : new LocalDate[]{null, LocalDate.now().plusDays(1)})
            assertThrows(IllegalArgumentException.class, () -> central.modificarUsuario("ana", "Ana", "Pérez", fecha, "", ""));
        assertEquals(0, p.escrituras);
    }
}
