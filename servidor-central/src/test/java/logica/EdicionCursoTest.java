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
}
