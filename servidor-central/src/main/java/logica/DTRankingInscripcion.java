package logica;

import java.util.Objects;

/** Datos inmutables del ranking; la prioridad no se persiste en la base. */
public final class DTRankingInscripcion {
    private final DTInscripcion inscripcion;
    private final long rechazosAnteriores;

    /** El contador debe incluir solo rechazos de ediciones finalizadas del mismo curso. */
    public DTRankingInscripcion(DTInscripcion inscripcion, long rechazosAnteriores) {
        this.inscripcion = Objects.requireNonNull(inscripcion, "Indique la inscripción.");
        if (rechazosAnteriores < 0) {
            throw new IllegalArgumentException("La cantidad de rechazos no puede ser negativa.");
        }
        this.rechazosAnteriores = rechazosAnteriores;
    }

    public DTInscripcion getInscripcion() { return inscripcion; }
    public long getRechazosAnteriores() { return rechazosAnteriores; }
    public double getPrioridad() { return rechazosAnteriores * 0.5; }
}
