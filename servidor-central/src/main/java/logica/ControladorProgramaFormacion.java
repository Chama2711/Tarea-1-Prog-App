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
        altaProgramaFormacion(nombre, descripcion, fechaInicio, fechaFin, fechaAlta, null);
    }

    public void altaProgramaFormacion(String nombre, String descripcion,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, byte[] imagen) {
        if (nombre == null || nombre.isBlank()
                || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Complete nombre y descripción.");
        }
       if (fechaInicio == null || fechaFin == null || fechaAlta == null) {
            throw new IllegalArgumentException(
            "Complete las fechas de inicio, fin y alta.");
        }

        // No permitir crear programas que comiencen en el pasado.
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser anterior a la fecha actual.");
        }

        // La finalización debe ser igual o posterior al inicio.
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
        if (imagen == null) persistencia.altaProgramaFormacion(programa);
        else persistencia.altaProgramaFormacion(programa, imagen);
    }
}
