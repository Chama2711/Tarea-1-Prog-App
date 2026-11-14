/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import logica.ValidadorAprobacionPrograma.ResultadoCurso;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTResultadoCursoProgramaSoap {

    private Long cursoId;
    private String nombreCurso;
    private boolean aprobado;
    private Integer calificacion;
    private String fechaResultado;

    public DTResultadoCursoProgramaSoap() {
        // Requerido por JAXB.
    }

    public static DTResultadoCursoProgramaSoap desde(
            ResultadoCurso origen) {

        if (origen == null) {
            throw new IllegalArgumentException(
                    "El resultado del curso es obligatorio.");
        }

        DTResultadoCursoProgramaSoap dto =
                new DTResultadoCursoProgramaSoap();

        dto.cursoId = origen.cursoId();
        dto.nombreCurso = origen.curso();
        dto.aprobado = origen.aprobado();
        dto.calificacion = origen.nota();

        dto.fechaResultado = origen.fechaResultado() == null
                ? null
                : origen.fechaResultado().toString();

        return dto;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public String getFechaResultado() {
        return fechaResultado;
    }
}
