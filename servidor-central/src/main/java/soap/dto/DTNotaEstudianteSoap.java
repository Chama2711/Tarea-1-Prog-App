package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import logica.DTNotaEstudiante;

@XmlAccessorType(XmlAccessType.FIELD)
public final class DTNotaEstudianteSoap {
    private String curso;
    private String edicion;
    private String estado;
    private Integer nota;
    private String fechaCalificacion;

    public DTNotaEstudianteSoap() { }

    public DTNotaEstudianteSoap(DTNotaEstudiante dato) {
        curso = dato.getCurso();
        edicion = dato.getEdicion();
        estado = dato.getEstado().name();
        nota = dato.getNota();
        fechaCalificacion = dato.getFechaCalificacion() == null ? null : dato.getFechaCalificacion().toString();
    }
}
