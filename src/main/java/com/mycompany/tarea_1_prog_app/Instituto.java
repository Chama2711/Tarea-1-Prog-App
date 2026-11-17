package com.mycompany.tarea_1_prog_app;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Instituto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreInsti;
    
     public Instituto()
    {
    }
    
    public Instituto(String nombreInsti)
    {
        this.nombreInsti = nombreInsti;
    }
    
     public String getNombreInsti()
    {
        return nombreInsti;
    }
     
    public Long getId()
    {
        return id;
    } 
    
    @Override
    public String toString()
    {
        return nombreInsti;
    }
}