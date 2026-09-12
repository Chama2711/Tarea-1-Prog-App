/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.time.LocalDate;
import java.util.HashSet;
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
    
    private Set<String> cursosAsignados;
    private Set<String> edicionesAsignadas;
    private Set<String> programasAsignados;
    
    public Docente(String ni, String m, String no, String a, LocalDate fn){
        super(ni, m, no, a, fn);
    }
    
    public Set<String> getCursosAsignados() { return cursosAsignados; }
    public Set<String> getEdicionesAsignadas() { return edicionesAsignadas; }
    public Set<String> getProgramasAsignados() { return programasAsignados; }
    @ManyToMany
    private Set<Instituto> institutos = new HashSet<>() ;

    
    public void agregoInstituto(Instituto i){
        institutos.add(i);
    }
    
    @ManyToMany
    private Set<Curso> cursos = new HashSet<>();
    
    public void agregoCurso(Curso c){
        cursos.add(c);
    }
    
    @ManyToMany(mappedBy = "docentes")
    private Set<EdicionCurso> ediciones = new HashSet<>();
    
    public void agregoEdicion(EdicionCurso ec){
        ediciones.add(ec);
    }
    
}
