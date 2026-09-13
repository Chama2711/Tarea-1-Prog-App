/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;

/**
 *
 * @author adrie
 */
public abstract class Usuario {
    private String nick;
    private String mail;
    private String nombre;
    private String apellido;
    private String fechaNac; // Se utiliza String para simplificar la captura desde el JTextField

    public Usuario(String nick, String mail, String nombre, String apellido, String fechaNac) {
        this.nick = nick;
        this.mail = mail;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
    }

    public String getNick() { return nick; }
    public String getMail() { return mail; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getFechaNac() { return fechaNac; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }
    
    
}
