/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalificacionInscripcionTest {

    private Inscripcion inscripcionAceptada() {
        EdicionCurso edicion = new EdicionCurso();

        Inscripcion inscripcion = new Inscripcion(
                LocalDate.of(2026, 3, 1),
                edicion
        );

        inscripcion.cambiarEstado(
                EstadoInscripcion.ACEPTADA
        );

        return inscripcion;
    }

    @Test
    public void permiteRegistrarCalificacionValida() {
        Inscripcion inscripcion = inscripcionAceptada();

        LocalDate fecha = LocalDate.of(2026, 11, 5);

        inscripcion.registrarCalificacion(10, fecha);

        assertEquals(
                Integer.valueOf(10),
                inscripcion.getCalificacion()
        );

        assertEquals(
                fecha,
                inscripcion.getFechaCalificacion()
        );
    }

    @Test
    public void rechazaCalificacionesFueraDeRango() {
        Inscripcion inscripcion = inscripcionAceptada();

        assertThrows(IllegalArgumentException.class,
                () -> inscripcion.registrarCalificacion(
                        0, LocalDate.now()
                ));

        assertThrows(IllegalArgumentException.class,
                () -> inscripcion.registrarCalificacion(
                        13, LocalDate.now()
                ));

        assertNull(inscripcion.getCalificacion());
    }

    @Test
    public void rechazaEstudianteNoAceptado() {
        Inscripcion inscripcion = new Inscripcion(
                LocalDate.now(),
                new EdicionCurso()
        );

        assertThrows(IllegalStateException.class,
                () -> inscripcion.registrarCalificacion(
                        8, LocalDate.now()
                ));
    }

    @Test
    public void rechazaInscripcionSinEdicion() {
        Inscripcion inscripcion = new Inscripcion();

        inscripcion.cambiarEstado(
                EstadoInscripcion.ACEPTADA
        );

        assertThrows(IllegalStateException.class,
                () -> inscripcion.registrarCalificacion(
                        8, LocalDate.now()
                ));
    }

    @Test
    public void rechazaFechaNula() {
        Inscripcion inscripcion = inscripcionAceptada();

        assertThrows(IllegalArgumentException.class,
                () -> inscripcion.registrarCalificacion(8, null));
    }
}

