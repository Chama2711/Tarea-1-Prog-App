package com.mycompany.tarea_1_prog_app;

import java.util.ArrayList;
import java.time.LocalDate;

public class Curso
{
    private String nombreInstituto;
    private String nombreCurso;
    private String descripcion;
    private String duracion;
    private int cantHoras;
    private int creditos;
    private LocalDate fechaRegistro;
    private String URL;
    
    private ArrayList<String> previas;
    
    
    public Curso (String nombreInstituto, String nombreCurso, String descripcion, String duracion, int cantHoras, int creditos, LocalDate fechaRegistro, String URL,ArrayList<String> previas)
    {
        this.nombreInstituto = nombreInstituto;
        this.nombreCurso = nombreCurso;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.fechaRegistro = fechaRegistro;
        this.URL = URL;
        this.previas = previas;
    }
    
    public String getNombreCurso()
    {
        return nombreCurso;
    }
    
    public String getNombreInstituto()
    {
        return nombreInstituto;
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
        
        
        
        
        
}
