package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import persistencia.ControladorPersistencia;

/** Sustituye únicamente el almacenamiento: los controladores y la fachada son reales. */
class PersistenciaLogicaPrueba extends ControladorPersistencia {
    final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    final Map<String, Instituto> institutos = new LinkedHashMap<>();
    final Map<String, Categoria> categorias = new LinkedHashMap<>();
    final Map<String, Curso> cursos = new LinkedHashMap<>();
    final Map<String, EdicionCurso> ediciones = new LinkedHashMap<>();
    final Map<String, ProgramaFormacion> programas = new LinkedHashMap<>();
    List<DTInscripcion> inscripciones = List.of();
    List<DTEdicionConsulta> edicionesSeleccion = List.of();
    List<DTRankingInscripcion> ranking = List.of();
    String ultimoActor, ultimaEdicion, ultimoInstituto;
    LocalDate ultimaFecha;
    Map<Long, EstadoInscripcion> decisiones;
    Usuario editado;
    byte[] ultimaImagen;
    RuntimeException fallo;
    boolean cerrada;
    int escrituras;

    PersistenciaLogicaPrueba() {
        super((EntityManagerFactory) Proxy.newProxyInstance(EntityManagerFactory.class.getClassLoader(),
                new Class<?>[]{EntityManagerFactory.class}, (p, m, a) -> {
                    throw new AssertionError("La prueba no debe acceder a JPA: " + m.getName());
                }));
    }
    void comprobarGuardado() { if (fallo != null) throw fallo; escrituras++; }
    @Override public void close() { cerrada = true; }
    @Override public ArrayList<Instituto> listarInstitutos() { return new ArrayList<>(institutos.values()); }
    @Override public List<Instituto> obtenerInstitutos() { return listarInstitutos(); }
    @Override public Instituto buscarInstituto(String n) { return institutos.get(n); }
    @Override public void altaInstituto(String n) { comprobarGuardado(); institutos.put(n, new Instituto(n)); }
    @Override public List<Categoria> listarCategorias() { return new ArrayList<>(categorias.values()); }
    @Override public Categoria buscarCategoria(String n) { return categorias.get(n); }
    @Override public void altaCategoria(String n) { comprobarGuardado(); categorias.put(n, new Categoria(n)); }
    @Override public void agregarCategoriaACurso(String c, String categoria) {
        comprobarGuardado(); cursos.get(c).agregarCategoria(categorias.get(categoria));
    }
    @Override public List<Categoria> listarCategoriasCurso(String n) { return new ArrayList<>(cursos.get(n).getCategorias()); }
    @Override public ArrayList<Curso> listarCursos() { return new ArrayList<>(cursos.values()); }
    @Override public ArrayList<Curso> listarCursosPorInstituto(String n) {
        return new ArrayList<>(cursos.values().stream().filter(c -> n.equals(c.getInstituto().getNombre())).toList());
    }
    @Override public Curso buscarCurso(String n) { return cursos.get(n); }
    @Override public Curso buscarCursoInstituto(String n, Instituto i) {
        Curso c = cursos.get(n); return c != null && c.getInstituto() == i ? c : null;
    }
    @Override public boolean existeCurso(String n) { return cursos.containsKey(n); }
    @Override public void altaCurso(Curso c) { altaCurso(c, null); }
    @Override public void altaCurso(Curso c, byte[] imagen) {
        comprobarGuardado(); ultimaImagen = imagen; cursos.put(c.getNombre(), c);
    }
    @Override public List<String> listarNombresCursos() { return new ArrayList<>(cursos.keySet()); }
    @Override public ArrayList<Docente> listarDocentes() {
        return new ArrayList<>(usuarios.values().stream().filter(Docente.class::isInstance).map(Docente.class::cast).toList());
    }
    @Override public Docente buscarDocentePorNick(String n) { return (Docente) usuarios.get(n); }
    @Override public List<String> listarNombresInstitutosDocente(String n) {
        if (!(usuarios.get(n) instanceof Docente)) throw new SecurityException("No es docente.");
        return List.of("INCO");
    }
    @Override public List<String> listarNicknamesDocentesPorInstituto(String n) {
        ultimoInstituto = n; return listarDocentes().stream().map(Usuario::getNick).toList();
    }
    @Override public void altaEdicionCurso(Curso c, EdicionCurso e) { altaEdicionCurso(c, e, null); }
    @Override public void altaEdicionCurso(Curso c, EdicionCurso e, byte[] imagen) {
        comprobarGuardado(); ultimaImagen = imagen; e.setCurso(c); c.agregoEdicion(e); ediciones.put(e.getNombre(), e);
    }
    @Override public EdicionCurso buscarEdicion(String n) { return ediciones.get(n); }
    @Override public ArrayList<EdicionCurso> listarEdicionesCurso(Curso c) { return new ArrayList<>(c.getEdiciones()); }
    @Override public List<Usuario> obtenerUsuarios() { return new ArrayList<>(usuarios.values()); }
    @Override public Usuario obtenerUsuario(String n) { return usuarios.get(n); }
    @Override public Usuario obtenerUsuarioPorIdentificador(String n) {
        return usuarios.values().stream().filter(u -> n.equals(u.getNick()) || n.equalsIgnoreCase(u.getMail())).findFirst().orElse(null);
    }
    @Override public void crearUsuario(Usuario u, String instituto) { crearUsuario(u, instituto, null); }
    @Override public void crearUsuario(Usuario u, String instituto, byte[] imagen) {
        comprobarGuardado(); ultimoInstituto = instituto; ultimaImagen = imagen; usuarios.put(u.getNick(), u);
    }
    @Override public void editarUsuario(Usuario u) { comprobarGuardado(); editado = u; usuarios.put(u.getNick(), u); }
    @Override public List<Estudiante> obtenerEstudiantes() {
        return usuarios.values().stream().filter(Estudiante.class::isInstance).map(Estudiante.class::cast).toList();
    }
    @Override public List<String> listarCursosOEdicionesUsuario(String n) { ultimoActor = n; return List.of("Curso"); }
    @Override public List<String> listarProgramasUsuario(String n) { ultimoActor = n; return List.of("Programa"); }
    @Override public List<String> listarEdicionesDocente(String n) { ultimoActor = n; return List.of("Edición"); }
    @Override public boolean existeProgramaFormacion(String n) { return programas.containsKey(n); }
    @Override public void altaProgramaFormacion(ProgramaFormacion p) { comprobarGuardado(); programas.put(p.getNombre(), p); }
    @Override public List<String> listarNombresProgramas() { return new ArrayList<>(programas.keySet()); }
    @Override public List<ProgramaFormacion> obtenerProgramas() { return new ArrayList<>(programas.values()); }
    @Override public ProgramaFormacion obtenerDetallePrograma(String n) { return programas.get(n); }
    @Override public void agregarCursoAPrograma(String p, String c) { comprobarGuardado(); programas.get(p).agregarCurso(cursos.get(c)); }
    @Override public List<String> listarNombresProgramasCurso(String c) {
        return programas.values().stream().filter(p -> p.getCursos().containsKey(c)).map(ProgramaFormacion::getNombre).toList();
    }
    @Override public List<String> listarNombresCategoriasPrograma(String n) {
        return programas.get(n).getCursos().values().stream().flatMap(c -> c.getCategorias().stream())
                .map(Categoria::getNombre).distinct().sorted().toList();
    }
    @Override public void seleccionarEstudiantes(String doc, String e, Map<Long, EstadoInscripcion> d) {
        comprobarGuardado(); ultimoActor = doc; ultimaEdicion = e; decisiones = Map.copyOf(d);
    }
    @Override public List<DTInscripcion> listarInscripcionesEdicion(String doc, String e) { ultimoActor = doc; ultimaEdicion = e; return inscripciones; }
    @Override public List<DTInscripcion> listarAceptadosEdicion(String doc, String e) { ultimoActor = doc; ultimaEdicion = e; return inscripciones; }
    @Override public List<DTInscripcion> listarResultadosInscripciones(String n) { ultimoActor = n; return inscripciones; }
    @Override public List<DTRankingInscripcion> listarInscripcionesConPrioridad(String doc, String e, LocalDate f) {
        ultimoActor = doc; ultimaEdicion = e; ultimaFecha = f; return ranking;
    }
    @Override public List<DTEdicionConsulta> listarEdicionesParaSeleccion(String doc, String i, String c, LocalDate f) {
        ultimoActor = doc; ultimoInstituto = i; ultimaFecha = f; return edicionesSeleccion;
    }
    @Override public void inscriboAEdicionCurso(Estudiante est, EdicionCurso e, LocalDate f) {
        comprobarGuardado(); ultimoActor = est.getNick(); ultimaEdicion = e.getNombre(); ultimaFecha = f;
    }
    @Override public void inscriboAProgramaFormacion(Estudiante est, ProgramaFormacion p, LocalDate f) {
        comprobarGuardado(); ultimoActor = est.getNick(); ultimaFecha = f;
    }
    @Override public boolean estaInscriptoAPrograma(String n, Long id) { ultimoActor = n; return id.equals(9L); }
    @Override public byte[] obtenerImagenUsuario(String n) { ultimoActor = n; return ultimaImagen; }
    @Override public byte[] obtenerImagenCurso(String n) { ultimaEdicion = n; return ultimaImagen; }
    @Override public byte[] obtenerImagenEdicion(String n) { ultimaEdicion = n; return ultimaImagen; }
    @Override public byte[] obtenerImagenPrograma(String n) { ultimaEdicion = n; return ultimaImagen; }
}
