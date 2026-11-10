package persistencia;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class ConfiguracionBaseDatosTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();

    private Path escribir(String contenido) throws Exception {
        Path archivo = temporal.newFile().toPath();
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
        return archivo;
    }
    private String valida() {
        return "soap.host=127.0.0.1\njdbc.url=jdbc:mysql://localhost:3306/edext_web\njdbc.usuario=ana\njdbc.contrasena=\n";
    }
    @Test public void leeLaConfiguracionUtf8YAdmiteContrasenaVacia() throws Exception {
        var opciones = ConfiguracionBaseDatos.leer(escribir(valida().replace("=ana", "=Nicolás")));
        assertEquals("Nicolás", opciones.get("javax.persistence.jdbc.user"));
        assertEquals("", opciones.get("javax.persistence.jdbc.password"));
        assertEquals("none", opciones.get("javax.persistence.schema-generation.database.action"));
        assertEquals("none", opciones.get("eclipselink.ddl-generation"));
    }
    @Test public void cambioDeDireccionYContrasenaNoRequiereRecompilar() throws Exception {
        Path archivo = escribir(valida());
        assertEquals("ana", ConfiguracionBaseDatos.leer(archivo).get("javax.persistence.jdbc.user"));
        Files.writeString(archivo, valida().replace("localhost:3306", "192.168.1.10:3307")
                .replace("jdbc.contrasena=", "jdbc.contrasena=secreta con espacios  "));
        var opciones = ConfiguracionBaseDatos.leer(archivo);
        assertEquals("jdbc:mysql://192.168.1.10:3307/edext_web", opciones.get("javax.persistence.jdbc.url"));
        assertEquals("secreta con espacios  ", opciones.get("javax.persistence.jdbc.password"));
    }
    @Test public void propiedadesFaltantesOVaciasNoActivanValoresOcultos() throws Exception {
        for (String clave : new String[]{"jdbc.url", "jdbc.usuario", "jdbc.contrasena"}) {
            String contenido = valida().replaceAll("(?m)^" + clave.replace(".", "\\.") + "=.*\\n", "");
            Path archivo = escribir(contenido);
            assertThrows(IllegalArgumentException.class, () -> ConfiguracionBaseDatos.leer(archivo));
        }
        for (String clave : new String[]{"jdbc.url", "jdbc.usuario"}) {
            Path archivo = escribir(valida().replaceAll("(?m)^" + clave.replace(".", "\\.") + "=.*", clave + "= "));
            assertThrows(IllegalArgumentException.class, () -> ConfiguracionBaseDatos.leer(archivo));
        }
    }
    @Test public void archivoAusenteYDireccionInvalidaSeInforman() throws Exception {
        assertThrows(IllegalStateException.class, () -> ConfiguracionBaseDatos.leer(temporal.getRoot().toPath().resolve("ausente")));
        Path archivo = escribir(valida().replace("jdbc:mysql://", "http://"));
        assertThrows(IllegalArgumentException.class, () -> ConfiguracionBaseDatos.leer(archivo));
    }
}
