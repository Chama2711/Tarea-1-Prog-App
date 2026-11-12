package logica;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import soap.dto.AdaptadorFecha;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTActaEstudio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreEstudiante;
    private String nickEstudiante;
    private String nombrePrograma;

    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaEmision;

    private List<DTCursoAprobado> cursosAprobados;

    // Constructor vacío requerido por JAXB
    public DTActaEstudio() {
    }

    public DTActaEstudio(String nombreEstudiante, String nickEstudiante, String nombrePrograma,
                         LocalDate fechaEmision, List<DTCursoAprobado> cursosAprobados) {
        this.nombreEstudiante = nombreEstudiante;
        this.nickEstudiante = nickEstudiante;
        this.nombrePrograma = nombrePrograma;
        this.fechaEmision = fechaEmision;
        this.cursosAprobados = cursosAprobados;
    }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getNickEstudiante() { return nickEstudiante; }
    public void setNickEstudiante(String nickEstudiante) { this.nickEstudiante = nickEstudiante; }

    public String getNombrePrograma() { return nombrePrograma; }
    public void setNombrePrograma(String nombrePrograma) { this.nombrePrograma = nombrePrograma; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public List<DTCursoAprobado> getCursosAprobados() { return cursosAprobados; }
    public void setCursosAprobados(List<DTCursoAprobado> cursosAprobados) { this.cursosAprobados = cursosAprobados; }

    @XmlTransient
    public String getNickname() {
        return nickEstudiante;
    }
}