/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Chama
 */
public class CursoTest {
     @Test
    public void testCrearCurso()
    {
        // PREPARAMOS LOS DATOS
        Instituto instituto = new Instituto("UTEC");

        ArrayList<String> previas = new ArrayList<>();
        previas.add("Programacion 1");

        LocalDate fecha = LocalDate.of(2026, 9, 13);

        // CREAMOS EL CURSO
        Curso curso = new Curso(instituto,"Programacion 2","Curso de programacion","4 meses",60,8,fecha,"www.utec.edu.uy",previas);

        // COMPROBAMOS LOS RESULTADOS
        assertEquals("Programacion 2", curso.getNombreCurso());
        assertEquals("Curso de programacion", curso.getDescripcionCurso());
        assertEquals("4 meses", curso.getDuracionCurso());
        assertEquals(60, curso.getHorasCurso());
        assertEquals(8, curso.getCreditosCurso());
        assertEquals(fecha, curso.getFechaRegistroCurso());
        assertEquals("www.utec.edu.uy", curso.getURLCurso());
        
        assertEquals(instituto, curso.getInstituto());
        assertEquals(previas, curso.getPreviasCurso());
    }
    
    @Test
    public void testAgregarEdicion()
    {
        Instituto instituto = new Instituto("UTEC");

        ArrayList<String> previas = new ArrayList<>();

        Curso curso = new Curso(instituto,"Programacion 2","Curso de programacion","4 meses",60,8,LocalDate.of(2026, 9, 13),"www.utec.edu.uy",previas);

        ArrayList<String> docentes = new ArrayList<>();
        docentes.add("Juan");

        EdicionCurso edicion = new EdicionCurso("Programacion 2 - 2026",LocalDate.of(2026, 3, 1),LocalDate.of(2026, 7, 1),30,LocalDate.of(2026, 2, 15),docentes,curso);

        curso.agregarEdicion(edicion);

        assertEquals(1, curso.getEdiciones().size());
    }
}
