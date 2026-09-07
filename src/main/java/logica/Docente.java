/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;
/**
 *
 * @author Nicolás
 */

@Entity
public class Docente extends Usuario{
    
    public Docente() {
        super();
    }
    
    private List<String> cursosAsignados;
    private List<String> edicionesAsignadas;
    private List<String> programasAsignados;
    
    public Docente(String ni, String m, String no, String a, Date fn){
        super(ni, m, no, a, fn);
    }
    
    public List<String> getCursosAsignados() { return cursosAsignados; }
    public List<String> getEdicionesAsignadas() { return edicionesAsignadas; }
    public List<String> getProgramasAsignados() { return programasAsignados; }
    @ManyToMany
    private Set<Instituto> institutos;
    
    public void agregoInstituto(Instituto i){
        institutos.add(i);
    }
    
    @ManyToMany
    private Set<Curso> cursos;
    
    public void agregoCurso(Curso c){
        cursos.add(c);
    }
    
    @ManyToMany(mappedBy = "docentes")
    private List<EdicionCurso> ediciones;
    
    public void agregoEdicion(EdicionCurso ec){
        ediciones.add(ec);
    }
    
}
