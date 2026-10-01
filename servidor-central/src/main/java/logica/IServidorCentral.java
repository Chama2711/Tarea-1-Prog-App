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
    
    void altaInstituto(String nombreInstituto) throws Exception;


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
    
    // ========================
    //  PROGRAMAS DE FORMACIÓN 
    //=========================

    void altaProgramaFormacion(ProgramaFormacion programa);

    void agregarCursoAPrograma(
        String nombrePrograma,
        String nombreCurso
    ) throws Exception;

    List<String> listarNombresCursos();

    List<String> listarNombresProgramas();

    List<ProgramaFormacion> obtenerProgramas();

    ProgramaFormacion obtenerDetallePrograma(String nombrePrograma);
    
   // ==================== 
   //    USUARIOS
   //====================
 
    List<Usuario> obtenerUsuarios();

    Usuario obtenerUsuario(String nickname);

    void crearUsuario(
        Usuario usuario,
        String nombreInstituto
    ) throws Exception;

    void editarUsuario(Usuario usuario) throws Exception;

    List<String> listarCursosOEdicionesUsuario(String nick);

    List<String> listarProgramasUsuario(String nick);
    
    
    
    
    
    
}
