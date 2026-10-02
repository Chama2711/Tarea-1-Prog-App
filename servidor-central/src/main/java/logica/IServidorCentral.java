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

    List<String> listarNombresInstitutos();

    List<String> listarNombresCursosPorInstituto(String nombreInstituto);

    List<String> listarNombresCategorias();

    void altaCurso(
            String nombreInstituto,
            String nombre,
            String descripcion,
            String duracion,
            int horas,
            int creditos,
            String url,
            List<String> nombresPrevias,
            List<String> nombresCategorias
    );


    void altaCurso(String nombreInstituto, String nombre, String descripcion,
            String duracion, int horas, int creditos, String url, LocalDate fechaAlta,
            List<String> nombresPrevias, List<String> nombresCategorias);

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

    List<String> listarNicknamesDocentes();
    List<String> listarNicknamesDocentesPorInstituto(String nombreInstituto);

    void altaEdicionCurso(
            String nombreInstituto,
            String nombreCurso,
            String nombreEdicion,
            LocalDate inicio,
            LocalDate fin,
            int cupo,
            List<String> nicknamesDocentes
    );

    List<Estudiante> obtenerEstudiantes();

    void inscriboAEdicionCurso(
        Estudiante estudiante,
        EdicionCurso edicion,
        LocalDate fecha
    );
    
    // ========================
    //  PROGRAMAS DE FORMACIÓN 
    //=========================

    void altaProgramaFormacion(String nombre, String descripcion,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta);

    void agregarCursoAPrograma(
        String nombrePrograma,
        String nombreCurso
    ) throws Exception;

    List<String> listarNombresCursos();

    List<String> listarNombresProgramas();
    List<String> listarNombresProgramasCurso(String nombreCurso);

    List<ProgramaFormacion> obtenerProgramas();

    ProgramaFormacion obtenerDetallePrograma(String nombrePrograma);
    
   // ==================== 
   //    USUARIOS
   //====================
 
    List<Usuario> obtenerUsuarios();

    Usuario obtenerUsuario(String nickname);

    Usuario obtenerUsuarioPorIdentificador(String nicknameOCorreo);

    DTAutenticacion autenticarUsuario(String nicknameOCorreo, String clave);

    void registrarEstudiante(String nick, String mail, String nombre, String apellido,
            LocalDate fechaNacimiento, String clave, String confirmacion);

    void registrarDocente(String nick, String mail, String nombre, String apellido,
            LocalDate fechaNacimiento, String nombreInstituto,
            String clave, String confirmacion);

    void crearUsuario(
        Usuario usuario,
        String nombreInstituto
    ) throws Exception;

    void editarUsuario(Usuario usuario) throws Exception;

    void crearUsuario(Usuario usuario, String nombreInstituto, byte[] imagen) throws Exception;

    byte[] obtenerImagenUsuario(String nick) throws Exception;

    List<String> listarCursosOEdicionesUsuario(String nick);

    List<String> listarEdicionesDocente(String nick);

    List<String> listarProgramasUsuario(String nick);
    
    
    
    
    
    
}
