/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import logica.DTInscripcion;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTInscripcionSoap {

    private Long id;
    private String nicknameEstudiante;
    private String nombreEstudiante;
    private String nombreEdicion;
    private String nombreCurso;
    private String fechaInscripcion;
    private String estado;

    // Constructor obligatorio para JAXB.
    public DTInscripcionSoap() {
    }

    // Convierte el DTO del Central al DTO SOAP.
    public static DTInscripcionSoap desde(DTInscripcion origen) {

        DTInscripcionSoap dto = new DTInscripcionSoap();

        dto.id = origen.getId();
        dto.nicknameEstudiante = origen.getNicknameEstudiante();
        dto.nombreEstudiante = origen.getNombreEstudiante();
        dto.nombreEdicion = origen.getNombreEdicion();
        dto.nombreCurso = origen.getNombreCurso();

        dto.fechaInscripcion = origen.getFechaInscripcion() == null
                ? null
                : origen.getFechaInscripcion().toString();

        dto.estado = origen.getEstado() == null
                ? null
                : origen.getEstado().name();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getNicknameEstudiante() {
        return nicknameEstudiante;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public String getNombreEdicion() {
        return nombreEdicion;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }
}

