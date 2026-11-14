package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Detalle de edición apto para SOAP; conserva el DTO local utilizado por Swing. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTEdicionConsulta {
    private String nombre;
    private String nombreCurso;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate inicio;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate fin;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    private LocalDate publicacion;
    private int cupo;
    private boolean vigente;
    private boolean tieneImagen;
    @XmlElement(name = "docente")
    private List<DTDocenteResumen> docentes = new ArrayList<>();
    private Boolean cerrada;

    public DTEdicionConsulta() { }
    public DTEdicionConsulta(logica.DTEdicionConsulta edicion) {
        nombre = edicion.nombre();
        nombreCurso = edicion.nombreCurso();
        inicio = edicion.inicio();
        fin = edicion.fin();
        publicacion = edicion.publicacion();
        cupo = edicion.cupo();
        vigente = edicion.vigente();
        tieneImagen = edicion.tieneImagen();
        cerrada = edicion.cerrada();
        edicion.docentes().forEach(d -> docentes.add(new DTDocenteResumen(d.nick(), d.nombre(), d.apellido())));
    }
    public String getNombre() { return nombre; }
    public String getNombreCurso() { return nombreCurso; }
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFin() { return fin; }
    public LocalDate getPublicacion() { return publicacion; }
    public int getCupo() { return cupo; }
    public boolean isVigente() { return vigente; }
    public boolean isTieneImagen() { return tieneImagen; }
    public List<DTDocenteResumen> getDocentes() { return List.copyOf(docentes); }
}
