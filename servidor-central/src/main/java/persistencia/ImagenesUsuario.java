package persistencia;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;

/** Guarda las imágenes fuera del proyecto; la base conserva solo el nombre. */
public class ImagenesUsuario {
    private final Path carpeta;

    public ImagenesUsuario() {
        this(Path.of(System.getProperty("edext.imagenes.usuarios",
                Path.of(System.getProperty("user.home"), "edEXT", "imagenes", "usuarios").toString())));
    }

    public ImagenesUsuario(Path carpeta) {
        this.carpeta = carpeta.toAbsolutePath().normalize();
    }

    public String guardar(byte[] datos) throws IOException {
        if (datos == null) return null;
        String formato;
        try (var entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(datos))) {
            var lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) throw new IllegalArgumentException("Seleccione una imagen JPG o PNG válida.");
            var lector = lectores.next();
            try {
                formato = lector.getFormatName().toLowerCase(java.util.Locale.ROOT);
                if (!formato.equals("png") && !formato.equals("jpeg")) {
                    throw new IllegalArgumentException("Seleccione una imagen JPG o PNG.");
                }
                lector.setInput(entrada);
                if (lector.read(0) == null) throw new IllegalArgumentException("La imagen no se pudo leer.");
            } finally {
                lector.dispose();
            }
        }
        Files.createDirectories(carpeta);
        String nombre = UUID.randomUUID() + (formato.equals("png") ? ".png" : ".jpg");
        Path destino = carpeta.resolve(nombre);
        try {
            Files.write(destino, datos);
        } catch (IOException e) {
            Files.deleteIfExists(destino);
            throw e;
        }
        return nombre;
    }

    public byte[] leer(String nombre) throws IOException {
        if (nombre == null || nombre.isBlank()) return null;
        Path archivo = resolver(nombre);
        return Files.exists(archivo) ? Files.readAllBytes(archivo) : null;
    }

    public void eliminar(String nombre) throws IOException {
        if (nombre != null) Files.deleteIfExists(resolver(nombre));
    }

    private Path resolver(String nombre) {
        Path archivo = carpeta.resolve(nombre).normalize();
        if (!carpeta.equals(archivo.getParent())) {
            throw new IllegalArgumentException("Nombre de imagen inválido.");
        }
        return archivo;
    }
}
