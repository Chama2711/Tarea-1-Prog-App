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
public class Docente extends Usuario {
    private Instituto instituto;

    private List<String> cursosAsignados;
    private List<String> edicionesAsignadas;
    private List<String> programasAsignados;
    
    
    public Docente(String nick, String mail, String nombre, 
            String apellido, String fechaNac, Instituto instituto) {
        super(nick, mail, nombre, apellido, fechaNac);
        this.instituto = instituto;
        this.cursosAsignados = new ArrayList<>();
        this.edicionesAsignadas = new ArrayList<>();
        this.programasAsignados = new ArrayList<>();
    }

    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
    
    public List<String> getCursosAsignados() { return cursosAsignados; }
    public List<String> getEdicionesAsignadas() { return edicionesAsignadas; }
    public List<String> getProgramasAsignados() { return programasAsignados; }
    
    
}
