package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTInscripcionConsulta {
    public Long id;
    public String nicknameEstudiante;
    public String nombreEstudiante;
    public String nombreEdicion;
    public String nombreCurso;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    public LocalDate fechaInscripcion;
    public String estado;
    public String videoMotivacionUrl;
    public DTInscripcionConsulta() { }
    public DTInscripcionConsulta(logica.DTInscripcion dato) {
        id = dato.getId(); nicknameEstudiante = dato.getNicknameEstudiante();
        nombreEstudiante = dato.getNombreEstudiante(); nombreEdicion = dato.getNombreEdicion();
        nombreCurso = dato.getNombreCurso(); fechaInscripcion = dato.getFechaInscripcion();
        estado = dato.getEstado().name();
        videoMotivacionUrl = dato.getVideoMotivacionUrl();
    }
}
