package logica;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class InscripcionEstadoTest {
    @Test public void nuevasInscripcionesComienzanInscriptas() {
        assertEquals(EstadoInscripcion.INSCRIPTO, new Inscripcion().getEstado());
        Inscripcion inscripcion = new Inscripcion(LocalDate.now(), new EdicionCurso());
        assertEquals(EstadoInscripcion.INSCRIPTO, inscripcion.getEstado());
    }

    @Test public void permiteAceptarUnaInscripcionPendiente() {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.cambiarEstado(EstadoInscripcion.ACEPTADA);
        assertEquals(EstadoInscripcion.ACEPTADA, inscripcion.getEstado());
    }

    @Test public void permiteRechazarUnaInscripcionPendiente() {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.cambiarEstado(EstadoInscripcion.RECHAZADA);
        assertEquals(EstadoInscripcion.RECHAZADA, inscripcion.getEstado());
    }

    @Test public void rechazaDestinosInvalidosSinCambiarElEstado() {
        Inscripcion inscripcion = new Inscripcion();
        assertThrows(IllegalArgumentException.class, () -> inscripcion.cambiarEstado(null));
        assertThrows(IllegalArgumentException.class, () -> inscripcion.cambiarEstado(EstadoInscripcion.INSCRIPTO));
        assertEquals(EstadoInscripcion.INSCRIPTO, inscripcion.getEstado());
    }

    @Test public void noPermiteVolverAResolverUnaInscripcion() {
        for (EstadoInscripcion resuelto : new EstadoInscripcion[]{EstadoInscripcion.ACEPTADA, EstadoInscripcion.RECHAZADA}) {
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.cambiarEstado(resuelto);
            assertThrows(IllegalStateException.class, () -> inscripcion.cambiarEstado(EstadoInscripcion.ACEPTADA));
            assertThrows(IllegalStateException.class, () -> inscripcion.cambiarEstado(EstadoInscripcion.RECHAZADA));
            assertEquals(resuelto, inscripcion.getEstado());
        }
    }
}
