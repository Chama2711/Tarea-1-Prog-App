/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import persistencia.ControladorPersistencia;

public class ServidorCentral implements IServidorCentral {

    private final ControladorPersistencia persistencia;
    private final ControladorCurso controladorCurso;
    private final ControladorUsuario controladorUsuario;
    private final ControladorProgramaFormacion controladorProgramaFormacion;

    public ServidorCentral() {
        this.persistencia = new ControladorPersistencia();
        this.controladorCurso = new ControladorCurso(persistencia);
        this.controladorUsuario = new ControladorUsuario(this);
        this.controladorProgramaFormacion = new ControladorProgramaFormacion(persistencia);
    }

    // =========================
    // CATEGORÍAS
    // =========================

    @Override
    public void altaCategoria(String nombreCategoria)
            throws Exception {

        persistencia.altaCategoria(nombreCategoria);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return persistencia.listarCategorias();
    }

    @Override
    public Categoria buscarCategoria(String nombre) {
        return persistencia.buscarCategoria(nombre);
    }

    @Override
    public void agregarCategoriaACurso(
            String nombreCurso,
            String nombreCategoria)
            throws Exception {

        persistencia.agregarCategoriaACurso(
                nombreCurso,
                nombreCategoria
        );
    }

    @Override
    public List<Categoria> listarCategoriasCurso(
            String nombreCurso)
            throws Exception {

        return persistencia.listarCategoriasCurso(
                nombreCurso
        );
    }

    // =========================
    // CURSOS
    // =========================

    @Override
    public ArrayList<Curso> listarCursos() {
        return persistencia.listarCursos();
    }

    @Override
    public ArrayList<Curso> listarCursosPorInstituto(
            String nombreInstituto) {

        return persistencia.listarCursosPorInstituto(
                nombreInstituto
        );
    }

    @Override
    public Curso buscarCurso(String nombre) {
        return persistencia.buscarCurso(nombre);
    }

    @Override
    public Curso buscarCursoInstituto(
            String nombre,
            Instituto instituto) {

        return persistencia.buscarCursoInstituto(
                nombre,
                instituto
        );
    }

    @Override
    public boolean existeCurso(String nombre) {
        return persistencia.existeCurso(nombre);
    }

    @Override
    public List<String> listarNombresInstitutos() {
        return controladorCurso.listarNombresInstitutos();
    }

    @Override
    public List<String> listarNombresCursosPorInstituto(String nombreInstituto) {
        return controladorCurso.listarNombresCursosPorInstituto(nombreInstituto);
    }

    @Override
    public List<String> listarNombresCategorias() {
        return controladorCurso.listarNombresCategorias();
    }

    @Override
    public void altaCurso(
            String nombreInstituto,
            String nombre,
            String descripcion,
            String duracion,
            int horas,
            int creditos,
            String url,
            List<String> nombresPrevias,
            List<String> nombresCategorias) {
        controladorCurso.altaCurso(nombreInstituto, nombre, descripcion,
                duracion, horas, creditos, url, nombresPrevias, nombresCategorias);
    }

    @Override
    public void altaCurso(String nombreInstituto, String nombre, String descripcion,
            String duracion, int horas, int creditos, String url, LocalDate fechaAlta,
            List<String> nombresPrevias, List<String> nombresCategorias) {
        controladorCurso.altaCurso(nombreInstituto, nombre, descripcion,
                duracion, horas, creditos, url, fechaAlta, nombresPrevias, nombresCategorias);
    }

    // =========================
    // INSTITUTOS
    // =========================

    @Override
    public ArrayList<Instituto> listarInstitutos() {
        return persistencia.listarInstitutos();
    }

    @Override
    public Instituto buscarInstituto(String nombre) {
        return persistencia.buscarInstituto(nombre);
    }
    
    @Override
    public void altaInstituto(String nombreInstituto) throws Exception {
    persistencia.altaInstituto(nombreInstituto);
    }
    
    

    // =========================
    // EDICIONES
    // =========================

    @Override
    public ArrayList<EdicionCurso> listarEdicionesCurso(
            Curso curso) {

        return persistencia.listarEdicionesCurso(curso);
    }
    
    
    @Override
    public ArrayList<Docente> listarDocentes() {
       return persistencia.listarDocentes();
    }

    @Override
    public Docente buscarDocentePorNick(String nick) {
       return persistencia.buscarDocentePorNick(nick);
    }   

    @Override
    public List<Instituto> obtenerInstitutos() {
       return persistencia.obtenerInstitutos();
    }

   @Override
   public List<Curso> obtenerCursosDeInstituto(Long idInstituto) {
      return persistencia.obtenerCursosDeInstituto(idInstituto);
    }

