package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

public class AltaCursoFechaTest {
    private static class PersistenciaDePrueba extends ControladorPersistencia {
        private Curso guardado;

        PersistenciaDePrueba() {
            super((EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(),
                    new Class<?>[]{EntityManagerFactory.class},
                    (proxy, metodo, argumentos) -> {
                        throw new AssertionError("Acceso inesperado a JPA");
                    }));
        }

        @Override public Instituto buscarInstituto(String nombre) {
            return new Instituto(nombre);
        }
        @Override public Categoria buscarCategoria(String nombre) {
            return new Categoria(nombre);
        }
        @Override public void altaCurso(Curso curso) { guardado = curso; }
    }

    @Test
    public void conservaLaFechaElegidaPorElAdministrador() {
        PersistenciaDePrueba persistencia = new PersistenciaDePrueba();
        LocalDate fecha = LocalDate.of(2026, 8, 10);
        new ControladorCurso(persistencia).altaCurso("Instituto", "Curso", "Descripción",
                "Un mes", 20, 5, "https://example.test", fecha, List.of(), List.of("Categoría"));
        assertNotNull(persistencia.guardado);
        assertEquals(fecha, persistencia.guardado.getFechaRegistro());
    }

    @Test
    public void sinFechaNoGuardaElCurso() {
        PersistenciaDePrueba persistencia = new PersistenciaDePrueba();
        assertThrows(IllegalArgumentException.class,
                () -> new ControladorCurso(persistencia).altaCurso("Instituto", "Curso",
                        "Descripción", "Un mes", 20, 5, "https://example.test",
                        null, List.of(), List.of("Categoría")));
        assertNull(persistencia.guardado);
    }

    @Test
    public void laOperacionSinFechaConservaLaFechaActual() {
        PersistenciaDePrueba persistencia = new PersistenciaDePrueba();
        LocalDate antes = LocalDate.now();
        new ControladorCurso(persistencia).altaCurso("Instituto", "Curso", "Descripción",
                "Un mes", 20, 5, "https://example.test", List.of(), List.of("Categoría"));
        LocalDate fecha = persistencia.guardado.getFechaRegistro();
        assertFalse(fecha.isBefore(antes));
        assertFalse(fecha.isAfter(LocalDate.now()));
    }
}
