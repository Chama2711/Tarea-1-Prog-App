package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Las relaciones del programa deben quedar copiadas antes de cerrar JPA. */
public class ConsultaProgramaTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        boolean cerrada, existe = true, fallar;
        int consultas;
        String parametro;
        final ProgramaFormacion programa = new ProgramaFormacion("Programa & ñ", "Descripción",
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 1), LocalDate.of(2026, 10, 1)) {
            @Override public Map<String, Curso> getCursos() {
                assertFalse("No acceder a relaciones después de cerrar JPA", cerrada);
                return super.getCursos();
            }
        };
        final Curso curso = new Curso("Curso ñ", "", 1, 2, null, "Descripción curso", "");
        final ServidorCentral central;
        Escenario() {
            curso.agregarCategoria(new Categoria("Tecnología"));
            programa.agregarCurso(curso);
            var otro = new Curso("Otro", "", 1, 2, null, "Otro curso", "");
            otro.agregarCategoria(new Categoria("Tecnología")); programa.agregarCurso(otro);
            programa.setImagen("ruta-privada.png");
            EntityManager em = proxy(EntityManager.class, (p, m, a) -> switch (m.getName()) {
                case "close" -> { cerrada = true; yield null; }
                case "createQuery" -> {
                    consultas++; assertEquals(ProgramaFormacion.class, a[1]);
                    yield proxy(TypedQuery.class, (q, n, b) -> switch (n.getName()) {
                        case "setParameter" -> { parametro = (String) b[1]; yield q; }
                        case "getResultList" -> {
                            if (fallar) throw new IllegalStateException("Fallo de consulta");
                            yield existe ? List.of(programa) : List.of();
                        }
                        default -> throw new AssertionError(n.getName());
                    });
                }
                default -> throw new AssertionError("No se esperan escrituras: " + m.getName());
            });
            central = new ServidorCentral(new ControladorPersistencia(proxy(EntityManagerFactory.class,
                    (p, m, a) -> {
                        if (m.getName().equals("createEntityManager")) return em;
                        throw new AssertionError(m.getName());
                    })));
        }
    }

    @Test public void detalleCopiaCursosCategoriasYFechasAntesDeCerrarJpa() {
        var s = new Escenario(); var dto = s.central.consultarPrograma("Programa & ñ");
        assertTrue(s.cerrada); assertEquals("Programa & ñ", s.parametro);
        assertEquals("Programa & ñ", dto.getNombre()); assertEquals("Descripción", dto.getDescripcion());
        assertEquals(LocalDate.of(2026, 11, 1), dto.getFechaInicio());
        assertEquals(LocalDate.of(2026, 12, 1), dto.getFechaFin());
        assertEquals(LocalDate.of(2026, 10, 1), dto.getFechaAlta());
        assertTrue(dto.isTieneImagen()); assertEquals(List.of("Tecnología"), dto.getCategorias());
        assertEquals(2, dto.getCursos().size());
        assertEquals("Descripción curso", dto.getCursos().get(0).getDescripcion());
        s.curso.getCategorias().clear();
        assertEquals(List.of("Tecnología"), dto.getCategorias());
    }

    @Test public void listadoDevuelveDtosCompletosYListaVaciaSiNoHayProgramas() {
        var s = new Escenario(); assertEquals(2, s.central.listarProgramasConsulta().get(0).getCursos().size());
        assertTrue(s.cerrada);
        s = new Escenario(); s.existe = false;
        assertTrue(s.central.listarProgramasConsulta().isEmpty()); assertTrue(s.cerrada);
    }

    @Test public void inexistenteOVacioNoInventaUnProgramaYCierraJpa() {
        var s = new Escenario(); s.existe = false;
        assertNull(s.central.consultarPrograma("No existe")); assertTrue(s.cerrada);
        for (String nombre : Arrays.asList(null, "", " ")) {
            s = new Escenario(); assertNull(s.central.consultarPrograma(nombre));
            assertTrue(s.cerrada); assertEquals(0, s.consultas);
        }
    }

    @Test public void falloDeListadoODetalleSiempreCierraJpa() {
        var s = new Escenario(); s.fallar = true;
        assertThrows(IllegalStateException.class, s.central::listarProgramasConsulta); assertTrue(s.cerrada);
        var otro = new Escenario(); otro.fallar = true;
        assertThrows(IllegalStateException.class, () -> otro.central.consultarPrograma("Programa"));
        assertTrue(otro.cerrada);
    }
}
