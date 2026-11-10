/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class CierreEdicionCursoTest {

    private EdicionCurso crearEdicion() {
        return new EdicionCurso(
                "Edicion de prueba",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 11, 30),
                20,
                LocalDate.of(2026, 2, 1)
        );
    }

    private Inscripcion crearInscripcionAceptada(
            EdicionCurso edicion) {

        Inscripcion inscripcion = new Inscripcion(
                LocalDate.of(2026, 3, 5),
                edicion
        );

        inscripcion.cambiarEstado(
                EstadoInscripcion.ACEPTADA
        );

        return inscripcion;
    }

    @Test
    public void nuevaEdicionComienzaAbierta() {
        EdicionCurso edicion = crearEdicion();

        assertFalse(edicion.isCerrada());
    }

    @Test
    public void permiteCerrarUnaEdicionAbierta() {
        EdicionCurso edicion = crearEdicion();

        edicion.cerrar();

        assertTrue(edicion.isCerrada());
    }

    @Test
    public void impideCerrarDosVeces() {
        EdicionCurso edicion = crearEdicion();
        edicion.cerrar();

        try {
            edicion.cerrar();
            fail("No debe permitir cerrar dos veces.");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("cerrada"));
        }
    }

    @Test
    public void permiteCalificarAntesDelCierre() {
        EdicionCurso edicion = crearEdicion();
        Inscripcion inscripcion =
                crearInscripcionAceptada(edicion);

        inscripcion.registrarCalificacion(
                10,
                LocalDate.of(2026, 11, 6)
        );

        assertEquals(
                Integer.valueOf(10),
                inscripcion.getCalificacion()
        );
    }

    @Test
    public void impideCalificarDespuesDelCierre() {
        EdicionCurso edicion = crearEdicion();
        Inscripcion inscripcion =
                crearInscripcionAceptada(edicion);

        edicion.cerrar();

        try {
            inscripcion.registrarCalificacion(
                    10,
                    LocalDate.of(2026, 11, 6)
            );
            fail("No debe permitir registrar notas.");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("cerrada"));
        }

        assertNull(inscripcion.getCalificacion());
    }

    @Test
    public void impideModificarNotaDespuesDelCierre() {
        EdicionCurso edicion = crearEdicion();
        Inscripcion inscripcion =
                crearInscripcionAceptada(edicion);

        inscripcion.registrarCalificacion(
                8,
                LocalDate.of(2026, 11, 5)
        );

        edicion.cerrar();

        try {
            inscripcion.registrarCalificacion(
                    12,
                    LocalDate.of(2026, 11, 6)
            );
            fail("No debe modificar una nota cerrada.");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("cerrada"));
        }

        // La calificación original debe conservarse.
        assertEquals(
                Integer.valueOf(8),
                inscripcion.getCalificacion()
        );
    }
}
