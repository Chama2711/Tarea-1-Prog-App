package logica;

import java.time.LocalDate;
import java.util.List;

/** Datos inmutables de consulta: no contienen credenciales ni entidades JPA. */
public final class DTEdicionConsulta {

    private final String nombre;
    private final String nombreCurso;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final LocalDate publicacion;
    private final int cupo;
    private final boolean vigente;
    private final List<DTUsuarioConsulta> docentes;

    public DTEdicionConsulta(
            String nombre,
            String nombreCurso,
            LocalDate inicio,
            LocalDate fin,
            LocalDate publicacion,
            int cupo,
            boolean vigente,
            List<DTUsuarioConsulta> docentes) {

        this.nombre = nombre;
        this.nombreCurso = nombreCurso;
        this.inicio = inicio;
        this.fin = fin;
        this.publicacion = publicacion;
        this.cupo = cupo;
        this.vigente = vigente;
        this.docentes = List.copyOf(docentes);
    }

    public String nombre() { return nombre; }
    public String nombreCurso() { return nombreCurso; }
    public LocalDate inicio() { return inicio; }
    public LocalDate fin() { return fin; }
    public LocalDate publicacion() { return publicacion; }
    public int cupo() { return cupo; }
    public boolean vigente() { return vigente; }
    public List<DTUsuarioConsulta> docentes() { return docentes; }
}