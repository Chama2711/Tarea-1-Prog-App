package persistencia;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/** Carga los datos originales de prueba en la base ya preparada de la web. */
public class CargarDatosPruebaWeb {

    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Uso: CargarDatosPruebaWeb [usuarioMySQL] [contrasenaMySQL]");
        }
        String usuario = args.length > 0 ? args[0] : "root";
        String contrasena = args.length > 1 ? args[1] : "";
        Map<String, Object> propiedades = Map.of(
                "javax.persistence.jdbc.url",
                "jdbc:mysql://localhost:3306/edext_web?zeroDateTimeBehavior=CONVERT_TO_NULL",
                "javax.persistence.jdbc.user", usuario,
                "javax.persistence.jdbc.password", contrasena,
                "javax.persistence.schema-generation.database.action", "none");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("edEXTPU", propiedades);
        try {
            EntityManager em = emf.createEntityManager();
            try {
                String base = String.valueOf(em.createNativeQuery("SELECT DATABASE()").getSingleResult());
                if (!"edext_web".equals(base)) {
                    throw new IllegalStateException("La carga solo está permitida en edext_web.");
                }
                em.getTransaction().begin();
                comprobarBaseVacia(em);
                CargarDatosPrueba.cargar(em);
                em.getTransaction().commit();
                System.out.println("Datos de CargarDatosPrueba cargados correctamente en edext_web.");
            } catch (RuntimeException error) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw error;
            } finally {
                em.close();
            }
        } finally {
            emf.close();
        }
    }

    private static void comprobarBaseVacia(EntityManager em) {
        for (String entidad : new String[]{"Usuario", "Instituto", "Curso", "Categoria",
                "EdicionCurso", "Inscripcion", "ProgramaFormacion"}) {
            Long cantidad = em.createQuery("SELECT COUNT(e) FROM " + entidad + " e", Long.class)
                    .getSingleResult();
            if (cantidad > 0) {
                throw new IllegalStateException(
                        "edext_web ya contiene datos. No se borró ni se cargó nada; "
                        + "esta carga requiere una base con las tablas preparadas y sin datos.");
            }
        }
    }
}
