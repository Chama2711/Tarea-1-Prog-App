/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap.dto;

import java.time.LocalDate;
import java.util.List;
import logica.ValidadorAprobacionPrograma.ResultadoCurso;
import logica.ValidadorAprobacionPrograma.ResultadoPrograma;
import org.junit.Test;
import static org.junit.Assert.*;

public class DTValidacionProgramaSoapTest {

    @Test
    public void convierteProgramaAprobado() {
        LocalDate fecha = LocalDate.of(2026, 11, 10);

        List<ResultadoCurso> cursos = List.of(
                new ResultadoCurso(6L, "Dalavuelta",
                        true, 10, fecha),
                new ResultadoCurso(8L, "Extensionismo Industrial",
                        true, 11, fecha),
                new ResultadoCurso(7L, "Inclusión Energética",
                        true, 8, fecha)
        );

        ResultadoPrograma origen = new ResultadoPrograma(
                "chechi",
                "EFI Ingeniería Mecánica",
                true,
                3,
                3,
                cursos
        );

        DTValidacionProgramaSoap dto =
                DTValidacionProgramaSoap.desde(origen);

        assertEquals("chechi", dto.getEstudiante());
        assertEquals("EFI Ingeniería Mecánica",
                dto.getPrograma());

        assertTrue(dto.isAprobado());
        assertEquals(3, dto.getTotalCursos());
        assertEquals(3, dto.getCursosAprobados());
        assertEquals(3, dto.getResultados().length);

        DTResultadoCursoProgramaSoap curso =
                dto.getResultados()[2];

        assertEquals("Inclusión Energética",
                curso.getNombreCurso());
        assertEquals(Integer.valueOf(8),
                curso.getCalificacion());
        assertEquals("2026-11-10",
                curso.getFechaResultado());
    }

    @Test
    public void convierteCursoPendiente() {
        ResultadoCurso pendiente = new ResultadoCurso(
                7L,
                "Inclusión Energética",
                false,
                null,
                null
        );

        DTResultadoCursoProgramaSoap dto =
                DTResultadoCursoProgramaSoap.desde(pendiente);

        assertFalse(dto.isAprobado());
        assertNull(dto.getCalificacion());
        assertNull(dto.getFechaResultado());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaResultadoProgramaNulo() {
        DTValidacionProgramaSoap.desde(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaResultadoCursoNulo() {
        DTResultadoCursoProgramaSoap.desde(null);
    }
}
