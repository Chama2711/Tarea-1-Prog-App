package logica;

import java.time.LocalDate;

public final class DTNotaEstudiante {
    private final String curso;
    private final String edicion;
    private final EstadoInscripcion estado;
    private final Integer nota;
    private final LocalDate fechaCalificacion;

    public DTNotaEstudiante(String curso, String edicion, EstadoInscripcion estado,
            Integer nota, LocalDate fechaCalificacion) {
        this.curso = curso;
        this.edicion = edicion;
        this.estado = estado;
        this.nota = nota;
        this.fechaCalificacion = fechaCalificacion;
    }

    public String getCurso() { return curso; }
    public String getEdicion() { return edicion; }
    public EstadoInscripcion getEstado() { return estado; }
    public Integer getNota() { return nota; }
    public LocalDate getFechaCalificacion() { return fechaCalificacion; }
}
