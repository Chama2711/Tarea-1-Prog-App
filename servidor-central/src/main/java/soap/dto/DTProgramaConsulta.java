package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Copia de un programa: sin entidades JPA ni rutas de imágenes. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTProgramaConsulta {
    private Long id;
    private String nombre;
    private String descripcion;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaInicio;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaFin;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaAlta;
    private boolean tieneImagen;
    @XmlElement(name = "curso")
    private List<DTCursoResumen> cursos = new ArrayList<>();
    @XmlElement(name = "categoria")
    private List<String> categorias = new ArrayList<>();

    public DTProgramaConsulta() { }
    public DTProgramaConsulta(Long id, String nombre, String descripcion, LocalDate fechaInicio,
            LocalDate fechaFin, LocalDate fechaAlta, boolean tieneImagen,
            List<DTCursoResumen> cursos, List<String> categorias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.tieneImagen = tieneImagen;
        this.cursos = new ArrayList<>(cursos);
        this.categorias = new ArrayList<>(categorias);
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public boolean isTieneImagen() { return tieneImagen; }
    public List<DTCursoResumen> getCursos() { return List.copyOf(cursos); }
    public List<String> getCategorias() { return List.copyOf(categorias); }
}
