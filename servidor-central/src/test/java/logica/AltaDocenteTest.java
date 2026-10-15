package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

public class AltaDocenteTest {
    private static class PersistenciaDePrueba extends ControladorPersistencia {
        Curso curso;
        EdicionCurso edicion;

        PersistenciaDePrueba() {
            super((EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(), new Class<?>[]{EntityManagerFactory.class},
                    (p, m, a) -> { throw new AssertionError("Acceso inesperado a JPA"); }));
        }

        @Override public List<String> listarNombresInstitutosDocente(String nick) {
            if (!"docente".equals(nick)) throw new SecurityException("No es docente.");
            return List.of("INCO");
        }
        @Override public List<String> listarNicknamesDocentesPorInstituto(String instituto) {
            return List.of("docente", "otroDocente");
        }
        @Override public Instituto buscarInstituto(String nombre) { return new Instituto(nombre); }
        @Override public Categoria buscarCategoria(String nombre) { return new Categoria(nombre); }
        @Override public Curso buscarCursoInstituto(String nombre, Instituto instituto) {
            if (!"Curso".equals(nombre) && !"Previa de INCO".equals(nombre)) return null;
            Curso c = new Curso(); c.setNombre(nombre); c.setInstituto(instituto); return c;
        }
        @Override public Docente buscarDocentePorNick(String nick) {
            return new Docente(nick, "doc@test", "Docente", "Prueba", LocalDate.of(1980, 1, 1));
        }
        @Override public void altaCurso(Curso c) { curso = c; }
        @Override public void altaEdicionCurso(Curso c, EdicionCurso e) { edicion = e; }
    }

    private void altaCurso(ServidorCentral central, String nick, String instituto) {
        central.altaCursoDocente(nick, instituto, " Curso ", "Descripción", "Un mes", 20, 3,
                "https://example.test", List.of("Previa de INCO"), List.of("Tecnología"));
    }

    @Test public void cursoDeSuInstitutoUsaFechaActualYPreviasDelMismoInstituto() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        LocalDate antes = LocalDate.now();
        altaCurso(new ServidorCentral(p), "docente", " INCO ");
        assertEquals("Curso", p.curso.getNombre());
        assertEquals("INCO", p.curso.getInstituto().getNombre());
        assertEquals("Previa de INCO", p.curso.getPrevias().iterator().next().getNombre());
        assertFalse(p.curso.getFechaRegistro().isBefore(antes));
        assertFalse(p.curso.getFechaRegistro().isAfter(LocalDate.now()));
    }

    @Test public void noGuardaCursoDeUnInstitutoAjeno() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        assertThrows(SecurityException.class, () -> altaCurso(new ServidorCentral(p), "docente", "Física"));
        assertNull(p.curso);
    }

    @Test public void previaDeOtroInstitutoNoSeGuardaEnAltaDocenteNiAdministrativa() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        ServidorCentral central = new ServidorCentral(p);
        assertThrows(IllegalArgumentException.class, () -> central.altaCursoDocente("docente",
                "INCO", "Curso", "Descripción", "Un mes", 20, 3, "URL",
                List.of("Previa de Física"), List.of("Tecnología")));
        assertThrows(IllegalArgumentException.class, () -> central.altaCurso("INCO", "Curso",
                "Descripción", "Un mes", 20, 3, "URL", LocalDate.now(),
                List.of("Previa de Física"), List.of("Tecnología")));
        assertNull(p.curso);
    }

    @Test public void noAceptaUnActorQueNoEsDocente() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        assertThrows(SecurityException.class, () -> altaCurso(new ServidorCentral(p), "estudiante", "INCO"));
        assertNull(p.curso);
    }

    @Test public void cursoSinInstitutoNoSeGuarda() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        assertThrows(IllegalArgumentException.class, () -> altaCurso(new ServidorCentral(p), "docente", ""));
        assertNull(p.curso);
    }

    @Test public void cursoSinCategoriaNoSeGuarda() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        assertThrows(IllegalArgumentException.class, () -> new ServidorCentral(p).altaCursoDocente(
                "docente", "INCO", "Curso", "Descripción", "Un mes", 20, 3, "URL", List.of(), List.of()));
        assertNull(p.curso);
    }

    @Test public void edicionAceptaCupoCeroYSinLimite() {
        for (int cupo : List.of(0, -1, 5)) {
            PersistenciaDePrueba p = new PersistenciaDePrueba();
            new ServidorCentral(p).altaEdicionCursoDocente("docente", "INCO", "Curso", "Edición",
                    LocalDate.now(), LocalDate.now().plusDays(5), cupo, List.of("otroDocente"));
            assertEquals(cupo, p.edicion.getCupo());
            assertEquals("otroDocente", p.edicion.getDocentes().iterator().next().getNick());
        }
    }

    @Test public void edicionNoAceptaDocentesDeOtroInstituto() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        assertThrows(IllegalArgumentException.class, () -> new ServidorCentral(p).altaEdicionCursoDocente(
                "docente", "INCO", "Curso", "Edición", LocalDate.now(), LocalDate.now(), 5,
                List.of("docenteAjeno")));
        assertNull(p.edicion);
    }

    @Test public void edicionNoAceptaInstitutoAjenoNiCursoDeOtroInstituto() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        ServidorCentral central = new ServidorCentral(p);
        assertThrows(SecurityException.class, () -> central.altaEdicionCursoDocente("docente", "Física",
                "Curso", "Edición", LocalDate.now(), LocalDate.now(), 5, List.of()));
        assertThrows(IllegalArgumentException.class, () -> central.altaEdicionCursoDocente("docente", "INCO",
                "Curso ajeno", "Edición", LocalDate.now(), LocalDate.now(), 5, List.of()));
        assertNull(p.edicion);
    }

    @Test public void edicionNoAceptaFechasInvertidasNiCupoInvalido() {
        PersistenciaDePrueba p = new PersistenciaDePrueba();
        ServidorCentral central = new ServidorCentral(p);
        assertThrows(IllegalArgumentException.class, () -> central.altaEdicionCursoDocente("docente", "INCO",
                "Curso", "Edición", LocalDate.now().plusDays(1), LocalDate.now(), 5, List.of()));
        assertThrows(IllegalArgumentException.class, () -> central.altaEdicionCursoDocente("docente", "INCO",
                "Curso", "Edición", LocalDate.now(), LocalDate.now(), -2, List.of()));
        assertNull(p.edicion);
    }
}
