/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidadorAprobacionProgramaTest {

    private ValidadorAprobacionPrograma validador;
    private Estudiante estudiante;
    private ProgramaFormacion programa;

    private final LocalDate hoy = LocalDate.of(2026, 11, 10);

    @Before
    public void preparar() {
        validador = new ValidadorAprobacionPrograma();

        estudiante = new Estudiante(
                "chechi", "Chechi", "Prueba",
                "Apellido", LocalDate.of(2000, 1, 1));

        programa = new ProgramaFormacion(
                "EFI Ingeniería Mecánica",
                "Programa de prueba",
                hoy, hoy.plusMonths(6), hoy);
    }

    private Curso crearCurso(String nombre) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        programa.agregarCurso(curso);
        return curso;
    }

    private void agregarEvaluacion(
            Curso curso,
            int nota,
            boolean cerrarEdicion) {

        EdicionCurso edicion = new EdicionCurso();
        edicion.setNombre(curso.getNombre() + " - 2026");
        edicion.setCurso(curso);

        Inscripcion inscripcion = new Inscripcion(hoy, edicion);

        inscripcion.cambiarEstado(
                EstadoInscripcion.ACEPTADA);

        inscripcion.registrarCalificacion(nota, hoy);

        if (cerrarEdicion) {
            edicion.cerrar();
        }

        estudiante.agregoInscripcion(inscripcion);
    }

    @Test
    public void programaCompletamenteAprobado() {
        Curso c1 = crearCurso("Dalavuelta");
        Curso c2 = crearCurso("Extensionismo Industrial");
        Curso c3 = crearCurso("Inclusión Energética");

        agregarEvaluacion(c1, 10, true);
        agregarEvaluacion(c2, 11, true);
        agregarEvaluacion(c3, 8, true);

        var resultado = validador.validar(
                estudiante, programa, 8);

        assertTrue(resultado.aprobado());
        assertEquals(3, resultado.totalCursos());
        assertEquals(3, resultado.cursosAprobados());
        assertEquals(3, resultado.resultados().size());
    }

    @Test
    public void faltaAprobarUnCurso() {
        Curso c1 = crearCurso("Dalavuelta");
        crearCurso("Inclusión Energética");

        agregarEvaluacion(c1, 10, true);

        var resultado = validador.validar(
                estudiante, programa, 8);

        assertFalse(resultado.aprobado());
        assertEquals(2, resultado.totalCursos());
        assertEquals(1, resultado.cursosAprobados());
    }

    @Test
    public void notaInsuficienteNoAprueba() {
        Curso curso = crearCurso("Extensionismo Industrial");

        agregarEvaluacion(curso, 3, true);

        var resultado = validador.validar(
                estudiante, programa, 8);

        assertFalse(resultado.aprobado());
        assertEquals(0, resultado.cursosAprobados());
    }

    @Test
    public void edicionAbiertaNoCuentaComoAprobada() {
        Curso curso = crearCurso("Dalavuelta");

        agregarEvaluacion(curso, 12, false);

        var resultado = validador.validar(
                estudiante, programa, 8);

        assertFalse(resultado.aprobado());
        assertEquals(0, resultado.cursosAprobados());
    }

    @Test
    public void programaSinCursosNoSeAprueba() {
        var resultado = validador.validar(
                estudiante, programa, 8);

        assertFalse(resultado.aprobado());
        assertEquals(0, resultado.totalCursos());
    }

    @Test(expected = IllegalArgumentException.class)
    public void notaMinimaFueraDeRango() {
        validador.validar(estudiante, programa, 13);
    }

    @Test
    public void inscripcionRechazadaNoCuenta() {
        Curso curso = crearCurso("Dalavuelta");

        EdicionCurso edicion = new EdicionCurso();
        edicion.setCurso(curso);

        Inscripcion inscripcion = new Inscripcion(hoy, edicion);
        inscripcion.cambiarEstado(
                EstadoInscripcion.RECHAZADA);

        estudiante.agregoInscripcion(inscripcion);

        var resultado = validador.validar(
                estudiante, programa, 8);

        assertFalse(resultado.aprobado());
    }
}

