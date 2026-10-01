
package logica;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Clase que conserva la colección global de los usuarios del sistema.
 * Los usuarios se identifican por su cédula de identidad.
 * Se implementa en base al patrón Singleton.
 * @author TProg2017
 *
 */
public class ManejadorUsuario {

    private Map<String, Usuario> usuarios;
    private static ManejadorUsuario instancia = null;

    private ManejadorUsuario() {
        usuarios = new HashMap<String, Usuario>();
    }

    public static ManejadorUsuario getinstance() {
        if (instancia == null)
            instancia = new ManejadorUsuario();

        return instancia;
    }

    public void addUsuario(Usuario usu) {

        String nick = usu.getNick();
        usuarios.put(nick, usu);

        // Persistencia con JPA
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("edEXTPU");

        EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();

        try {
            t.begin();

            em.persist(usu);

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

    public Usuario obtenerUsuario(String nick) {
        return ((Usuario) usuarios.get(nick));
    }

    public Usuario[] getUsuarios() {

        if (usuarios.isEmpty())
            return null;

        else {
            Collection<Usuario> usrs = usuarios.values();

            Object[] o = usrs.toArray();

            Usuario[] usuarios = new Usuario[o.length];

            for (int i = 0; i < o.length; i++) {
                usuarios[i] = (Usuario) o[i];
            }

            return usuarios;
        }
    }
}
