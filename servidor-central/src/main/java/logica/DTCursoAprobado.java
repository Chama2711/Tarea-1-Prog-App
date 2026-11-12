package logica;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import soap.dto.AdaptadorFecha;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTCursoAprobado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreCurso;
    private int nota;

    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaAprobacion;

    // Constructor vacío requerido por JAXB
    public DTCursoAprobado() {
    }

    public DTCursoAprobado(String nombreCurso, int nota, LocalDate fechaAprobacion) {
        this.nombreCurso = nombreCurso;
        this.nota = nota;
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }
}