package com.mycompany.tarea_1_prog_app;
import javax.persistence.Entity;

@Entity
public class Docente extends Usuario
{
    public Docente()
    {
        super();
    }
    
     public Docente(String nickname, String mail, String nombre, String apellido, int fechaNac)
     {
         super(nickname, mail, nombre, apellido, fechaNac); 
     }
}