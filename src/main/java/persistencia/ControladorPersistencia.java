/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.*;

/**
 *
 * @author elizeth
 */
    public class ControladorPersistencia {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("edEXTPU");
    
    public void altaProgramaFormacion(ProgramaFormacion programa) {

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    try {
        tx.begin();

        em.persist(programa);

        tx.commit();

    } catch (Exception e) {

        if (tx.isActive()) {
            tx.rollback();
        }

        throw e;

    } finally {
        em.close();
    }
    }

    // Requerimiento: Agregar Curso a Programa de Formación
    public void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            ProgramaFormacion pf = em.createQuery("SELECT p FROM ProgramaFormacion p WHERE p.nombre = :nombre", ProgramaFormacion.class)
                    .setParameter("nombre", nombrePrograma)
                    .getSingleResult();

            Curso curso = em.createQuery("SELECT c FROM Curso c WHERE c.nombre = :nombre", Curso.class)
                    .setParameter("nombre", nombreCurso)
                    .getSingleResult();

            if (pf.getCursos().containsKey(nombreCurso)) {
                throw new Exception("El curso ya está dentro del programa de formación.");
            }

            pf.agregarCurso(curso);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public List<String> listarNombresCursos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c.nombre FROM Curso c", String.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Requerimiento: Consulta de Programa de Formación
    public List<String> listarNombresProgramas() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p.nombre FROM ProgramaFormacion p", String.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ProgramaFormacion> obtenerProgramas() {
    EntityManager em = emf.createEntityManager();

    try {

        return em.createQuery(
                "SELECT p FROM ProgramaFormacion p",ProgramaFormacion.class).getResultList();
    } finally {
        em.close();
    }
    }

    public ProgramaFormacion obtenerDetallePrograma(String nombrePrograma) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT p FROM ProgramaFormacion p LEFT JOIN FETCH p.cursos WHERE p.nombre = :nombre", ProgramaFormacion.class)
                    .setParameter("nombre", nombrePrograma)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    // Requerimiento : Alta de Instituto
    public void altaInstituto(String nombreInstituto) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            // Verificar si el nombre del instituto ya existe
            Long existe = em.createQuery("SELECT COUNT(i) FROM Instituto i WHERE i.nombre = :nombre", Long.class)
                    .setParameter("nombre", nombreInstituto)
                    .getSingleResult();

            if (existe > 0) {
                throw new Exception("Ya existe un instituto registrado con el nombre: " + nombreInstituto);
            }

            em.getTransaction().begin();
            Instituto inst = new Instituto(nombreInstituto);
            em.persist(inst);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Método auxiliar para listar institutos (necesario para Alta de Curso o Consultas)
    public List<Instituto> obtenerInstitutos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT i FROM Instituto i", Instituto.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    
    // ALTA DE USUARIO
public void crearUsuario(Usuario usuario) throws Exception {
    crearUsuario(usuario, null);
}

public void crearUsuario(Usuario usuario, String nombreInstituto) throws Exception {
    if (usuario == null || usuario.getNick() == null || usuario.getNick().isBlank()
            || usuario.getMail() == null || usuario.getMail().isBlank()) {
        throw new IllegalArgumentException("Faltan nickname o correo.");
    }
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        if (em.find(Usuario.class, usuario.getNick()) != null) {
            throw new IllegalArgumentException("Ya existe ese nickname.");
        }
        Long correos = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.mail) = LOWER(:mail)",
                Long.class).setParameter("mail", usuario.getMail()).getSingleResult();
        if (correos > 0) {
            throw new IllegalArgumentException("Ya existe ese correo.");
        }
        if (usuario instanceof Docente) {
            if (nombreInstituto == null || nombreInstituto.isBlank()) {
                throw new IllegalArgumentException("Seleccione el instituto del docente.");
            }
            List<Instituto> institutos = em.createQuery(
                    "SELECT i FROM Instituto i WHERE i.nombre = :nombre", Instituto.class)
                    .setParameter("nombre", nombreInstituto).getResultList();
            if (institutos.size() != 1) {
                throw new IllegalArgumentException("El instituto seleccionado ya no existe o es ambiguo.");
            }
            ((Docente) usuario).agregoInstituto(institutos.get(0));
        }
        em.persist(usuario);
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        throw e;
    } finally {
        em.close();
    }
}
    
    
    
    

    // MODIFICAR USUARIO
