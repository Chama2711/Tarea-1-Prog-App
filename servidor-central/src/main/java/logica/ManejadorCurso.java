
package logica;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Clase que conserva la colección global de los cursos del sistema.
 * Los cursos se identifican por su nombre.
 */
public class ManejadorCurso {

    private Map<String, Curso> cursos;
    private static ManejadorCurso instancia = null;

    private ManejadorCurso() {
        cursos = new HashMap<String, Curso>();
    }

    public static ManejadorCurso getinstance() {
        if (instancia == null)
            instancia = new ManejadorCurso();

        return instancia;
    }

    public void addCurso(Curso cur) {

        String nombre = cur.getNombre();
        cursos.put(nombre, cur);

        // Persistencia con JPA
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("edEXTPU");

        EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();

        try {
            t.begin();

            em.persist(cur);

            t.commit();

        } catch (Exception e) {

            if (t.isActive()) {
                t.rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
            emf.close();
        }
    }

    public Curso obtenerCurso(String nombre) {
        return ((Curso) cursos.get(nombre));
    }

    public Curso[] getCursos() {

        if (cursos.isEmpty())
            return null;

        else {
            Collection<Curso> curs = cursos.values();

            Object[] o = curs.toArray();

            Curso[] c = new Curso[o.length];

            for (int i = 0; i < o.length; i++) {
                c[i] = (Curso) o[i];
            }

            return c;
        }
    }
}
