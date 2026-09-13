/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
/**
 *
 * @author Chama
 */
public class ControladorTest {
    
     @Test
    public void testExisteCurso()
    {
        // PREPARAMOS
        Controlador controlador = new Controlador();

        Instituto instituto = controlador.buscarInstituto("Colegio Capuchinas");

        // COMPROBAMOS QUE ENCONTRÓ EL INSTITUTO
        assertNotNull(instituto);

        // PROBAMOS UN CURSO QUE EXISTE
        boolean existe = controlador.existeCurso("ejemplo base de datos", instituto);

        assertTrue(existe);
        
        boolean noExiste =
        controlador.existeCurso("Curso Inventado 99999", instituto);

        assertFalse(noExiste);
    }
    
    @Test
    public void testExisteEdicion()
    {
        Controlador controlador = new Controlador();

        // UNA EDICIÓN QUE SABEMOS QUE EXISTE
        boolean existe = controlador.existeEdicion("prog 2026");

        assertTrue(existe);

        // UNA EDICIÓN QUE NO EXISTE
        boolean noExiste = controlador.existeEdicion("Edicion Inventada 99999");

        assertFalse(noExiste);
    }
    
    
    @Test
    public void testBuscarInstituto()
    {
        Controlador controlador = new Controlador();

        // BUSCAMOS UNO QUE EXISTE
        Instituto instituto = controlador.buscarInstituto("Colegio Capuchinas");

        assertNotNull(instituto);
        assertEquals("Colegio Capuchinas", instituto.getNombreInsti());

        // BUSCAMOS UNO QUE NO EXISTE
        Instituto noExiste = controlador.buscarInstituto("Instituto Inventado 99999");

        assertNull(noExiste);
    }
    
    
    @Test
    public void testBuscarCursoInstituto()
    {
        Controlador controlador = new Controlador();

        Instituto instituto = controlador.buscarInstituto("Colegio Capuchinas");

        assertNotNull(instituto);

        // BUSCAMOS UN CURSO QUE EXISTE
        Curso curso = controlador.buscarCursoInstituto("ejemplo base de datos",instituto);

        assertNotNull(curso);
        assertEquals("ejemplo base de datos", curso.getNombreCurso());
        assertEquals("Colegio Capuchinas",curso.getInstituto().getNombreInsti());

        // BUSCAMOS UN CURSO QUE NO EXISTE
        Curso noExiste = controlador.buscarCursoInstituto("Curso Inventado 99999",instituto);

        assertNull(noExiste);
    }
    
    @Test
    public void testBuscarCurso()
    {
        Controlador controlador = new Controlador();

        // CURSO QUE EXISTE
        Curso curso = controlador.buscarCurso("ejemplo base de datos");

        assertNotNull(curso);
        assertEquals("ejemplo base de datos", curso.getNombreCurso());

        // CURSO QUE NO EXISTE
        Curso noExiste = controlador.buscarCurso("Curso Inventado 99999");

        assertNull(noExiste);
    }
    
    
    @Test
    public void testListarInstitutos()
    {
        Controlador controlador = new Controlador();

        ArrayList<Instituto> institutos = controlador.listarInstitutos();

        // LA LISTA NO DEBERÍA SER NULL
        assertNotNull(institutos);

        // DEBERÍA HABER AL MENOS UN INSTITUTO
        assertFalse(institutos.isEmpty());

        // BUSCAMOS COLEGIO CAPUCHINAS EN LA LISTA
        boolean encontrado = false;

        for(int i = 0; i < institutos.size(); i++)
        {
            if(institutos.get(i).getNombreInsti().equalsIgnoreCase("Colegio Capuchinas"))
            {
                encontrado = true;
            }
        }

        assertTrue(encontrado);
    }
    
    
    @Test
    public void testListarCursosPorInstituto()
    {
        Controlador controlador = new Controlador();

        ArrayList<Curso> cursos = controlador.listarCursosPorInstituto("Colegio Capuchinas");

        // LA LISTA NO DEBE SER NULL
        assertNotNull(cursos);

        // SABEMOS QUE ESTE INSTITUTO TIENE CURSOS
        assertFalse(cursos.isEmpty());

        // BUSCAMOS EL CURSO QUE SABEMOS QUE EXISTE
        boolean encontrado = false;

        for(int i = 0; i < cursos.size(); i++)
        {
            if(cursos.get(i).getNombreCurso().equalsIgnoreCase("ejemplo base de datos"))
            {
                encontrado = true;
            }
        }

        assertTrue(encontrado);
    }
    
    
    
    @Test
    public void testListarCursos()
    {
        Controlador controlador = new Controlador();

        ArrayList<Curso> cursos = controlador.listarCursos();

        // LA LISTA NO DEBE SER NULL
        assertNotNull(cursos);

        // SABEMOS QUE HAY CURSOS EN LA BASE
        assertFalse(cursos.isEmpty());

        // COMPROBAMOS QUE ESTÉ UN CURSO CONOCIDO
        boolean encontrado = false;

        for(int i = 0; i < cursos.size(); i++)
        {
            if(cursos.get(i).getNombreCurso().equalsIgnoreCase("ejemplo base de datos"))
            {
                encontrado = true;
            }
        }

        assertTrue(encontrado);
    }
    
    
    @Test
    public void testListarDocentes()
    {
        Controlador controlador = new Controlador();

        ArrayList<Docente> docentes = controlador.listarDocentes();

        // LA LISTA NO DEBE SER NULL
        assertNotNull(docentes);

        // SABEMOS QUE HAY DOCENTES EN LA BASE
        assertFalse(docentes.isEmpty());

        // BUSCAMOS A JUAN
        boolean encontrado = false;

        for(int i = 0; i < docentes.size(); i++)
        {
            if(docentes.get(i).getNombre().equalsIgnoreCase("Juan"))
            {
                encontrado = true;
            }
        }

        assertTrue(encontrado);
    }
    
        @Test
    public void testListarEdicionesCurso()
    {
        Controlador controlador = new Controlador();

        Instituto instituto = controlador.buscarInstituto("Colegio Capuchinas");

        assertNotNull(instituto);

        Curso curso = controlador.buscarCursoInstituto("ejemplo base de datos",instituto);

        assertNotNull(curso);

        ArrayList<EdicionCurso> ediciones = controlador.listarEdicionesCurso(curso);

        // LA LISTA NO DEBE SER NULL
        assertNotNull(ediciones);

        // EL CURSO DEBE TENER ALGUNA EDICIÓN
        assertFalse(ediciones.isEmpty());
    }

}
