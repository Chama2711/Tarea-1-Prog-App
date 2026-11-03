package logica;

import java.io.Serializable;
import java.time.LocalDate;

public class DTCursoAprobado implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String nombreCurso;
    private final int nota;
    private final LocalDate fechaAprobacion;

    public DTCursoAprobado(String nombreCurso, int nota, LocalDate fechaAprobacion) {
        this.nombreCurso = nombreCurso;
        this.nota = nota;
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getNombreCurso() { return nombreCurso; }
    public int getNota() { return nota; }
    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
}