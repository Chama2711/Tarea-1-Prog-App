package logica;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DTActaEstudioTest {

    @Test
    public void testDTCursoAprobadoGetters() {
        LocalDate fecha = LocalDate.of(2026, 6, 15);
        DTCursoAprobado curso = new DTCursoAprobado("Desarrollo Web", 10, fecha);

        assertEquals("Desarrollo Web", curso.getNombreCurso());
        assertEquals(10, curso.getNota());
        assertEquals(fecha, curso.getFechaAprobacion());
    }

    @Test
    public void testDTActaEstudioGetters() {
        LocalDate fecha = LocalDate.now();
        List<DTCursoAprobado> cursos = new ArrayList<>();
        cursos.add(new DTCursoAprobado("Bases de Datos", 12, fecha));

        DTActaEstudio acta = new DTActaEstudio("Adriano", "adri", "Programa Tecnólogo", fecha, cursos);

        assertEquals("Adriano", acta.getNombreEstudiante());
        assertEquals("adri", acta.getNickEstudiante());
        assertEquals("Programa Tecnólogo", acta.getNombrePrograma());
        assertEquals(fecha, acta.getFechaEmision());
        assertEquals(1, acta.getCursosAprobados().size());
        assertEquals("Bases de Datos", acta.getCursosAprobados().get(0).getNombreCurso());
    }
}