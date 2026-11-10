package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Datos para listados y detalle de cursos; no contiene entidades JPA ni rutas de archivos. */
@XmlRootElement(name = "curso")
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTCursoConsulta {
    private String nombre;
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fechaRegistro;
    private String nombreInstituto;
    private boolean tieneImagen;
    @XmlElement(name = "previa")
    private List<String> previas = new ArrayList<>();
    @XmlElement(name = "categoria")
    private List<String> categorias = new ArrayList<>();
    @XmlElement(name = "programa")
    private List<String> programas = new ArrayList<>();
    @XmlElement(name = "edicion")
    private List<DTEdicionResumen> ediciones = new ArrayList<>();

    /** Constructor requerido para leer los datos XML. */
    public DTCursoConsulta() { }

    public DTCursoConsulta(String nombre, String descripcion, String duracion,
            int cantidadHoras, int creditos, String url, LocalDate fechaRegistro,
            String nombreInstituto, boolean tieneImagen, List<String> previas,
            List<String> categorias, List<String> programas, List<DTEdicionResumen> ediciones) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.nombreInstituto = nombreInstituto;
        this.tieneImagen = tieneImagen;
        this.previas = new ArrayList<>(previas);
        this.categorias = new ArrayList<>(categorias);
        this.programas = new ArrayList<>(programas);
        this.ediciones = new ArrayList<>(ediciones);
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public String getNombreInstituto() { return nombreInstituto; }
    public boolean isTieneImagen() { return tieneImagen; }
    public List<String> getPrevias() { return List.copyOf(previas); }
    public List<String> getCategorias() { return List.copyOf(categorias); }
    public List<String> getProgramas() { return List.copyOf(programas); }
    public List<DTEdicionResumen> getEdiciones() { return List.copyOf(ediciones); }
}
