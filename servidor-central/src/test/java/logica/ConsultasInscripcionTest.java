package logica;

import java.time.LocalDate;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConsultasInscripcionTest {
    private DTInscripcion inscripcion() {
        return new DTInscripcion(1L, "ana", "Ana Pérez", "Edición", "Curso", LocalDate.of(2026, 1, 1), EstadoInscripcion.INSCRIPTO);
    }
    @Test public void consultasDeInscripcionesYSeleccionConservanActorYDestino() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p); var fila = inscripcion();
        p.inscripciones = List.of(fila);
        assertEquals(List.of(fila), central.listarInscripcionesEdicion("doc", "Edición"));
        assertEquals("doc", p.ultimoActor); assertEquals("Edición", p.ultimaEdicion);
        assertEquals(List.of(fila), central.listarAceptadosEdicion("doc", "Edición"));
        assertEquals(List.of(fila), central.listarResultadosInscripciones("ana")); assertEquals("ana", p.ultimoActor);
        central.seleccionarEstudiantes("doc", "Edición", Map.of(1L, EstadoInscripcion.ACEPTADA));
        assertEquals(Map.of(1L, EstadoInscripcion.ACEPTADA), p.decisiones);
        assertEquals("doc", p.ultimoActor); assertEquals("Edición", p.ultimaEdicion);
        assertEquals(EstadoInscripcion.INSCRIPTO, fila.getEstado());
    }
    @Test public void consultasInvalidasNoLleganAlRepositorio() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        for (String vacio : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> central.listarAceptadosEdicion(vacio, "Edición"));
            assertThrows(IllegalArgumentException.class, () -> central.listarInscripcionesEdicion("doc", vacio));
            assertThrows(IllegalArgumentException.class, () -> central.listarResultadosInscripciones(vacio));
            assertThrows(IllegalArgumentException.class, () -> central.listarEdicionesParaSeleccion(vacio, "INCO", "Curso"));
            assertThrows(IllegalArgumentException.class, () -> central.listarEdicionesParaSeleccion("doc", vacio, "Curso"));
            assertThrows(IllegalArgumentException.class, () -> central.listarEdicionesParaSeleccion("doc", "INCO", vacio));
        }
        assertNull(p.ultimoActor); assertNull(p.ultimaFecha);
    }
    @Test public void seleccionConsultaEdicionesYRankingConFechaActualSinGuardar() {
        var p = new PersistenciaLogicaPrueba(); var central = new ServidorCentral(p);
        p.edicionesSeleccion = List.of(new DTEdicionConsulta("Edición", "Curso", LocalDate.now(), LocalDate.now(),
                LocalDate.now().minusDays(1), 3, true, false, List.of()));
        assertEquals(p.edicionesSeleccion, central.listarEdicionesParaSeleccion("doc", "INCO", "Curso"));
        assertEquals("doc", p.ultimoActor); assertEquals("INCO", p.ultimoInstituto); assertEquals(LocalDate.now(), p.ultimaFecha);
        p.ranking = List.of(new DTRankingInscripcion(inscripcion(), 2));
        assertEquals(1, central.listarInscripcionesParaSeleccion("doc", "Edición", OrdenInscripciones.PRIORIDAD).get(0).getPrioridad(), 0);
        assertEquals("Edición", p.ultimaEdicion); assertEquals(0, p.escrituras);
    }
}
