/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrie
 */
public class Estudiante extends Usuario {
    
    private List<String> edicionesInscriptas;
    private List<String> programasInscriptos;
    
    
    public Estudiante(String nick, String mail, String nombre, 
            String apellido, String fechaNac) {
        super(nick, mail, nombre, apellido, fechaNac);
        
        this.edicionesInscriptas = new ArrayList<>();
        this.programasInscriptos = new ArrayList<>();
    }
    
    
    public List<String> getEdicionesInscriptas() {
        return edicionesInscriptas;
    }

    public List<String> getProgramasInscriptos() {
        return programasInscriptos;
    }
    
    
    
    
    
}
