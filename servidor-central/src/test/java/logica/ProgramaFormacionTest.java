package logica;

import java.time.LocalDate;
import java.lang.reflect.Proxy;
import javax.persistence.EntityManagerFactory;
import persistencia.ControladorPersistencia;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProgramaFormacionTest {
    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 1);
    private static final LocalDate ALTA = LocalDate.of(2026, 9, 1);

    private static class PersistenciaEnMemoria extends ControladorPersistencia {
        PersistenciaEnMemoria() {
            // Las pruebas reemplazan las operaciones de programas y no usan JPA.
            super((EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(),
                    new Class<?>[]{EntityManagerFactory.class},
                    (proxy, metodo, argumentos) -> {
                        throw new AssertionError("Acceso inesperado a JPA: " + metodo.getName());
                    }));
        }
        private ProgramaFormacion guardado;
        private int escrituras;
        private RuntimeException fallo;

        @Override
        public boolean existeProgramaFormacion(String nombre) {
            return guardado != null && guardado.getNombre().equals(nombre);
        }

        @Override
        public void altaProgramaFormacion(ProgramaFormacion programa) {
            if (fallo != null) throw fallo;
            guardado = programa;
            escrituras++;
        }
    }

    @Test
    public void altaNormalizaTextoYConservaFechas() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        new ControladorProgramaFormacion(repo).altaProgramaFormacion(
                " Desarrollo ", " Formación en aplicaciones ", INICIO, FIN, ALTA);
        assertEquals(1, repo.escrituras);
        assertEquals("Desarrollo", repo.guardado.getNombre());
        assertEquals("Formación en aplicaciones", repo.guardado.getDescripcion());
        assertEquals(INICIO, repo.guardado.getFechaInicio());
        assertEquals(FIN, repo.guardado.getFechaFin());
        assertEquals(ALTA, repo.guardado.getFechaAlta());
        assertTrue(repo.guardado.getCursos().isEmpty());
    }

    @Test
    public void textosObligatoriosNoGuardan() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        ControladorProgramaFormacion control = new ControladorProgramaFormacion(repo);
        for (String texto : new String[]{null, "", "   "}) {
            assertThrows(IllegalArgumentException.class,
                    () -> control.altaProgramaFormacion(texto, "Descripción", INICIO, FIN, ALTA));
            assertThrows(IllegalArgumentException.class,
                    () -> control.altaProgramaFormacion("Programa", texto, INICIO, FIN, ALTA));
        }
        assertEquals(0, repo.escrituras);
    }

    @Test
    public void fechasAusentesOInvertidasNoGuardan() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        ControladorProgramaFormacion control = new ControladorProgramaFormacion(repo);
        assertThrows(IllegalArgumentException.class,
                () -> control.altaProgramaFormacion("Programa", "Descripción", null, FIN, ALTA));
        assertThrows(IllegalArgumentException.class,
                () -> control.altaProgramaFormacion("Programa", "Descripción", INICIO, null, ALTA));
        assertThrows(IllegalArgumentException.class,
                () -> control.altaProgramaFormacion("Programa", "Descripción", INICIO, FIN, null));
        assertThrows(IllegalArgumentException.class,
                () -> control.altaProgramaFormacion("Programa", "Descripción", FIN, INICIO, ALTA));
        assertEquals(0, repo.escrituras);
    }

    @Test
    public void permiteInicioYFinElMismoDia() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        new ControladorProgramaFormacion(repo).altaProgramaFormacion(
                "Jornada", "Un día", INICIO, INICIO, ALTA);
        assertEquals(1, repo.escrituras);
    }

    @Test
    public void nombreRepetidoNoReemplazaElPrograma() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        ControladorProgramaFormacion control = new ControladorProgramaFormacion(repo);
        control.altaProgramaFormacion("Programa", "Original", INICIO, FIN, ALTA);
        ProgramaFormacion original = repo.guardado;
        assertThrows(IllegalArgumentException.class,
                () -> control.altaProgramaFormacion(" Programa ", "Otra", INICIO, FIN, ALTA));
        assertSame(original, repo.guardado);
        assertEquals(1, repo.escrituras);
    }

    @Test
    public void falloDeGuardadoNoSePresentaComoAltaExitosa() {
        PersistenciaEnMemoria repo = new PersistenciaEnMemoria();
        repo.fallo = new IllegalStateException("Almacenamiento no disponible");
        RuntimeException error = assertThrows(IllegalStateException.class,
                () -> new ControladorProgramaFormacion(repo).altaProgramaFormacion(
                        "Programa", "Descripción", INICIO, FIN, ALTA));
        assertSame(repo.fallo, error);
        assertNull(repo.guardado);
        assertEquals(0, repo.escrituras);
    }
}
