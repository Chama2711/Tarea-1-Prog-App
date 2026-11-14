package persistencia;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import logica.Docente;
import logica.Estudiante;
import logica.Usuario;
import org.junit.Test;
import static org.junit.Assert.*;

public class InstitutosDocenteTest {
    private static class Escenario {
        Usuario usuario;
        boolean cerrado, consultado;
        int contextos;
        String nickConsultado;
        RuntimeException fallo;
        final ControladorPersistencia persistencia;

        Escenario(Usuario usuario) {
            this.usuario = usuario;
            TypedQuery<?> query = (TypedQuery<?>) Proxy.newProxyInstance(TypedQuery.class.getClassLoader(),
                    new Class<?>[]{TypedQuery.class}, (p, m, a) -> switch (m.getName()) {
                        case "setParameter" -> { nickConsultado = (String) a[1]; yield p; }
                        case "getResultList" -> {
                            if (fallo != null) throw fallo;
                            yield List.of("DISI", "INCO");
                        }
                        default -> throw new AssertionError(m.getName());
                    });
            EntityManager em = (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(),
                    new Class<?>[]{EntityManager.class}, (p, m, a) -> switch (m.getName()) {
                        case "find" -> usuario;
                        case "createQuery" -> { consultado = true; yield query; }
                        case "close" -> { cerrado = true; yield null; }
                        default -> throw new AssertionError(m.getName());
                    });
            EntityManagerFactory emf = (EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(), new Class<?>[]{EntityManagerFactory.class},
                    (p, m, a) -> {
                        if (!m.getName().equals("createEntityManager")) throw new AssertionError(m.getName());
                        contextos++; return em;
                    });
            persistencia = new ControladorPersistencia(emf);
        }
    }

    @Test public void sinIdentidadRechazaAntesDeAccederALaBase() {
        Escenario s = new Escenario(null);
        for (String nick : new String[]{null, "", " "}) {
            assertThrows(SecurityException.class, () -> s.persistencia.listarNombresInstitutosDocente(nick));
        }
        assertEquals(0, s.contextos);
    }

    @Test public void usuarioInexistenteOEstudianteNoPuedeConsultarComoDocente() {
        Usuario estudiante = new Estudiante("estudiante", "e@test", "Nombre", "Apellido", LocalDate.of(2000, 1, 1));
        for (Usuario usuario : new Usuario[]{null, estudiante}) {
            Escenario s = new Escenario(usuario);
            assertThrows(SecurityException.class, () -> s.persistencia.listarNombresInstitutosDocente("nick"));
            assertFalse(s.consultado); assertTrue(s.cerrado);
        }
    }

    @Test public void consultaInstitutosConIdentidadParametrizadaYCierraContexto() {
        Escenario s = new Escenario(new Docente("docente", "d@test", "Nombre", "Apellido", LocalDate.of(1980, 1, 1)));
        assertEquals(List.of("DISI", "INCO"), s.persistencia.listarNombresInstitutosDocente("docente"));
        assertEquals("docente", s.nickConsultado); assertTrue(s.cerrado);
    }

    @Test public void tambienCierraCuandoFallaLaConsulta() {
        Escenario s = new Escenario(new Docente("docente", "d@test", "Nombre", "Apellido", LocalDate.of(1980, 1, 1)));
        s.fallo = new IllegalStateException("Fallo de conexión");
        assertThrows(IllegalStateException.class, () -> s.persistencia.listarNombresInstitutosDocente("docente"));
        assertTrue(s.cerrado);
    }
}