public void editarUsuario(Usuario usuario) throws Exception {
    if (usuario == null || usuario.getNick() == null) {
        throw new IllegalArgumentException("Seleccione un usuario.");
    }
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        Usuario actual = em.find(Usuario.class, usuario.getNick());
        if (actual == null) throw new IllegalArgumentException("El usuario ya no existe.");
        actual.setNombre(usuario.getNombre());
        actual.setApellido(usuario.getApellido());
        actual.setFechaNacimiento(usuario.getFechaNacimiento());
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        throw e;
    } finally {
        em.close();
    }
}
    
    
    
    public void guardarUsuario(Usuario usuario) throws Exception {
    EntityManager em = emf.createEntityManager();

    try {
        em.getTransaction().begin();

        em.persist(usuario);

        em.getTransaction().commit();

    } catch (Exception e) {

        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }

        throw e;

    } finally {
        em.close();
    }
}
    
    public Usuario obtenerUsuario(String nick) {
    EntityManager em = emf.createEntityManager();

    try {

        return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nick = :nick",
                Usuario.class)
                .setParameter("nick", nick)
                .getSingleResult();

    } catch (javax.persistence.NoResultException e) {
        // Al capturar la excepción, indicamos que el usuario no existe aún (comportamiento esperado)
        return null; 
    } finally {
        em.close();
    }
}
    
    public List<Usuario> obtenerUsuarios() {
        EntityManager em = emf.createEntityManager();

        try {

            return em.createQuery(
                    "SELECT u FROM Usuario u",
                    Usuario.class)
                    .getResultList();

        } finally {
            em.close();
        }
    }
    
    public List<Estudiante> obtenerEstudiantes(){
        EntityManager em = emf.createEntityManager();
        
        try {
            return em.createQuery("SELECT e FROM Estudiante e", Estudiante.class).getResultList();
        } finally {
            em.close();
        }
        
    }
    
    public List<Curso> obtenerCursosDeInstituto(Long idInstituto) {
    EntityManager em = emf.createEntityManager();

    try {
        return em.createQuery(
                "SELECT c FROM Curso c WHERE c.instituto.id = :id",
                Curso.class)
                .setParameter("id", idInstituto)
                .getResultList();

    } finally {
        em.close();
    }
}
    
    public List<Curso> obtenerTodosLosCursos() {

    EntityManager em = emf.createEntityManager();

    try {
        return em.createQuery(
                "SELECT c FROM Curso c",
                Curso.class)
                .getResultList();

    } finally {
        em.close();
    }
}

    private EntityManager getEntityManager() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
