package logica; 

import java.time.LocalDate;

public class DTResultadoEvaluacion {
    
    private Long idInscripcion; 
    private String nicknameEstudiante;
    private String nombreEstudiante;
    private Integer calificacion;
    private LocalDate fechaCalificacion;

    // Constructor vacío
    public DTResultadoEvaluacion() {
    }

    // Constructor con todos los parámetros
    public DTResultadoEvaluacion(Long idInscripcion, String nicknameEstudiante, String nombreEstudiante, Integer calificacion, LocalDate fechaCalificacion) {
        this.idInscripcion = idInscripcion;
        this.nicknameEstudiante = nicknameEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.calificacion = calificacion;
        this.fechaCalificacion = fechaCalificacion;
    }

    
    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public String getNicknameEstudiante() {
        return nicknameEstudiante;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public LocalDate getFechaCalificacion() {
        return fechaCalificacion;
    }
}