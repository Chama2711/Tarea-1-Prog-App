package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/** La validación del contenido y el almacenamiento pertenecen al servidor central. */
final class ImagenFormulario {
    private ImagenFormulario() { }

    static byte[] leer(HttpServletRequest request) throws ServletException, IOException {
        var archivo = request.getPart("imagen");
        if (archivo == null || archivo.getSize() == 0) return null;
        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalStateException("La imagen no puede superar los 5 MB.");
        }
        try (var entrada = archivo.getInputStream()) {
            return entrada.readAllBytes();
        }
    }
}
