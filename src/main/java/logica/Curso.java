/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.util.HashSet;
import javax.persistence.CascadeType;

/**
 *
 * @author elizeth
 */
@Entity
@Table(name = "cursos")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre; // Único

    @Lob
    @Column(name = "DESCRIPCION")
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private LocalDate fechaRegistro;
    private String url;

    @ManyToMany(mappedBy = "cursos")
    private Set<ProgramaFormacion> programas = new HashSet<>() ;
    @ManyToOne
    @JoinColumn(name = "instituto_id")
    private Instituto instituto;
    
    @ManyToMany
    @JoinTable(
        name = "previas_curso",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "previa_id")
    )
    private Set<Curso> previas = new HashSet<>();
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private Set<EdicionCurso> ediciones = new HashSet<>();
    

    public Curso() {}
    
    public Curso(String n, String d, int h, int c, LocalDate fr, String desc, String u){
        this.nombre = n;
        this.duracion = d;
        this.cantidadHoras = h;
        this.creditos = c;
        this.fechaRegistro = fr;
        this.descripcion = desc;
        this.url = u;
    }
    
    @Override
    public String toString() {
        return nombre;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Set<ProgramaFormacion> getProgramasFormacion() {
        return programas;
    }
    
    public void setProgramaFormacion(Set<ProgramaFormacion> programas){
        this.programas = programas;
    }

    public void setInstituto(Instituto instituto) {
        this.instituto = instituto;
    }
    
    public Instituto getInstituto() {
    return instituto;
    }

    public Set<Curso> getPrevias() {
        return previas;
    }
    
    public void setPrevias(Set<Curso> previas){
        this.previas = previas;
    }
    
    public Set<EdicionCurso> getEdiciones() {
        return ediciones;
    }
    
    public void setEdiciones(Set<EdicionCurso> ediciones){
        this.ediciones = ediciones;
    }
    
    public void agregoPrevia(Curso p){
        previas.add(p);
    }
    
    public void agregoEdicion(EdicionCurso e){
        ediciones.add(e);
    }
}
