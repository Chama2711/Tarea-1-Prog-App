/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarea_1_prog_app;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Chama
 */
    public class Controlador 
    {
    
        private EntityManagerFactory emf;

        public Controlador()
        {
            emf = Persistence.createEntityManagerFactory("edextPU");
        }
     
    public ArrayList<Curso> listarCursosPorInstituto(String nombreInstituto)
    {
        EntityManager em = emf.createEntityManager();
        
        try
        {
            ArrayList<Curso> lista = new ArrayList<>(em.createQuery("SELECT c FROM Curso c " + "WHERE LOWER(c.instituto.nombreInsti) = LOWER(:nombre)", Curso.class).setParameter("nombre", nombreInstituto).getResultList());
            
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
    
    
    
    
    public void altaCurso(Curso curso)
    {
        EntityManager em = emf.createEntityManager();
        
        try 
        {
            em.getTransaction().begin();
            
            Instituto instituto = curso.getInstituto();
            
            if(instituto.getId() == null)
            {
                em.persist(instituto);
            }
            
            em.persist(curso);
            
            em.getTransaction().commit();
            
            
            
           
        }
        catch (Exception e)
        {
            if(em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
            }
            
            e.printStackTrace();
        }
        finally
        {
            em.close();
        }
    }
    
    
    public boolean existeCurso(String nombre, Instituto instituto)
    {
        EntityManager em = emf.createEntityManager();
        
        try
        {
            Long cantidad = em.createQuery("SELECT COUNT(c) FROM Curso c "+ "WHERE LOWER(c.nombreCurso) = LOWER(:nombre) " + "AND c.instituto.id = :institutoId",Long.class).setParameter("nombre", nombre).setParameter("institutoId", instituto.getId()).getSingleResult();
            
            return cantidad > 0;
        }
        finally
        {
            em.close();
        }
    }
    
    
    public boolean existeEdicion(String nombre)
    {
        EntityManager em = emf.createEntityManager();

        try
        {
            Long cantidad = em.createQuery("SELECT COUNT(e) FROM EdicionCurso e " + "WHERE LOWER(e.nombreEdicion) = LOWER(:nombre)",Long.class).setParameter("nombre", nombre).getSingleResult();

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
        return em.createQuery("SELECT i FROM Instituto i " + "WHERE LOWER (i.nombreInsti) = LOWER(:nombre)",Instituto.class).setParameter("nombre", nombre).getResultStream().findFirst().orElse(null);
    }
    finally
    {
        em.close();
    }
}
    
    public Curso buscarCursoInstituto(String nombre, Instituto instituto)
{
    EntityManager em = emf.createEntityManager();

    try
    {
        return em.createQuery("SELECT c FROM Curso c " + "WHERE LOWER(c.nombreCurso) = LOWER(:nombre) " + "AND c.instituto.id = :institutoId", Curso.class).setParameter("nombre", nombre).setParameter("institutoId", instituto.getId()).getResultStream().findFirst().orElse(null);
        
    }
    finally
    {
        em.close();
    }
}  
    
    public Curso buscarCurso(String nombre)
    {
        EntityManager em = emf.createEntityManager();

        try
        {
            return em.createQuery("SELECT c FROM Curso c " + "WHERE LOWER(c.nombreCurso) = LOWER(:nombre)",Curso.class).setParameter("nombre", nombre).getResultStream().findFirst().orElse(null);
        }
        finally
        {
            em.close();
        }
    }
    
    
   public void altaEdicionCurso(Curso curso, EdicionCurso edicion)
    {
        EntityManager em = emf.createEntityManager();
        
        try
        {
            em.getTransaction().begin();
            
            Curso cursoBD = em.merge(curso);
            
            edicion.setCurso(cursoBD);
            
            em.persist(edicion);
            
            em.getTransaction().commit();
        }
        catch(Exception e)
        {
            if(em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            
        }
        finally
        {
            em.close();
        }
    }

 
}
