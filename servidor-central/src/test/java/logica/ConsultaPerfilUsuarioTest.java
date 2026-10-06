package logica;

import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConsultaPerfilUsuarioTest {
    private DTUsuarioConsulta usuario(DTAutenticacion.Rol rol) {
        return new DTUsuarioConsulta("ana", "Ana", "Pérez", "ana@example.com",
                LocalDate.of(2000, 1, 1), rol, false, List.of());
    }

    private DTInscripcion inscripcion(long id, EstadoInscripcion estado) {
        return new DTInscripcion(id, "ana", "Ana Pérez", "Edición", "Curso",
                LocalDate.of(2026, 10, 1), estado);
    }

    private DTPerfilUsuario perfil(String consultante, DTAutenticacion.Rol rol) {
        return DTPerfilUsuario.crear(usuario(rol), consultante,
                List.of(inscripcion(1, EstadoInscripcion.INSCRIPTO),
                        inscripcion(2, EstadoInscripcion.ACEPTADA),
                        inscripcion(3, EstadoInscripcion.RECHAZADA)),
                List.of(new DTEdicionConsulta("Edición", "Curso", LocalDate.now(),
                        LocalDate.now(), LocalDate.now(), -1,true, List.of())),
                List.of("Programa"), List.of(inscripcion(2, EstadoInscripcion.ACEPTADA)));
    }

    @Test public void visitanteNoVeRechazos() {
        DTPerfilUsuario perfil = perfil(null, DTAutenticacion.Rol.ESTUDIANTE);
        assertFalse(perfil.propio());
        assertEquals(List.of(EstadoInscripcion.INSCRIPTO, EstadoInscripcion.ACEPTADA),
                perfil.inscripciones().stream().map(DTInscripcion::getEstado).toList());
    }

    @Test public void otroUsuarioTampocoVeRechazos() {
        assertEquals(2, perfil("otra", DTAutenticacion.Rol.ESTUDIANTE).inscripciones().size());
    }

    @Test public void estudianteVeTodosSusResultados() {
        DTPerfilUsuario perfil = perfil("ana", DTAutenticacion.Rol.ESTUDIANTE);
        assertTrue(perfil.propio());
        assertEquals(3, perfil.inscripciones().size());
        assertTrue(perfil.ediciones().isEmpty());
        assertTrue(perfil.aceptados().isEmpty());
        assertEquals(List.of("Programa"), perfil.programas());
    }

    @Test public void aceptadosSoloEnPerfilPropioDelDocente() {
        DTPerfilUsuario publico = perfil("otra", DTAutenticacion.Rol.DOCENTE);
        assertEquals(1, publico.ediciones().size());
        assertTrue(publico.aceptados().isEmpty());
        assertTrue(publico.inscripciones().isEmpty());
        DTPerfilUsuario propio = perfil("ana", DTAutenticacion.Rol.DOCENTE);
        assertEquals(1, propio.aceptados().size());
        assertTrue(propio.programas().isEmpty());
    }

    @Test public void identidadDebeCoincidirExactamente() {
        assertFalse(perfil("ANA", DTAutenticacion.Rol.ESTUDIANTE).propio());
        assertEquals(2, perfil("ANA", DTAutenticacion.Rol.ESTUDIANTE).inscripciones().size());
    }
}
