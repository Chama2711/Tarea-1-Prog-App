package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConsultaCursoTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }

    private static class Escenario {
        boolean cerrada, existe = true, fallar;
        String parametro;
        int consultas;
        final Curso curso = new Curso("Robótica & Educación", "Un mes", 20, 3,
                LocalDate.of(2026, 11, 4), "Descripción", "https://example.test") {
            @Override public Set<EdicionCurso> getEdiciones() {
                assertFalse("Las relaciones deben copiarse antes de cerrar JPA", cerrada);
                return super.getEdiciones();
            }
        };
        final ControladorPersistencia persistencia;

        Escenario() {
            curso.setInstituto(new Instituto("Eléctrica"));
            curso.setImagen("ruta-interna.png");
            curso.agregoPrevia(new Curso("Previa", "", 0, 0, null, "", ""));
            curso.agregarCategoria(new Categoria("Tecnología"));
            curso.getProgramasFormacion().add(new ProgramaFormacion("Programa ñ", "", null, null, null));
            var edicion = new EdicionCurso("Edición 2026", LocalDate.of(2026, 11, 5),
                    LocalDate.of(2026, 12, 5), -1, LocalDate.of(2026, 11, 1));
            edicion.setImagen("edicion-interna.png"); curso.agregoEdicion(edicion);
            EntityManager em = proxy(EntityManager.class, (p, m, a) -> switch (m.getName()) {
                case "close" -> { cerrada = true; yield null; }
                case "createQuery" -> {
                    consultas++;
                    assertEquals(Curso.class, a[1]);
                    assertTrue(((String) a[0]).contains("LOWER(c.nombre) = LOWER(:nombre)"));
                    yield proxy(TypedQuery.class, (q, n, b) -> switch (n.getName()) {
                        case "setParameter" -> { parametro = (String) b[1]; yield q; }
                        case "getResultList" -> {
                            if (fallar) throw new IllegalStateException("Consulta fallida");
                            yield existe ? List.of(curso) : List.of();
                        }
                        default -> throw new AssertionError(n.getName());
                    });
                }
                default -> throw new AssertionError("No se esperan escrituras: " + m.getName());
            });
            persistencia = new ControladorPersistencia(proxy(EntityManagerFactory.class, (p, m, a) -> {
                if (m.getName().equals("createEntityManager")) return em;
                throw new AssertionError(m.getName());
            }));
        }
    }

    @Test public void fachadaEntregaElCursoCompletoConRelacionesMaterializadas() {
        var s = new Escenario();
        var dto = new ServidorCentral(s.persistencia).consultarCurso("robótica & educación");
        assertTrue(s.cerrada); assertEquals("robótica & educación", s.parametro);
        assertEquals("Robótica & Educación", dto.getNombre()); assertEquals("Descripción", dto.getDescripcion());
        assertEquals("Un mes", dto.getDuracion()); assertEquals(20, dto.getCantidadHoras());
        assertEquals(3, dto.getCreditos()); assertEquals("https://example.test", dto.getUrl());
        assertEquals(LocalDate.of(2026, 11, 4), dto.getFechaRegistro());
        assertEquals("Eléctrica", dto.getNombreInstituto()); assertTrue(dto.isTieneImagen());
        assertEquals(List.of("Previa"), dto.getPrevias());
        assertEquals(List.of("Tecnología"), dto.getCategorias());
        assertEquals(List.of("Programa ñ"), dto.getProgramas());
        var edicion = dto.getEdiciones().get(0);
        assertEquals("Edición 2026", edicion.getNombre()); assertEquals(-1, edicion.getCupo());
        assertEquals(LocalDate.of(2026, 11, 5), edicion.getFechaInicio());
        assertEquals(LocalDate.of(2026, 12, 5), edicion.getFechaFin()); assertTrue(edicion.isTieneImagen());
        s.curso.getCategorias().clear();
        assertEquals(List.of("Tecnología"), dto.getCategorias());
    }

    @Test public void inexistenteOIdentificadorVacioNoDevuelveUnCursoInventado() {
        var s = new Escenario(); s.existe = false;
        assertNull(s.persistencia.consultarCurso("Inexistente")); assertTrue(s.cerrada);
        for (String nombre : Arrays.asList(null, "", " ")) {
            s = new Escenario(); assertNull(s.persistencia.consultarCurso(nombre));
            assertTrue(s.cerrada); assertEquals(0, s.consultas);
        }
    }

    @Test public void cursoSinInstitutoORelacionesTieneDatosOpcionalesVacios() {
        for (String imagen : Arrays.asList(null, " ")) {
            var s = new Escenario(); s.curso.setInstituto(null); s.curso.setImagen(imagen);
            s.curso.getPrevias().clear(); s.curso.getCategorias().clear();
            s.curso.getProgramasFormacion().clear(); s.curso.getEdiciones().iterator().next().setImagen(imagen);
            var dto = s.persistencia.consultarCurso("Curso");
            assertNull(dto.getNombreInstituto()); assertFalse(dto.isTieneImagen());
            assertTrue(dto.getPrevias().isEmpty()); assertTrue(dto.getCategorias().isEmpty());
            assertTrue(dto.getProgramas().isEmpty()); assertFalse(dto.getEdiciones().get(0).isTieneImagen());
        }
    }

    @Test public void errorDeConsultaCierraJPA() {
        var s = new Escenario(); s.fallar = true;
        assertThrows(IllegalStateException.class, () -> s.persistencia.consultarCurso("Curso"));
        assertTrue(s.cerrada);
    }
}
