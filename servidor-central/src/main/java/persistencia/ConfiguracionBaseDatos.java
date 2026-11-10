package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/** Parámetros de MySQL externos al JAR, compartidos por el arranque desde IDE y terminal. */
public final class ConfiguracionBaseDatos {
    private ConfiguracionBaseDatos() { }

    public static Map<String, Object> leer() {
        return leer(Path.of(System.getProperty("user.home"), ".edEXT", "central.properties"));
    }

    public static Map<String, Object> leer(Path archivo) {
        var configuracion = new Properties();
        try (var lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            configuracion.load(lector);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo leer la configuración de MySQL en " + archivo, error);
        }
        String url = requerida(configuracion, "jdbc.url");
        if (!url.startsWith("jdbc:mysql://")) {
            throw new IllegalArgumentException("jdbc.url debe ser una URL JDBC de MySQL.");
        }
        String usuario = requerida(configuracion, "jdbc.usuario");
        String contrasena = configuracion.getProperty("jdbc.contrasena");
        if (contrasena == null) throw new IllegalArgumentException("Falta configurar jdbc.contrasena; puede quedar vacía.");
        return Map.of(
                "javax.persistence.jdbc.url", url,
                "javax.persistence.jdbc.user", usuario,
                "javax.persistence.jdbc.password", contrasena,
                "javax.persistence.schema-generation.database.action", "none",
                "eclipselink.ddl-generation", "none");
    }

    private static String requerida(Properties propiedades, String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Falta configurar " + clave + ".");
        return valor.trim();
    }
}
