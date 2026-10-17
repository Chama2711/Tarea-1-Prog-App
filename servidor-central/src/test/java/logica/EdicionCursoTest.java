package logica;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class EdicionCursoTest {
    private EdicionCurso edicion(LocalDate inicio, LocalDate fin) {
        return new EdicionCurso("Edición", inicio, fin, 10, inicio);
    }

    @Test
    public void incluyeElDiaDeInicioYElDiaDeFin() {
        LocalDate hoy = LocalDate.now();
        assertTrue(edicion(hoy, hoy.plusDays(1)).esVigente());
        assertTrue(edicion(hoy.minusDays(1), hoy).esVigente());
        assertTrue(edicion(hoy, hoy).esVigente());
    }

    @Test
    public void excluyeDiasFueraDelPeriodo() {
        LocalDate hoy = LocalDate.now();
        assertFalse(edicion(hoy.plusDays(1), hoy.plusDays(2)).esVigente());
        assertFalse(edicion(hoy.minusDays(2), hoy.minusDays(1)).esVigente());
    }

    @Test public void validacionDirectaDeSeleccionRechazaDestinosNoDefinidos() {
        var e = edicion(LocalDate.now(), LocalDate.now());
        var i = new Inscripcion(LocalDate.now(), e);
        assertThrows(IllegalArgumentException.class, () -> e.validarSeleccion(i, null, 0));
        assertThrows(IllegalArgumentException.class, () -> e.validarSeleccion(i, EstadoInscripcion.INSCRIPTO, 0));
        assertEquals(EstadoInscripcion.INSCRIPTO, i.getEstado());
    }

    @Test public void participanteNuloNoSeAutorizaAunqueLaEdicionTengaDocentes() {
        var e = edicion(LocalDate.now(), LocalDate.now());
        e.agregoDocente(new Docente("doc", "doc@test", "Docente", "Uno", LocalDate.of(1980, 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> e.validarDocenteParticipante(null));
        e.validarDocenteParticipante("doc");
    }
}
