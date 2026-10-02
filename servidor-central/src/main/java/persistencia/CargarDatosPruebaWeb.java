package persistencia;

import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/** Crea las tablas y carga los datos originales en la base vacía de la web. */
public class CargarDatosPruebaWeb {

    private static final String URL =
            "jdbc:mysql://localhost:3306/edext_web?zeroDateTimeBehavior=CONVERT_TO_NULL";

    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Uso: CargarDatosPruebaWeb [usuarioMySQL] [contrasenaMySQL]");
        }
        String usuario = args.length > 0 ? args[0] : "root";
        String contrasena = args.length > 1 ? args[1] : "";
        // Comprobar antes de iniciar JPA, porque la creación del esquema puede ser inmediata.
        comprobarBaseSinTablas(usuario, contrasena);
        Map<String, Object> propiedades = Map.of(
                "javax.persistence.jdbc.url", URL,
                "javax.persistence.jdbc.user", usuario,
                "javax.persistence.jdbc.password", contrasena,
                "javax.persistence.schema-generation.database.action", "create");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("edEXTPU", propiedades);
        try {
            EntityManager em = emf.createEntityManager();
            try {
                String base = String.valueOf(em.createNativeQuery("SELECT DATABASE()").getSingleResult());
                if (!"edext_web".equals(base)) {
                    throw new IllegalStateException("La carga solo está permitida en edext_web.");
                }
                em.getTransaction().begin();
                CargarDatosPrueba.cargar(em);
                em.getTransaction().commit();
                System.out.println("Tablas creadas y datos de CargarDatosPrueba cargados correctamente en edext_web.");
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

    private static void comprobarBaseSinTablas(String usuario, String contrasena) {
        try (Connection conexion = DriverManager.getConnection(URL, usuario, contrasena)) {
            if (!"edext_web".equals(conexion.getCatalog())) {
                throw new IllegalStateException("La carga solo está permitida en edext_web.");
            }
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ?")) {
                consulta.setString(1, "edext_web");
                try (ResultSet resultado = consulta.executeQuery()) {
                    resultado.next();
                    if (resultado.getLong(1) > 0) {
                        throw new IllegalStateException(
                                "edext_web ya contiene tablas. No se modificó nada; "
                                + "esta carga inicial requiere la base creada sin tablas.");
                    }
                }
            }
        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo conectar a edext_web. Crea primero la base y verifica las credenciales de MySQL.",
                    error);
        }
    }
}
