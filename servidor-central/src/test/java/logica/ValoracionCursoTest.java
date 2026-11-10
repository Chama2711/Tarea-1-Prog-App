/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValoracionCursoTest {

    private static final LocalDate FECHA =
            LocalDate.of(2026, 11, 9);

    @Test
    public void registraValoracionValida() {

        Estudiante estudiante = new Estudiante();
        Curso curso = new Curso();

        ValoracionCurso valoracion =
                new ValoracionCurso(estudiante, curso, 3, FECHA);

        assertSame(estudiante, valoracion.getEstudiante());
        assertSame(curso, valoracion.getCurso());
        assertEquals(3, valoracion.getPuntuacion());
        assertEquals(FECHA, valoracion.getFechaValoracion());
    }

    @Test
    public void aceptaLimitesDePuntuacion() {

        ValoracionCurso minima = new ValoracionCurso(
                new Estudiante(), new Curso(), 1, FECHA);

        ValoracionCurso maxima = new ValoracionCurso(
                new Estudiante(), new Curso(), 5, FECHA);

        assertEquals(1, minima.getPuntuacion());
        assertEquals(5, maxima.getPuntuacion());
    }

    @Test
    public void rechazaPuntuacionMenorAUno() {

        assertThrows(IllegalArgumentException.class,
                () -> new ValoracionCurso(
                        new Estudiante(), new Curso(), 0, FECHA));
    }

    @Test
    public void rechazaPuntuacionMayorACinco() {

        assertThrows(IllegalArgumentException.class,
                () -> new ValoracionCurso(
                        new Estudiante(), new Curso(), 6, FECHA));
    }

    @Test
    public void rechazaEstudianteOCursoNulo() {

        assertThrows(IllegalArgumentException.class,
                () -> new ValoracionCurso(
                        null, new Curso(), 3, FECHA));

        assertThrows(IllegalArgumentException.class,
                () -> new ValoracionCurso(
                        new Estudiante(), null, 3, FECHA));
    }

    @Test
    public void rechazaFechaNula() {

        assertThrows(IllegalArgumentException.class,
                () -> new ValoracionCurso(
                        new Estudiante(), new Curso(), 3, null));
    }
}
