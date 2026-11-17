package com.mycompany.tarea_1_prog_app;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;

@Entity
public class EdicionCurso
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    private String nombreEdicion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupo;
    private LocalDate fechaPublic;
    
    @ElementCollection
    private ArrayList<String> docentes;
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
     public EdicionCurso()
    {
    }
    
    public EdicionCurso (String nombreEdicion, LocalDate fechaInicio, LocalDate fechaFin, int cupo, LocalDate fechaPublic, ArrayList<String> docentes, Curso curso)
    {
        this.nombreEdicion = nombreEdicion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublic = fechaPublic;
        this.docentes = docentes;
        this.curso = curso;
    }
    
    public String getNombreEdicion()
    {
        return nombreEdicion;
    }
    
     public LocalDate getFechaInicio()
    {
        return fechaInicio;
    }
     
       public LocalDate getFechaFin()
    {
        return fechaFin;
    }
    
       public void setCurso(Curso curso)
    {
        this.curso = curso;
    }

       
       
       public int getCupo()
    {
        return cupo;
    }
    
       public LocalDate getFechaPublicacion()
    {
        return fechaPublic;
    }
       
    public ArrayList<String> getDocentes()
    {
        return docentes;
    }   
    
    
}