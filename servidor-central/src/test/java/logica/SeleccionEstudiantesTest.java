package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

public class SeleccionEstudiantesTest {
    private static class Escenario {
        final EdicionCurso edicion = new EdicionCurso("Edición", LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1), 1, LocalDate.now());
        final Inscripcion primera = new Inscripcion(LocalDate.now(), edicion);
        final Inscripcion segunda = new Inscripcion(LocalDate.now(), edicion);
        long aceptadas;
        boolean activa, confirmada, revertida, cerrada;
        int accesos;
        final ControladorInscripcion controlador;

        Escenario() {
            edicion.agregoDocente(new Docente("docente", "docente@test", "Docente", "Uno", LocalDate.of(1980, 1, 1)));
            EntityTransaction tx = (EntityTransaction) Proxy.newProxyInstance(
                    EntityTransaction.class.getClassLoader(), new Class[]{EntityTransaction.class},
                    (proxy, metodo, args) -> {
                        switch (metodo.getName()) {
                            case "begin": activa = true; return null;
                            case "commit": confirmada = true; activa = false; return null;
                            case "rollback": revertida = true; activa = false; return null;
                            case "isActive": return activa;
                            default: throw new AssertionError(metodo.getName());
                        }
                    });
            TypedQuery<?> consulta = (TypedQuery<?>) Proxy.newProxyInstance(
                    TypedQuery.class.getClassLoader(), new Class[]{TypedQuery.class},
                    (proxy, metodo, args) -> switch (metodo.getName()) {
                        case "setParameter" -> proxy;
                        case "getSingleResult" -> aceptadas;
                        default -> throw new AssertionError(metodo.getName());
                    });
            EntityManager em = (EntityManager) Proxy.newProxyInstance(
                    EntityManager.class.getClassLoader(), new Class[]{EntityManager.class},
                    (proxy, metodo, args) -> {
                        switch (metodo.getName()) {
                            case "getTransaction": return tx;
                            case "find":
                                if (args[0] == EdicionCurso.class) return "Edición".equals(args[1]) ? edicion : null;
                                return Long.valueOf(1).equals(args[1]) ? primera : Long.valueOf(2).equals(args[1]) ? segunda : null;
                            case "createQuery": return consulta;
                            case "close": cerrada = true; return null;
                            default: throw new AssertionError(metodo.getName());
                        }
                    });
            EntityManagerFactory emf = (EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(), new Class[]{EntityManagerFactory.class},
                    (proxy, metodo, args) -> {
                        if (!metodo.getName().equals("createEntityManager")) throw new AssertionError(metodo.getName());
                        accesos++;
                        return em;
                    });
            controlador = new ControladorInscripcion(new ControladorPersistencia(emf));
        }
    }

    @Test public void guardaAceptacionYRechazoEnUnaSeleccion() {
        Escenario s = new Escenario();
        s.controlador.seleccionarEstudiantes("docente", "Edición",
                Map.of(1L, EstadoInscripcion.ACEPTADA, 2L, EstadoInscripcion.RECHAZADA));
        assertEquals(EstadoInscripcion.ACEPTADA, s.primera.getEstado());
        assertEquals(EstadoInscripcion.RECHAZADA, s.segunda.getEstado());
        assertTrue(s.confirmada && s.cerrada);
        assertFalse(s.revertida);
    }

    @Test public void docenteAjenoNoPuedeSeleccionar() {
        Escenario s = new Escenario();
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(
                "otro", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA)));
        assertEquals(EstadoInscripcion.INSCRIPTO, s.primera.getEstado());
        assertTrue(s.revertida && s.cerrada);
        assertFalse(s.confirmada);
    }

    @Test public void rechazaEdicionFinalizada() {
        Escenario s = new Escenario();
        s.edicion.setFechaFin(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(
                "docente", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA)));
        assertEquals(EstadoInscripcion.INSCRIPTO, s.primera.getEstado());
        assertTrue(s.revertida);
    }

    @Test public void noGuardaParcialmenteCuandoElLoteSuperaElCupo() {
        Escenario s = new Escenario();
        Map<Long, EstadoInscripcion> lote = new LinkedHashMap<>();
        lote.put(1L, EstadoInscripcion.ACEPTADA);
        lote.put(2L, EstadoInscripcion.ACEPTADA);
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes("docente", "Edición", lote));
        assertEquals(EstadoInscripcion.INSCRIPTO, s.primera.getEstado());
        assertEquals(EstadoInscripcion.INSCRIPTO, s.segunda.getEstado());
        assertTrue(s.revertida && s.cerrada);
        assertFalse(s.confirmada);
    }

    @Test public void cuentaAceptadosExistentesPeroPermiteRechazarConCupoLleno() {
        Escenario s = new Escenario();
        s.aceptadas = 1;
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(
                "docente", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA)));
        s.controlador.seleccionarEstudiantes("docente", "Edición", Map.of(1L, EstadoInscripcion.RECHAZADA));
        assertEquals(EstadoInscripcion.RECHAZADA, s.primera.getEstado());
    }

    @Test public void sinLimitePermiteAceptarVariasInscripciones() {
        Escenario s = new Escenario();
        s.edicion.setCupo(-1);
        s.controlador.seleccionarEstudiantes("docente", "Edición",
                Map.of(1L, EstadoInscripcion.ACEPTADA, 2L, EstadoInscripcion.ACEPTADA));
        assertEquals(EstadoInscripcion.ACEPTADA, s.primera.getEstado());
        assertEquals(EstadoInscripcion.ACEPTADA, s.segunda.getEstado());
    }

    @Test public void noSeleccionaInscripcionAjenaONoExistente() {
        Escenario s = new Escenario();
        s.primera.setEdicionCurso(new EdicionCurso("Otra", LocalDate.now(), LocalDate.now(), -1, LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(
                "docente", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA)));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(
                "docente", "Edición", Map.of(99L, EstadoInscripcion.ACEPTADA)));
        assertEquals(EstadoInscripcion.INSCRIPTO, s.primera.getEstado());
    }

    @Test public void noVuelveAResolverUnaInscripcion() {
        Escenario s = new Escenario();
        s.primera.cambiarEstado(EstadoInscripcion.RECHAZADA);
        assertThrows(IllegalStateException.class, () -> s.controlador.seleccionarEstudiantes(
                "docente", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA)));
        assertEquals(EstadoInscripcion.RECHAZADA, s.primera.getEstado());
    }

    @Test public void datosInvalidosNoAccedenALaBase() {
        Escenario s = new Escenario();
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes(null, "Edición", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes("docente", "", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes("docente", "Edición", null));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes("docente", "Edición", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.seleccionarEstudiantes("docente", "Edición", Map.of(1L, EstadoInscripcion.INSCRIPTO)));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.listarInscripcionesEdicion("", "Edición"));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.listarAceptadosEdicion("docente", null));
        assertThrows(IllegalArgumentException.class, () -> s.controlador.listarResultadosInscripciones(null));
        assertEquals(0, s.accesos);
    }
}
