package com.mycompany.tarea_1_prog_app;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nickname;
    private String mail;
    private String nombre;
    private String apellido;
    private int fechaNac;
    
    public Usuario()
    {
    }
    
    public Usuario (String nickname, String mail, String nombre, String apellido, int fechaNac)
    {
        this.nickname = nickname;
        this.mail = mail;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public String getNombre()
    {
        return nombre;
    }
}