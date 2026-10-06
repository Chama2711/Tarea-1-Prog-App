package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ImagenUsuarioServlet", urlPatterns = {"/imagen-usuario"})
public class ImagenUsuarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        String nick = request.getParameter("nick");
        if (nick == null || nick.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        byte[] imagen;
        try {
            imagen = InicializadorAplicacion.getServidor(getServletContext()).obtenerImagenUsuario(nick);
        } catch (IOException e) {
            getServletContext().log("No se pudo leer la imagen del usuario solicitado.", e);
            response.sendRedirect(request.getContextPath() + "/imagenes/avatar.svg");
            return;
        } catch (Exception e) {
            throw new ServletException("No se pudo consultar la imagen del usuario.", e);
        }
        if (imagen == null || imagen.length == 0) {
            response.sendRedirect(request.getContextPath() + "/imagenes/avatar.svg");
            return;
        }
        boolean png = imagen.length >= 8 && imagen[0] == (byte) 0x89
                && imagen[1] == 0x50 && imagen[2] == 0x4e && imagen[3] == 0x47;
        boolean jpg = imagen.length >= 3 && imagen[0] == (byte) 0xff
                && imagen[1] == (byte) 0xd8 && imagen[2] == (byte) 0xff;
        if (!png && !jpg) {
            response.sendRedirect(request.getContextPath() + "/imagenes/avatar.svg");
            return;
        }
        response.setContentType(png ? "image/png" : "image/jpeg");
        response.setContentLength(imagen.length);
        response.getOutputStream().write(imagen);
    }
}

