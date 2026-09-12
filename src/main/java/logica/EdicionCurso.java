/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

<<<<<<< Updated upstream
import java.util.Date;
=======
import java.time.LocalDate;
>>>>>>> Stashed changes
import java.util.HashSet;
import java.util.Set;
import javax.persistence.JoinTable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;

/**
 *
 * @author Nicolás
 */

@Entity
public class EdicionCurso {
    
    @Id
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaPublicacion;
    private int cupo;
    
    public EdicionCurso(){
        
    }
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
    @ManyToMany
    @JoinTable(
    name = "edicion_docente",
    joinColumns = @JoinColumn(name = "edicion_nombre"),
    inverseJoinColumns = @JoinColumn(name = "docente_nick")
    )
<<<<<<< Updated upstream
    private Set<Docente> docentes = new HashSet<>() ;
=======
    private Set<Docente> docentes = new HashSet<>();
>>>>>>> Stashed changes
    
    public EdicionCurso(String n, LocalDate fi, LocalDate ff, int c, LocalDate fp){
        this.nombre = n;
        this.fechaInicio = fi;
        this.fechaFin = ff;
        this.fechaPublicacion = fp;
        this.cupo = c;
    }
    
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public LocalDate getFechaInicio(){
        return fechaInicio;
    }
    public void setFechaInicio(LocalDate fechaInicio){
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin(){
        return fechaFin;
    }
    public void setFechaFin(LocalDate fechaFin){
        this.fechaFin = fechaFin;
    }
    
    public LocalDate getFechaPublicacion(){
        return fechaPublicacion;
    }
    public void setFechaPublicacion(LocalDate fechaPublicacion){
        this.fechaPublicacion = fechaPublicacion;
    }
    
    public int getCupo(){
        return cupo;
    }
    public void setCupo(int cupo){
        this.cupo = cupo;
    }
    
    public Curso getCurso() {
    return curso;
}

    public void setCurso(Curso curso) {
    this.curso = curso;
}
    @Override
    public String toString() {
    return nombre;
}
    public boolean esVigente() {
    LocalDate fechaActual = LocalDate.now();

        return fechaActual.isAfter(fechaInicio) &&
            fechaActual.isBefore(fechaFin);
        
    }
    
    public void agregoDocente(Docente d){
        docentes.add(d);
    }
    
}
