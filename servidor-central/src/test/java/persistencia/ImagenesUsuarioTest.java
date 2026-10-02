package persistencia;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.persistence.EntityManagerFactory;
import logica.Estudiante;
import logica.Usuario;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class ImagenesUsuarioTest {
    @Rule public TemporaryFolder temporal = new TemporaryFolder();

    private byte[] imagen(String formato) throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(12, 8, BufferedImage.TYPE_INT_RGB), formato, salida);
        return salida.toByteArray();
    }

    @Test public void conservaJpgYPngConNombresDistintos() throws Exception {
        ImagenesUsuario archivos = new ImagenesUsuario(temporal.getRoot().toPath());
        for (String formato : new String[]{"jpeg", "png"}) {
            byte[] datos = imagen(formato);
            String primero = archivos.guardar(datos);
            String segundo = archivos.guardar(datos);
            assertNotEquals(primero, segundo);
            assertArrayEquals(datos, archivos.leer(primero));
            archivos.eliminar(primero);
            assertNull(archivos.leer(primero));
            assertArrayEquals(datos, archivos.leer(segundo));
        }
    }

    @Test public void imagenOpcionalNoCreaArchivos() throws Exception {
        Path carpeta = temporal.getRoot().toPath().resolve("imagenes");
        ImagenesUsuario archivos = new ImagenesUsuario(carpeta);
        assertNull(archivos.guardar(null));
        assertNull(archivos.leer(null));
        assertFalse(Files.exists(carpeta));
    }

    @Test public void rechazaArchivosInvalidosYOtrosFormatos() throws Exception {
        ImagenesUsuario archivos = new ImagenesUsuario(temporal.getRoot().toPath());
        for (byte[] datos : new byte[][]{new byte[]{1, 2, 3}, imagen("gif")}) {
            assertThrows(IllegalArgumentException.class, () -> archivos.guardar(datos));
        }
        try (var entradas = Files.list(temporal.getRoot().toPath())) {
            assertEquals(0, entradas.count());
        }
    }

    @Test public void impideLeerFueraDeLaCarpeta() {
        ImagenesUsuario archivos = new ImagenesUsuario(temporal.getRoot().toPath());
        assertThrows(IllegalArgumentException.class, () -> archivos.leer("../otro.png"));
    }

    @Test public void altaFallidaEliminaLaCopiaDeImagen() throws Exception {
        String anterior = System.getProperty("edext.imagenes.usuarios");
        System.setProperty("edext.imagenes.usuarios", temporal.getRoot().toString());
        try {
            EntityManagerFactory emf = (EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(), new Class[]{EntityManagerFactory.class},
                    (objeto, metodo, args) -> null);
            ControladorPersistencia persistencia = new ControladorPersistencia(emf) {
                @Override public void crearUsuario(Usuario usuario, String instituto) {
                    throw new IllegalArgumentException("Nickname duplicado");
                }
            };
            Usuario usuario = new Estudiante("nick", "mail", "Nombre", "Apellido", LocalDate.of(2000, 1, 1));
            byte[] datos = imagen("png");
            assertThrows(IllegalArgumentException.class, () -> persistencia.crearUsuario(usuario, null, datos));
            assertNull(usuario.getImagen());
            try (var entradas = Files.list(temporal.getRoot().toPath())) {
                assertEquals(0, entradas.count());
            }
        } finally {
            if (anterior == null) System.clearProperty("edext.imagenes.usuarios");
            else System.setProperty("edext.imagenes.usuarios", anterior);
        }
    }
}
