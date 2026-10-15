package web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

/** Comparte la entrega de imágenes y su alternativa con los cursos. */
@WebServlet(urlPatterns = {"/imagen-edicion", "/imagen-programa"})
public class ImagenFormacionServlet extends ImagenCursoServlet {
    @Override
    protected byte[] obtenerImagen(HttpServletRequest request, String nombre) throws Exception {
        var servidor = InicializadorAplicacion.getServidor(getServletContext());
        return "/imagen-edicion".equals(request.getServletPath())
                ? servidor.obtenerImagenEdicion(nombre)
                : servidor.obtenerImagenPrograma(nombre);
    }
}
