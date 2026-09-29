/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface IServidorCentral {

    // =========================
    // CATEGORÍAS
    // =========================

    void altaCategoria(String nombreCategoria) throws Exception;

    List<Categoria> listarCategorias();

    Categoria buscarCategoria(String nombre);

    void agregarCategoriaACurso(
            String nombreCurso,
            String nombreCategoria
    ) throws Exception;

    List<Categoria> listarCategoriasCurso(
            String nombreCurso
    ) throws Exception;


    // =========================
    // CURSOS
    // =========================

    ArrayList<Curso> listarCursos();

    ArrayList<Curso> listarCursosPorInstituto(
            String nombreInstituto
    );

    Curso buscarCurso(String nombre);

    Curso buscarCursoInstituto(
            String nombre,
            Instituto instituto
    );

    boolean existeCurso(String nombre);

    void altaCurso(Curso curso);


    // =========================
    // INSTITUTOS
    // =========================

    ArrayList<Instituto> listarInstitutos();

    Instituto buscarInstituto(String nombre);


    // =========================
    // EDICIONES
    // =========================

    ArrayList<EdicionCurso> listarEdicionesCurso(
            Curso curso
    );

    ArrayList<Docente> listarDocentes();

    Docente buscarDocentePorNick(String nick);

    List<Instituto> obtenerInstitutos();

    List<Curso> obtenerCursosDeInstituto(Long institutoId);

    void altaEdicionCurso(Curso curso, EdicionCurso edicion);

    List<Estudiante> obtenerEstudiantes();

    void inscriboAEdicionCurso(
        Estudiante estudiante,
        EdicionCurso edicion,
        LocalDate fecha
    );
    
    
    
    
    
    
    
    
    
    
    
}
