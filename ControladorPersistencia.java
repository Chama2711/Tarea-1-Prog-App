/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.Curso;
import logica.Instituto;
import logica.ProgramaFormacion;

/**
 *
 * @author elizeth
 */
    public class ControladorPersistencia {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("edEXTPU");

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

    // Requerimiento: Consulta de Programa de Formación
    public List<String> listarNombresProgramas() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p.nombre FROM ProgramaFormacion p", String.class).getResultList();
        } finally {
            em.close();
        }
    }

    public ProgramaFormacion obtenerDetallePrograma(String nombrePrograma) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM ProgramaFormacion p LEFT JOIN FETCH p.cursos WHERE p.nombre = :nombre", ProgramaFormacion.class)
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

    public static class ControladoraPersistencia {

        public ControladoraPersistencia() {
        }
    }
}


