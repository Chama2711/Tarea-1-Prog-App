package logica;

import java.time.LocalDate;
import java.util.Objects;
import persistencia.ControladorPersistencia;

public class ControladorProgramaFormacion {
    private final ControladorPersistencia persistencia;

    public ControladorProgramaFormacion(ControladorPersistencia persistencia) {
        this.persistencia = Objects.requireNonNull(persistencia);
    }

    public void altaProgramaFormacion(String nombre, String descripcion,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta) {
        if (nombre == null || nombre.isBlank()
                || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Complete nombre y descripción.");
        }
        if (fechaInicio == null || fechaFin == null || fechaAlta == null) {
            throw new IllegalArgumentException("Complete las fechas de inicio, fin y alta.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        String nombreNormalizado = nombre.trim();
        if (persistencia.existeProgramaFormacion(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un programa con ese nombre.");
        }
        ProgramaFormacion programa = new ProgramaFormacion(nombreNormalizado,
                descripcion.trim(), fechaInicio, fechaFin, fechaAlta);
        persistencia.altaProgramaFormacion(programa);
    }
}
