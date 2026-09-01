/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;
import java.util.ArrayList;

/**
 *
 * @author Chama
 */
public class Controlador {
    
    private ArrayList<Curso> cursos;
    
    public ArrayList<Curso> listarCursos()
    {
        return cursos;
    }
    
    public Controlador()
    {
        cursos = new ArrayList<>();
    }
    
    public void altaCurso(Curso curso)
    {
        cursos.add(curso);
        System.out.println("Cantidad de cursos: " + cursos.size());
    }
    
    public boolean existeCurso(String nombre)
    {
        for(int i=0; i < cursos.size();i++)
        {
            Curso curso = cursos.get(i);
            
            
            
            if(curso.getNombreCurso().equals(nombre))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Curso buscarCurso(String nombre)
    {
        for(int i=0; i < cursos.size(); i++)
        {
            Curso curso = cursos.get(i);
            
            if(curso.getNombreCurso().equals(nombre))
            {
                return curso;
            }
        }
        
        return null;
    }
    
    
}
