package logica;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Comparator;
import java.time.Clock;
import java.time.LocalDate;
import persistencia.ControladorPersistencia;

public class ControladorInscripcion {
    private final ControladorPersistencia persistencia;
    private final Clock reloj;

    public ControladorInscripcion(ControladorPersistencia persistencia) {
        this(persistencia, Clock.systemDefaultZone());
    }

    ControladorInscripcion(ControladorPersistencia persistencia, Clock reloj) {
        this.persistencia = Objects.requireNonNull(persistencia);
        this.reloj = Objects.requireNonNull(reloj);
    }

    public void seleccionarEstudiantes(String nickDocente, String nombreEdicion,
            Map<Long, EstadoInscripcion> decisiones) {
        validarIdentificadores(nickDocente, nombreEdicion);
        if (decisiones == null || decisiones.isEmpty()) {
            throw new IllegalArgumentException("Seleccione al menos una inscripción.");
        }
        for (var decision : decisiones.entrySet()) {
            if (decision.getKey() == null || decision.getValue() == null
                    || decision.getValue() == EstadoInscripcion.INSCRIPTO) {
                throw new IllegalArgumentException("Indique una inscripción y el estado Aceptada o Rechazada.");
            }
        }
        persistencia.seleccionarEstudiantes(nickDocente, nombreEdicion, decisiones);
    }

    public List<DTInscripcion> listarInscripcionesEdicion(String nickDocente, String nombreEdicion) {
        validarIdentificadores(nickDocente, nombreEdicion);
        return persistencia.listarInscripcionesEdicion(nickDocente, nombreEdicion);
    }

    public List<DTRankingInscripcion> listarInscripcionesParaSeleccion(String nickDocente,
            String nombreEdicion, OrdenInscripciones orden) {
        validarIdentificadores(nickDocente, nombreEdicion);
        if (orden == null) throw new IllegalArgumentException("Indique el orden de las inscripciones.");
        List<DTRankingInscripcion> ranking = persistencia.listarInscripcionesConPrioridad(
                nickDocente, nombreEdicion, LocalDate.now(reloj));
        Comparator<DTRankingInscripcion> porFecha = Comparator
                .comparing((DTRankingInscripcion fila) -> fila.getInscripcion().getFechaInscripcion())
                .thenComparing(fila -> fila.getInscripcion().getNicknameEstudiante())
                .thenComparing(fila -> fila.getInscripcion().getId());
        Comparator<DTRankingInscripcion> comparador = orden == OrdenInscripciones.PRIORIDAD
                ? Comparator.comparingLong(DTRankingInscripcion::getRechazosAnteriores)
                        .reversed().thenComparing(porFecha)
                : porFecha;
        return ranking.stream().sorted(comparador).toList();
    }

    public List<DTEdicionConsulta> listarEdicionesParaSeleccion(String nickDocente,
            String nombreInstituto, String nombreCurso) {
        if (nickDocente == null || nickDocente.isBlank() || nombreInstituto == null
                || nombreInstituto.isBlank() || nombreCurso == null || nombreCurso.isBlank()) {
            throw new IllegalArgumentException("Seleccione instituto y curso.");
        }
        return persistencia.listarEdicionesParaSeleccion(nickDocente, nombreInstituto,
                nombreCurso, LocalDate.now(reloj));
    }

    public List<DTInscripcion> listarAceptadosEdicion(String nickDocente, String nombreEdicion) {
        validarIdentificadores(nickDocente, nombreEdicion);
        return persistencia.listarAceptadosEdicion(nickDocente, nombreEdicion);
    }

    public List<DTInscripcion> listarResultadosInscripciones(String nickEstudiante) {
        if (nickEstudiante == null || nickEstudiante.isBlank()) {
            throw new IllegalArgumentException("Indique el estudiante.");
        }
        return persistencia.listarResultadosInscripciones(nickEstudiante);
    }

    private void validarIdentificadores(String nickDocente, String nombreEdicion) {
        if (nickDocente == null || nickDocente.isBlank()
                || nombreEdicion == null || nombreEdicion.isBlank()) {
            throw new IllegalArgumentException("Indique docente y edición.");
        }
    }
}
