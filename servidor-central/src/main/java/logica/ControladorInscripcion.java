package logica;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistencia.ControladorPersistencia;

public class ControladorInscripcion {
    private final ControladorPersistencia persistencia;

    public ControladorInscripcion(ControladorPersistencia persistencia) {
        this.persistencia = Objects.requireNonNull(persistencia);
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
