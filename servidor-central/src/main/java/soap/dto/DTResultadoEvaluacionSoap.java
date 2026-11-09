/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import logica.DTResultadoEvaluacion;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTResultadoEvaluacionSoap {

    private Long idInscripcion;
    private String nicknameEstudiante;
    private String nombreEstudiante;
    private Integer calificacion;
    private String fechaCalificacion;

    // Constructor requerido por JAXB.
    public DTResultadoEvaluacionSoap() {
    }

    // Convertir el DTO lógico a un DTO SOAP.
    public static DTResultadoEvaluacionSoap desde(
            DTResultadoEvaluacion origen) {

        DTResultadoEvaluacionSoap dto =
                new DTResultadoEvaluacionSoap();

        dto.idInscripcion = origen.getIdInscripcion();
        dto.nicknameEstudiante = origen.getNicknameEstudiante();
        dto.nombreEstudiante = origen.getNombreEstudiante();
        dto.calificacion = origen.getCalificacion();

        dto.fechaCalificacion =
                origen.getFechaCalificacion() == null
                ? null
                : origen.getFechaCalificacion().toString();

        return dto;
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

    public String getFechaCalificacion() {
        return fechaCalificacion;
    }
}
