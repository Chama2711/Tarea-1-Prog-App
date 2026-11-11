/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidacionProgramaServidorCentralTest {

    private static class PersistenciaPrueba
            extends PersistenciaLogicaPrueba {

        boolean inscripto = true;

        @Override
        public Estudiante obtenerEstudianteConEvaluaciones(String nick) {
            Usuario usuario = usuarios.get(nick);
            return usuario instanceof Estudiante e ? e : null;
        }

        @Override
        public boolean estaInscriptoAPrograma(
                String nick, Long programaId) {
            return inscripto;
        }
    }

    private PersistenciaPrueba persistencia;
    private ServidorCentral central;
    private Estudiante estudiante;
    private ProgramaFormacion programa;

    @Before
    public void preparar() {
        persistencia = new PersistenciaPrueba();
        central = new ServidorCentral(persistencia);

        LocalDate fecha = LocalDate.of(2026, 11, 10);

        estudiante = new Estudiante(
                "chechi", "chechi@test.com",
                "Chechi", "Prueba",
                LocalDate.of(2000, 1, 1));

        programa = new ProgramaFormacion(
                "EFI Ingeniería Mecánica",
                "Programa de prueba",
                fecha.minusMonths(6),
                fecha.plusMonths(6),
                fecha);

        persistencia.usuarios.put("chechi", estudiante);
        persistencia.programas.put(
                programa.getNombre(), programa);
    }

    private void agregarCursoEvaluado(String nombre, int nota) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        programa.agregarCurso(curso);

        LocalDate fecha = LocalDate.of(2026, 11, 10);

        EdicionCurso edicion = new EdicionCurso();
        edicion.setNombre(nombre + " - 2026");
        edicion.setCurso(curso);

        Inscripcion inscripcion = new Inscripcion(fecha, edicion);
        inscripcion.cambiarEstado(EstadoInscripcion.ACEPTADA);
        inscripcion.registrarCalificacion(nota, fecha);

        edicion.cerrar();
        estudiante.agregoInscripcion(inscripcion);
    }

    @Test
    public void validaProgramaAprobado() {
        agregarCursoEvaluado("Dalavuelta", 10);
        agregarCursoEvaluado("Extensionismo Industrial", 11);
        agregarCursoEvaluado("Inclusión Energética", 8);

        var resultado = central.validarAprobacionPrograma(
                "chechi", "EFI Ingeniería Mecánica", 8);

        assertTrue(resultado.aprobado());
        assertEquals(3, resultado.totalCursos());
        assertEquals(3, resultado.cursosAprobados());
        assertEquals("chechi", resultado.estudiante());
    }

    @Test
    public void programaIncompletoNoAprueba() {
        agregarCursoEvaluado("Dalavuelta", 10);

        Curso pendiente = new Curso();
        pendiente.setNombre("Inclusión Energética");
        programa.agregarCurso(pendiente);

        var resultado = central.validarAprobacionPrograma(
                "chechi", "EFI Ingeniería Mecánica", 8);

        assertFalse(resultado.aprobado());
        assertEquals(2, resultado.totalCursos());
        assertEquals(1, resultado.cursosAprobados());
    }

    @Test(expected = RecursoNoEncontradoException.class)
    public void rechazaProgramaInexistente() {
        central.validarAprobacionPrograma(
                "chechi", "Programa inexistente", 8);
    }

    @Test(expected = RecursoNoEncontradoException.class)
    public void rechazaEstudianteInexistente() {
        central.validarAprobacionPrograma(
                "estudiante999", "EFI Ingeniería Mecánica", 8);
    }

    @Test(expected = IllegalStateException.class)
    public void rechazaEstudianteNoInscripto() {
        persistencia.inscripto = false;

        central.validarAprobacionPrograma(
                "chechi", "EFI Ingeniería Mecánica", 8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaNickVacio() {
        central.validarAprobacionPrograma(
                " ", "EFI Ingeniería Mecánica", 8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaNombreProgramaVacio() {
        central.validarAprobacionPrograma(
                "chechi", " ", 8);
    }
}

