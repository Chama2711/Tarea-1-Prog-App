package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Ejecuta las reglas y transacciones reales del controlador, sustituyendo la conexión JPA. */
public class InscripcionesPersistenciaTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        final LocalDate hoy = LocalDate.now();
        final Estudiante estudiante = new Estudiante("ana", "ana@test", "Ana", "Pérez", hoy.minusYears(25));
        final EdicionCurso edicion = new EdicionCurso("Edición", hoy.minusDays(1), hoy.plusDays(1), 1, hoy);
        final ProgramaFormacion programa = new ProgramaFormacion("Programa", "Formación", hoy.minusDays(14), hoy.plusDays(30), hoy.minusMonths(1)) {
            @Override public Long getId() { return 9L; }
        };
        long repetidas;
        int accesos, commits, rollbacks;
        boolean activa, cerrada, existeEstudiante = true, existeDestino = true, falloGuardado;
        Inscripcion guardada;
        final EntityTransaction tx = proxy(EntityTransaction.class, (p, m, a) -> switch (m.getName()) {
            case "begin" -> { activa = true; yield null; }
            case "isActive" -> activa;
            case "commit" -> { activa = false; commits++; yield null; }
            case "rollback" -> { activa = false; rollbacks++; yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final EntityManager em = proxy(EntityManager.class, (p, m, a) -> switch (m.getName()) {
            case "getTransaction" -> tx;
            case "close" -> { cerrada = true; yield null; }
            case "find" -> {
                if (a.length == 3) assertEquals(LockModeType.PESSIMISTIC_WRITE, a[2]);
                yield a[0] == Estudiante.class ? (existeEstudiante ? estudiante : null)
                        : !existeDestino ? null : a[0] == EdicionCurso.class ? edicion : programa;
            }
            case "createQuery" -> proxy(TypedQuery.class, (q, n, b) -> switch (n.getName()) {
                case "setParameter" -> q;
                case "getSingleResult" -> repetidas;
                default -> throw new AssertionError(n.getName());
            });
            case "persist" -> {
                if (falloGuardado) throw new PersistenceException("Fallo de almacenamiento simulado.");
                guardada = (Inscripcion) a[0]; yield null;
            }
            default -> throw new AssertionError(m.getName());
        });
        final ControladorPersistencia p = new ControladorPersistencia(proxy(EntityManagerFactory.class, (q, m, a) -> {
            if (!m.getName().equals("createEntityManager")) throw new AssertionError(m.getName());
            accesos++; return em;
        }));
        void inscribir(boolean aPrograma, LocalDate fecha) {
            if (aPrograma) p.inscriboAProgramaFormacion(estudiante, programa, fecha);
            else p.inscriboAEdicionCurso(estudiante, edicion, fecha);
        }
        void assertSinGuardado() {
            assertNull(guardada); assertTrue(estudiante.getInscripciones().isEmpty());
            assertEquals(0, commits); assertEquals(1, rollbacks); assertTrue(cerrada);
        }
    }

    @Test public void nuevasInscripcionesConservanDestinoFechaYEstadoPendiente() {
        for (boolean programa : new boolean[]{false, true}) {
            var s = new Escenario(); s.inscribir(programa, s.hoy);
            assertEquals(1, s.commits); assertEquals(0, s.rollbacks); assertTrue(s.cerrada);
            assertEquals(s.hoy, s.guardada.getFechaInscripcion());
            assertEquals(EstadoInscripcion.INSCRIPTO, s.guardada.getEstado());
            assertTrue(s.estudiante.getInscripciones().contains(s.guardada));
            if (programa) { assertSame(s.programa, s.guardada.getProgramaFormacion()); assertNull(s.guardada.getEdicionCurso()); }
            else { assertSame(s.edicion, s.guardada.getEdicionCurso()); assertNull(s.guardada.getProgramaFormacion()); }
        }
    }

    @Test public void duplicadoEnProgramaOEdicionRevierteSinAgregarOtraInscripcion() {
        for (boolean programa : new boolean[]{false, true}) {
            var s = new Escenario(); s.repetidas = 1;
            assertThrows(IllegalArgumentException.class, () -> s.inscribir(programa, s.hoy)); s.assertSinGuardado();
        }
    }

    @Test public void estudianteODestinoEliminadosSeRechazanSinInscribir() {
        for (boolean programa : new boolean[]{false, true}) for (boolean estudianteAusente : new boolean[]{false, true}) {
            var s = new Escenario();
            if (estudianteAusente) s.existeEstudiante = false; else s.existeDestino = false;
            assertThrows(IllegalArgumentException.class, () -> s.inscribir(programa, s.hoy)); s.assertSinGuardado();
        }
    }

    @Test public void fechasFuturasONulasYSeleccionIncompletaNoAbrenConexion() {
        for (boolean programa : new boolean[]{false, true}) {
            var s = new Escenario();
            assertThrows(IllegalArgumentException.class, () -> s.inscribir(programa, null));
            assertThrows(IllegalArgumentException.class, () -> s.inscribir(programa, s.hoy.plusDays(1)));
            assertThrows(IllegalArgumentException.class, () -> s.p.inscriboAEdicionCurso(null, s.edicion, s.hoy));
            assertThrows(IllegalArgumentException.class, () -> s.p.inscriboAEdicionCurso(s.estudiante, null, s.hoy));
            assertThrows(IllegalArgumentException.class, () -> s.p.inscriboAProgramaFormacion(null, s.programa, s.hoy));
            assertThrows(IllegalArgumentException.class, () -> s.p.inscriboAProgramaFormacion(s.estudiante, new ProgramaFormacion(), s.hoy));
            assertEquals(0, s.accesos);
        }
    }

    @Test public void edicionNoVigenteOFechaFueraDelPeriodoNoPermitenInscripcion() {
        for (boolean futura : new boolean[]{false, true}) {
            var s = new Escenario();
            if (futura) s.edicion.setFechaInicio(s.hoy.plusDays(1)); else s.edicion.setFechaFin(s.hoy.minusDays(1));
            assertThrows(IllegalArgumentException.class, () -> s.inscribir(false, s.hoy)); s.assertSinGuardado();
        }
        var s = new Escenario();
        assertThrows(IllegalArgumentException.class, () -> s.inscribir(false, s.hoy.minusDays(2))); s.assertSinGuardado();
    }

    @Test public void plazoDeProgramaIncluyeDiaCatorceYRechazaDiaQuince() {
        var permitido = new Escenario(); permitido.inscribir(true, permitido.hoy);
        assertEquals(1, permitido.commits);
        var vencido = new Escenario(); vencido.programa.setFechaInicio(vencido.hoy.minusDays(15));
        assertThrows(IllegalArgumentException.class, () -> vencido.inscribir(true, vencido.hoy)); vencido.assertSinGuardado();
        var finalizado = new Escenario(); finalizado.programa.setFechaFin(finalizado.hoy.minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> finalizado.inscribir(true, finalizado.hoy)); finalizado.assertSinGuardado();
    }

    @Test public void cupoDeAceptacionNoImpideInscribirsePeroUnCupoInvalidoSi() {
        var sinPlazas = new Escenario(); sinPlazas.edicion.setCupo(0); sinPlazas.inscribir(false, sinPlazas.hoy);
        assertEquals(EstadoInscripcion.INSCRIPTO, sinPlazas.guardada.getEstado()); assertEquals(1, sinPlazas.commits);
        var invalido = new Escenario(); invalido.edicion.setCupo(-2);
        assertThrows(IllegalArgumentException.class, () -> invalido.inscribir(false, invalido.hoy)); invalido.assertSinGuardado();
    }

    @Test public void errorAlPersistirRevierteYCierraAmbosTiposDeInscripcion() {
        for (boolean programa : new boolean[]{false, true}) {
            var s = new Escenario(); s.falloGuardado = true;
            assertThrows(RuntimeException.class, () -> s.inscribir(programa, s.hoy)); s.assertSinGuardado();
        }
    }
}
