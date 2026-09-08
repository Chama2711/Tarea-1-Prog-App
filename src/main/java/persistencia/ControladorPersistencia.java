/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.Curso;
import logica.Inscripcion;
import logica.Instituto;
import logica.ProgramaFormacion;
import logica.Usuario;
import logica.Estudiante;
import logica.EdicionCurso;

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
            return em.createQuery("SELECT p FROM ProgramaFormacion p", ProgramaFormacion.class).getResultList();
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
        EntityManager em = emf.createEntityManager();
        try {
            // Verificar si el nickname ya existe (opcional si la BD ya lo restringe)
            Usuario existe = em.find(Usuario.class, usuario.getNick());
            if (existe != null) {
                throw new Exception("Ya existe un usuario registrado con el nickname: " + usuario.getNick());
            }

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
    
    
    
    

    // MODIFICAR USUARIO
    public void editarUsuario(Usuario usuario) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario); // merge actualiza los datos del objeto en la BD
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
    
    public void inscriboAEdicionCurso(Estudiante e, EdicionCurso ec) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Inscripcion nueva = new Inscripcion();
            nueva.setFechaInscripcion(new Date());
            nueva.setEdicionCurso(ec);

            e.getInscripciones().add(nueva);

            em.persist(nueva);

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }
    

    public static class ControladoraPersistencia {

        public ControladoraPersistencia() {
        }
    }
}


