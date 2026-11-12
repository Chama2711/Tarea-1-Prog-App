package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/** Datos públicos del curso que se muestran dentro de un programa. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTCursoResumen {
    private String nombre;
    private String descripcion;

    public DTCursoResumen() { }
    public DTCursoResumen(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
