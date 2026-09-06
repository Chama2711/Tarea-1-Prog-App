/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 *
 * @author Nicolás
 */

@Entity
public class EdicionCurso {
    
    @Id
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaPublicacion;
    private int cupo;
    
    public EdicionCurso(){
        
    }
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
    public EdicionCurso(String n, Date fi, Date ff, int c, Date fp){
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
    
    public Date getFechaInicio(){
        return fechaInicio;
    }
    public void setFechaInicio(Date fechaInicio){
        this.fechaInicio = fechaInicio;
    }
    
    public Date getFechaFin(){
        return fechaFin;
    }
    public void setFechaFin(Date fechaFin){
        this.fechaFin = fechaFin;
    }
    
    public Date getFechaPublicacion(){
        return fechaPublicacion;
    }
    public void setFechaPublicacion(Date fechaPublicacion){
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
    Date fechaActual = new Date();

        return fechaActual.after(fechaInicio) &&
            fechaActual.before(fechaFin);
        
    }
}
