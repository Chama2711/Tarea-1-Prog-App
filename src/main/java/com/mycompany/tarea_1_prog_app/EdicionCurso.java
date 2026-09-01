package com.mycompany.tarea_1_prog_app;

public class EdicionCurso
{
    private String nombreEdicion;
    private int fechaInicio;
    private int fechaFin;
    private int cupo;
    private int fechaPublic;
    
    
    public EdicionCurso (String nombreEdicion, int fechaInicio, int fechaFin, int cupo, int fechaPublic)
    {
        this.nombreEdicion = nombreEdicion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublic = fechaPublic;
    }
}