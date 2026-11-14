package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

/** Las inscripciones remotas resuelven entidades exclusivamente dentro del central. */
public class InscripcionesRemotasTest {
    private static class Persistencia extends ControladorPersistencia {
        Usuario usuario = new Estudiante("ana", "ana@test", "Ana", "Pérez", LocalDate.of(2000, 1, 1));
        final EdicionCurso edicion = new EdicionCurso("Edición", LocalDate.now(), LocalDate.now().plusDays(30), -1, LocalDate.now());
        final ProgramaFormacion programa = new ProgramaFormacion("Programa", "Descripción", LocalDate.now(), LocalDate.now().plusDays(30), LocalDate.now());
        boolean existe = true;
        int guardados;
        Persistencia() {
            super((EntityManagerFactory) Proxy.newProxyInstance(EntityManagerFactory.class.getClassLoader(), new Class<?>[]{EntityManagerFactory.class},
                    (p, m, a) -> { throw new AssertionError("No se usa MySQL en esta prueba"); }));
        }
        @Override public Usuario obtenerUsuario(String nick) { assertEquals("ana", nick); return usuario; }
        @Override public EdicionCurso buscarEdicion(String nombre) { assertEquals("Edición", nombre); return existe ? edicion : null; }
        @Override public soap.dto.DTProgramaConsulta consultarPrograma(String nombre) {
            assertEquals("Programa", nombre);
            return existe ? new soap.dto.DTProgramaConsulta(1L, "Programa", "Descripción", null, null, null, false, List.of(), List.of()) : null;
        }
        @Override public ProgramaFormacion obtenerDetallePrograma(String nombre) { return programa; }
        @Override public void inscriboAEdicionCurso(Estudiante estudiante, EdicionCurso destino, LocalDate fecha) {
            assertSame(usuario, estudiante); assertSame(edicion, destino); assertEquals(LocalDate.now(), fecha); guardados++;
        }
        @Override public void inscriboAProgramaFormacion(Estudiante estudiante, ProgramaFormacion destino, LocalDate fecha) {
            assertSame(usuario, estudiante); assertSame(programa, destino); assertEquals(LocalDate.now(), fecha); guardados++;
        }
    }
    @Test public void ambasOperacionesResuelvenEstudianteYDestinoYUsanFechaDelCentral() {
        var p = new Persistencia(); var central = new ServidorCentral(p);
        central.inscribirEstudianteAEdicion("ana", "Edición"); central.inscribirEstudianteAPrograma("ana", "Programa");
        assertEquals(2, p.guardados);
    }
    @Test public void docenteOUsuarioInexistenteNoPuedeInscribirse() {
        for (Usuario usuario : new Usuario[]{new Docente(), null}) {
            var p = new Persistencia(); p.usuario = usuario; var central = new ServidorCentral(p);
            assertThrows(SecurityException.class, () -> central.inscribirEstudianteAEdicion("ana", "Edición"));
            assertThrows(SecurityException.class, () -> central.inscribirEstudianteAPrograma("ana", "Programa"));
            assertEquals(0, p.guardados);
        }
    }
    @Test public void destinoInexistenteTieneErrorDiferenciadoYNoGuarda() {
        var p = new Persistencia(); p.existe = false; var central = new ServidorCentral(p);
        assertThrows(RecursoNoEncontradoException.class, () -> central.inscribirEstudianteAEdicion("ana", "Edición"));
        assertThrows(RecursoNoEncontradoException.class, () -> central.inscribirEstudianteAPrograma("ana", "Programa"));
        assertEquals(0, p.guardados);
    }
    @Test public void identificadoresVaciosSeRechazanAntesDeConsultarPersistencia() {
        var p = new Persistencia(); var central = new ServidorCentral(p);
        for (String vacio : new String[]{null, "", " "}) {
            assertThrows(SecurityException.class, () -> central.inscribirEstudianteAEdicion(vacio, "Edición"));
            assertThrows(SecurityException.class, () -> central.inscribirEstudianteAPrograma(vacio, "Programa"));
            assertThrows(IllegalArgumentException.class, () -> central.inscribirEstudianteAEdicion("ana", vacio));
            assertThrows(IllegalArgumentException.class, () -> central.inscribirEstudianteAPrograma("ana", vacio));
        }
        assertEquals(0, p.guardados);
    }
}