public void inscriboAEdicionCurso(Estudiante estudiante, EdicionCurso edicion,
        LocalDate fechaInscripcion) {
    if (estudiante == null || estudiante.getNick() == null || edicion == null
            || edicion.getNombre() == null || fechaInscripcion == null) {
        throw new IllegalArgumentException("Seleccione estudiante, edición y fecha.");
    }
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        // Serializa las inscripciones a la misma edición que pasan por este método.
        EdicionCurso edicionBD = em.find(EdicionCurso.class, edicion.getNombre(),
                javax.persistence.LockModeType.PESSIMISTIC_WRITE);
        Estudiante estudianteBD = em.find(Estudiante.class, estudiante.getNick());
        if (edicionBD == null || estudianteBD == null) {
            throw new IllegalArgumentException("El estudiante o la edición ya no existen.");
        }
        if (!edicionBD.esVigente()) throw new IllegalArgumentException("La edición ya no está vigente.");
        Long repetidas = em.createQuery(
                "SELECT COUNT(i) FROM Estudiante e JOIN e.inscripciones i "
                + "WHERE e.nick = :nick AND i.edicion.nombre = :edicion", Long.class)
                .setParameter("nick", estudianteBD.getNick())
                .setParameter("edicion", edicionBD.getNombre()).getSingleResult();
        if (repetidas > 0) {
            throw new IllegalArgumentException("El estudiante ya está inscripto en esa edición. Cambie la selección o cancele.");
        }
        int cupo = edicionBD.getCupo();
        if (cupo < -1) throw new IllegalArgumentException("La edición tiene un cupo inválido; revíselo antes de inscribir.");
        if (cupo != -1) {
            Long ocupadas = em.createQuery(
                    "SELECT COUNT(i) FROM Estudiante e JOIN e.inscripciones i "
                    + "WHERE i.edicion.nombre = :edicion", Long.class)
                    .setParameter("edicion", edicionBD.getNombre()).getSingleResult();
            if (ocupadas >= cupo) throw new IllegalArgumentException("No quedan plazas disponibles.");
        }
        Inscripcion nueva = new Inscripcion(fechaInscripcion, edicionBD);
        em.persist(nueva);
        estudianteBD.agregoInscripcion(nueva);
        em.getTransaction().commit();
    } catch (RuntimeException e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        if (e instanceof IllegalArgumentException) throw e;
        throw new IllegalStateException("No se pudo guardar la inscripción.", e);
    } finally {
        em.close();
    }
}
    
    ////////
    public ArrayList<Curso> listarCursosPorInstituto(String nombreInstituto)
    {
        EntityManager em = emf.createEntityManager();
        
        try
        {
            ArrayList<Curso> lista = new ArrayList<>(em.createQuery("SELECT c FROM Curso c " + "WHERE LOWER(c.instituto.nombre) = LOWER(:nombre)", Curso.class).setParameter("nombre", nombreInstituto).getResultList());
            
            return lista;
            
        }
        finally
        {
            em.close();
        }
    } 
    
    public ArrayList<EdicionCurso> listarEdicionesCurso(Curso curso)
    {
        EntityManager em = emf.createEntityManager();

        try
        {
            return new ArrayList<>(em.createQuery("SELECT e FROM EdicionCurso e " + "WHERE e.curso.id = :cursoId",EdicionCurso.class).setParameter("cursoId", curso.getId()).getResultList());
        }
        finally
        {
            em.close();
        }
    }
                
    
    //metodos
    public ArrayList<Instituto> listarInstitutos()
    {
        EntityManager em = emf.createEntityManager();
        
        try
        {
            ArrayList<Instituto> lista = new ArrayList<>(em.createQuery("SELECT i FROM Instituto i", Instituto.class).getResultList());
            
            return lista;
        }
        
        finally
        {
            em.close();
        }
    }

    public ArrayList<Docente> listarDocentes()
    {
        EntityManager em = emf.createEntityManager();
        
        try 
        {
            return new ArrayList<>(em.createQuery("SELECT d  FROM Docente d",Docente.class).getResultList());
            
        }
        finally
        {
            em.close();
        }
    }
    
    public ArrayList<Curso> listarCursos()
    {
         EntityManager em = emf.createEntityManager();
        
        try
        {
            ArrayList<Curso> lista = new ArrayList<>(em.createQuery("SELECT c FROM Curso c", Curso.class).getResultList());
            
            return lista;
        }
        
        finally
        {
            em.close();
        }
    }
    
    
    
    
