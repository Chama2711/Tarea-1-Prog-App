package com.mycompany.tarea_1_prog_app;

import java.util.ArrayList;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;

@Entity
public class Curso
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "instituto_id")
    private Instituto instituto;
    
    
    
    private String nombreCurso;
    private String descripcion;
    private String duracion;
    private int cantHoras;
    private int creditos;
    private LocalDate fechaRegistro;
    private String URL;
    
    @ElementCollection
    private ArrayList<String> previas;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private ArrayList<EdicionCurso> ediciones = new ArrayList<>();
    
     public Curso()
    {
    }
    
    
    
    public Curso (Instituto instituto, String nombreCurso, String descripcion, String duracion, int cantHoras, int creditos, LocalDate fechaRegistro, String URL,ArrayList<String> previas)
    {
        this.instituto = instituto;
        this.nombreCurso = nombreCurso;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.fechaRegistro = fechaRegistro;
        this.URL = URL;
        this.previas = previas;
    }
    
    public void agregarEdicion(EdicionCurso edicion)
    {
        ediciones.add(edicion);
    }   
    
    public Long getId()
    {
        return id;
    }
    
    public String getNombreCurso()
    {
        return nombreCurso;
    }
    
    public Instituto getInstituto()
    {
        return instituto;
    }
    
    public String getDescripcionCurso()
    {
        return descripcion;
    }
    
    public String getDuracionCurso()
    {
        return duracion;
    }
    
    public int getHorasCurso()
    {
        return cantHoras;
    }
    
    public int getCreditosCurso()
    {
        return creditos;
    }
        
    public LocalDate getFechaRegistroCurso()
    {
        return fechaRegistro;
    }
    
    public String getURLCurso()
    {
        return URL;
    }
    
    public ArrayList<String> getPreviasCurso()
    {
        return previas;
    }
    
    public ArrayList<EdicionCurso> getEdiciones()
{
    return ediciones;
}
        
        
        
        
        
}
