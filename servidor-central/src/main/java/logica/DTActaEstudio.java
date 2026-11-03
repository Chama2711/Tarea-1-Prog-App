package logica;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class DTActaEstudio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombreEstudiante;
    private final String nickEstudiante;
    private final String nombrePrograma;
    private final LocalDate fechaEmision;
    private final List<DTCursoAprobado> cursosAprobados;

    public DTActaEstudio(String nombreEstudiante, String nickEstudiante, String nombrePrograma, 
                          LocalDate fechaEmision, List<DTCursoAprobado> cursosAprobados) {
        this.nombreEstudiante = nombreEstudiante;
        this.nickEstudiante = nickEstudiante;
        this.nombrePrograma = nombrePrograma;
        this.fechaEmision = fechaEmision;
        this.cursosAprobados = cursosAprobados;
    }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getNickEstudiante() { return nickEstudiante; }
    public String getNombrePrograma() { return nombrePrograma; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public List<DTCursoAprobado> getCursosAprobados() { return cursosAprobados; }

    public String getNickname() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}