public void altaCurso(Curso curso) {
    if (curso == null || curso.getNombre() == null || curso.getNombre().isBlank()
            || curso.getInstituto() == null || curso.getInstituto().getId() == null) {
        throw new IllegalArgumentException("Indique nombre e instituto del curso.");
    }
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        curso.setNombre(curso.getNombre().trim());
        Long cantidad = em.createQuery(
                "SELECT COUNT(c) FROM Curso c WHERE LOWER(c.nombre) = LOWER(:nombre)",
                Long.class).setParameter("nombre", curso.getNombre()).getSingleResult();
        if (cantidad > 0) throw new IllegalArgumentException("Ya existe ese nombre de curso en la plataforma.");
        Instituto instituto = em.find(Instituto.class, curso.getInstituto().getId());
        if (instituto == null) throw new IllegalArgumentException("El instituto ya no existe.");
        curso.setInstituto(instituto);
        java.util.Set<Curso> previas = new java.util.HashSet<>();
        for (Curso previa : curso.getPrevias()) {
            Curso existente = previa == null || previa.getId() == null
                    ? null : em.find(Curso.class, previa.getId());
            if (existente == null) throw new IllegalArgumentException("Una previa seleccionada ya no existe.");
            previas.add(existente);
        }
        curso.setPrevias(previas);
        em.persist(curso);
        em.getTransaction().commit();
    } catch (RuntimeException e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        if (e instanceof IllegalArgumentException) throw e;
        throw new IllegalStateException("No se pudo guardar el curso.", e);
    } finally {
        em.close();
    }
}
    
    
public boolean existeCurso(String nombre) {
    EntityManager em = emf.createEntityManager();
    try {
        return em.createQuery(
                "SELECT COUNT(c) FROM Curso c WHERE LOWER(c.nombre) = LOWER(:nombre)",
                Long.class).setParameter("nombre", nombre.trim()).getSingleResult() > 0;
    } finally {
        em.close();
    }
}
    
    
    public boolean existeEdicion(String nombre)
    {
        EntityManager em = emf.createEntityManager();

        try
        {
            Long cantidad = em.createQuery("SELECT COUNT(e) FROM EdicionCurso e " + "WHERE LOWER(e.nombre) = LOWER(:nombre)",Long.class).setParameter("nombre", nombre).getSingleResult();

            return cantidad > 0;
        }
        finally
        {
            em.close();
        }
    }
    
    
    public Instituto buscarInstituto(String nombre)
{
    EntityManager em = emf.createEntityManager();
    
    try
    {
        return em.createQuery("SELECT i FROM Instituto i " + "WHERE LOWER (i.nombre) = LOWER(:nombre)",Instituto.class).setParameter("nombre", nombre).getResultStream().findFirst().orElse(null);
    }
    finally
    {
        em.close();
    }
}
    
public Curso buscarCursoInstituto(String nombre, Instituto instituto) {
    if (nombre == null || instituto == null || instituto.getId() == null) return null;
    EntityManager em = emf.createEntityManager();
    try {
        List<Curso> cursos = em.createQuery(
                "SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.previas "
                + "WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.instituto.id = :institutoId",
                Curso.class).setParameter("nombre", nombre)
                .setParameter("institutoId", instituto.getId()).getResultList();
        return cursos.isEmpty() ? null : cursos.get(0);
    } finally {
        em.close();
    }
}  
    
    public Curso buscarCurso(String nombre)
    {
        EntityManager em = emf.createEntityManager();

        try
        {
            return em.createQuery("SELECT c FROM Curso c " + "WHERE LOWER(c.nombre) = LOWER(:nombre)",Curso.class).setParameter("nombre", nombre).getResultStream().findFirst().orElse(null);
        }
        finally
        {
            em.close();
        }
    }
    
    
