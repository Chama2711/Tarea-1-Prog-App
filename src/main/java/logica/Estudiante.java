/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
<<<<<<< Updated upstream
import java.util.Date;
import java.util.Set;
import java.util.ArrayList;
=======
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
>>>>>>> Stashed changes
import java.util.HashSet;
import javax.persistence.OneToMany;

/**
 *
 * @author Nicolás
 */
@Entity
public class Estudiante extends Usuario{
    
    public Estudiante() {
        super();
    }
    
    private Set<String> edicionesInscriptas;
    private Set<String> programasInscriptos;
    
    public Estudiante(String ni, String m, String no, String a, LocalDate fn){
        super(ni, m, no, a, fn);
    }
    
    public Set<String> getEdicionesInscriptas() {
        return edicionesInscriptas;
    }

    public Set<String> getProgramasInscriptos() {
        return programasInscriptos;
    }
    @OneToMany
    @JoinColumn(name = "estudiante_nick")
    private Set<Inscripcion> inscripciones = new HashSet<>();
    
    public Set<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Set<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
    
    public void agregoInscripcion(Inscripcion i){
        inscripciones.add(i);
    }
    
}