    @Override
    public List<Estudiante> obtenerEstudiantes() {
       return persistencia.obtenerEstudiantes();
    }

   @Override
    public void altaEdicionCurso(
            String nombreInstituto,
            String nombreCurso,
            String nombreEdicion,
            LocalDate inicio,
            LocalDate fin,
            int cupo,
            List<String> nicknamesDocentes) {
        controladorCurso.altaEdicionCurso(nombreInstituto, nombreCurso,
                nombreEdicion, inicio, fin, cupo, nicknamesDocentes);
    }

    @Override
    public List<String> listarNicknamesDocentes() {
        return controladorCurso.listarNicknamesDocentes();
    }

    @Override
    public List<String> listarNicknamesDocentesPorInstituto(String nombreInstituto) {
        return persistencia.listarNicknamesDocentesPorInstituto(nombreInstituto);
    }

   @Override
   public void inscriboAEdicionCurso(
        Estudiante estudiante,
        EdicionCurso edicion,
        LocalDate fechaInscripcion) {

    persistencia.inscriboAEdicionCurso(
            estudiante,
            edicion,
            fechaInscripcion
    );
    } 
    
    // ==================== PROGRAMAS DE FORMACIÓN ====================

    @Override
    public void altaProgramaFormacion(String nombre, String descripcion,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta) {
        controladorProgramaFormacion.altaProgramaFormacion(
                nombre, descripcion, fechaInicio, fechaFin, fechaAlta);
    }

    @Override
    public void agregarCursoAPrograma(
        String nombrePrograma,
        String nombreCurso) throws Exception {

    persistencia.agregarCursoAPrograma(
            nombrePrograma,
            nombreCurso
    );
    }

    @Override
    public List<String> listarNombresCursos() {
    return persistencia.listarNombresCursos();
    }

    @Override
    public List<String> listarNombresProgramas() {
    return persistencia.listarNombresProgramas();
    }

    @Override
    public List<String> listarNombresProgramasCurso(String nombreCurso) {
        return persistencia.listarNombresProgramasCurso(nombreCurso);
    }

    @Override
    public List<ProgramaFormacion> obtenerProgramas() {
    return persistencia.obtenerProgramas();
    }

    @Override
    public ProgramaFormacion obtenerDetallePrograma(
        String nombrePrograma) {

    return persistencia.obtenerDetallePrograma(nombrePrograma);
    }
    
    
    
    
    
    // ==================== 
    //     USUARIOS 
    //=====================

    @Override
    public List<Usuario> obtenerUsuarios() {
    return persistencia.obtenerUsuarios();
    }

    @Override
    public Usuario obtenerUsuario(String nick) {
    return persistencia.obtenerUsuario(nick);
    }

    @Override
    public Usuario obtenerUsuarioPorIdentificador(String identificador) {
        return persistencia.obtenerUsuarioPorIdentificador(identificador);
    }

    @Override
    public DTAutenticacion autenticarUsuario(String identificador, String clave) {
        return controladorUsuario.autenticarUsuario(identificador, clave);
    }

    @Override
    public void registrarEstudiante(String nick, String mail, String nombre, String apellido,
            LocalDate fechaNacimiento, String clave, String confirmacion) {
        controladorUsuario.registrarEstudiante(nick, mail, nombre, apellido,
                fechaNacimiento, clave, confirmacion);
    }

    @Override
    public void registrarDocente(String nick, String mail, String nombre, String apellido,
            LocalDate fechaNacimiento, String instituto, String clave, String confirmacion) {
        controladorUsuario.registrarDocente(nick, mail, nombre, apellido,
                fechaNacimiento, instituto, clave, confirmacion);
    }

    @Override
    public void crearUsuario(
        Usuario usuario,
        String nombreInstituto) throws Exception {

    persistencia.crearUsuario(usuario, nombreInstituto);
    }

    @Override
    public void editarUsuario(Usuario usuario) throws Exception {
    persistencia.editarUsuario(usuario);
    }

    @Override
    public List<String> listarCursosOEdicionesUsuario(String nick) {
    return persistencia.listarCursosOEdicionesUsuario(nick);
    }

    @Override
    public List<String> listarProgramasUsuario(String nick) {
    return persistencia.listarProgramasUsuario(nick);
    }

    @Override
    public void crearUsuario(Usuario usuario, String nombreInstituto, byte[] imagen) throws Exception {
        persistencia.crearUsuario(usuario, nombreInstituto, imagen);
    }

    @Override
    public byte[] obtenerImagenUsuario(String nick) throws Exception {
        return persistencia.obtenerImagenUsuario(nick);
    }

    @Override
    public List<String> listarEdicionesDocente(String nick) {
        return persistencia.listarEdicionesDocente(nick);
    }
    
    
    

    
}