public void altaEdicionCurso(Curso curso, EdicionCurso edicion) {
    if (curso == null || curso.getId() == null || edicion == null
            || edicion.getNombre() == null || edicion.getNombre().isBlank()) {
        throw new IllegalArgumentException("Seleccione un curso e indique el nombre de la edición.");
    }
    if (edicion.getFechaInicio() == null || edicion.getFechaFin() == null
            || edicion.getFechaFin().isBefore(edicion.getFechaInicio())) {
        throw new IllegalArgumentException("Revise las fechas de inicio y fin.");
    }
    if (edicion.getCupo() < -1) throw new IllegalArgumentException("Cupo inválido: use -1 para sin límite o un entero no negativo.");
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        edicion.setNombre(edicion.getNombre().trim());
        Long cantidad = em.createQuery(
                "SELECT COUNT(e) FROM EdicionCurso e WHERE LOWER(e.nombre) = LOWER(:nombre)",
                Long.class).setParameter("nombre", edicion.getNombre()).getSingleResult();
        if (cantidad > 0) throw new IllegalArgumentException("Ya existe ese nombre de edición.");
        Curso cursoBD = em.find(Curso.class, curso.getId());
        if (cursoBD == null) throw new IllegalArgumentException("El curso ya no existe.");
        java.util.Set<Docente> docentes = new java.util.HashSet<>();
        if (edicion.getDocentes() == null) throw new IllegalArgumentException("La colección de docentes no puede ser nula.");
        for (Docente docente : edicion.getDocentes()) {
            Docente existente = docente == null || docente.getNick() == null
                    ? null : em.find(Docente.class, docente.getNick());
            if (existente == null) throw new IllegalArgumentException("Un docente seleccionado ya no existe.");
            docentes.add(existente);
        }
        edicion.setCurso(cursoBD);
        edicion.setDocentes(docentes);
        em.persist(edicion);
        em.getTransaction().commit();
    } catch (RuntimeException e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        if (e instanceof IllegalArgumentException) throw e;
        throw new IllegalStateException("No se pudo guardar la edición.", e);
    } finally {
        em.close();
    }
}
   
public Docente buscarDocentePorNick(String nick) {
    EntityManager em = emf.createEntityManager();
    try {
        return nick == null ? null : em.find(Docente.class, nick);
    } finally {
        em.close();
    }
}
    ////////
    

public List<String> listarCursosOEdicionesUsuario(String nick) {
    EntityManager em = emf.createEntityManager();
    try {
        Usuario usuario = em.find(Usuario.class, nick);
        if (usuario instanceof Docente) {
            java.util.Set<String> nombres = new java.util.TreeSet<>();
            nombres.addAll(em.createQuery(
                    "SELECT DISTINCT c.nombre FROM Docente d JOIN d.cursos c WHERE d.nick = :nick",
                    String.class).setParameter("nick", nick).getResultList());
            nombres.addAll(em.createQuery(
                    "SELECT DISTINCT e.curso.nombre FROM EdicionCurso e JOIN e.docentes d WHERE d.nick = :nick",
                    String.class).setParameter("nick", nick).getResultList());
            return new ArrayList<>(nombres);
        }
        if (usuario instanceof Estudiante) {
            return em.createQuery(
                    "SELECT DISTINCT i.edicion.nombre FROM Estudiante e JOIN e.inscripciones i "
                    + "WHERE e.nick = :nick AND i.edicion IS NOT NULL ORDER BY i.edicion.nombre",
                    String.class).setParameter("nick", nick).getResultList();
        }
        return new ArrayList<>();
    } finally {
        em.close();
    }
}

public List<String> listarProgramasUsuario(String nick) {
    EntityManager em = emf.createEntityManager();
    try {
        Usuario usuario = em.find(Usuario.class, nick);
        if (usuario instanceof Docente) {
            List<String> cursos = listarCursosOEdicionesUsuario(nick);
            if (cursos.isEmpty()) return new ArrayList<>();
            return em.createQuery(
                    "SELECT DISTINCT p.nombre FROM ProgramaFormacion p JOIN p.cursos c "
                    + "WHERE c.nombre IN :cursos ORDER BY p.nombre", String.class)
                    .setParameter("cursos", cursos).getResultList();
        }
        if (usuario instanceof Estudiante) {
            return em.createQuery(
                    "SELECT DISTINCT i.programa.nombre FROM Estudiante e JOIN e.inscripciones i "
                    + "WHERE e.nick = :nick AND i.programa IS NOT NULL ORDER BY i.programa.nombre",
                    String.class).setParameter("nick", nick).getResultList();
        }
        return new ArrayList<>();
    } finally {
        em.close();
    }
}

public EdicionCurso buscarEdicion(String nombre) {
    EntityManager em = emf.createEntityManager();
    try {
        return nombre == null ? null : em.find(EdicionCurso.class, nombre);
    } finally {
        em.close();
    }
}

public static class ControladoraPersistencia {

        public ControladoraPersistencia() {
        }
    }
}


