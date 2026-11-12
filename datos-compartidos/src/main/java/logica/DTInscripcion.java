package logica;

import java.time.LocalDate;

public final class DTInscripcion {
    private final Long id;
    private final String nicknameEstudiante;
    private final String nombreEstudiante;
    private final String nombreEdicion;
    private final String nombreCurso;
    private final LocalDate fechaInscripcion;
    private final EstadoInscripcion estado;

    public DTInscripcion(Long id, String nicknameEstudiante, String nombreEstudiante,
            String nombreEdicion, String nombreCurso, LocalDate fechaInscripcion,
            EstadoInscripcion estado) {
        this.id = id;
        this.nicknameEstudiante = nicknameEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.nombreEdicion = nombreEdicion;
        this.nombreCurso = nombreCurso;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public String getNicknameEstudiante() { return nicknameEstudiante; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getNombreEdicion() { return nombreEdicion; }
    public String getNombreCurso() { return nombreCurso; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public EstadoInscripcion getEstado() { return estado; }
}
