package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/** Datos de las ediciones que se muestran dentro del detalle de un curso. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTEdicionResumen {
    private String nombre;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaInicio;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaFin;
    private int cupo;
    private boolean tieneImagen;

    public DTEdicionResumen() { }

    public DTEdicionResumen(String nombre, LocalDate fechaInicio, LocalDate fechaFin,
            int cupo, boolean tieneImagen) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.tieneImagen = tieneImagen;
    }

    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getCupo() { return cupo; }
    public boolean isTieneImagen() { return tieneImagen; }
}
