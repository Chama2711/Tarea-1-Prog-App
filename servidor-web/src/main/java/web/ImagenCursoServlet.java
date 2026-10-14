/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ImagenCursoServlet", urlPatterns = {"/imagen-curso"})
public class ImagenCursoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");

        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        byte[] imagen;

        try {
            imagen = InicializadorAplicacion
                    .getServidor(getServletContext())
                    .obtenerImagenCurso(nombre);

        } catch (IOException e) {
            getServletContext().log(
                    "No se pudo leer la imagen del curso solicitado.", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;

        } catch (Exception e) {
            throw new ServletException(
                    "No se pudo consultar la imagen del curso.", e);
        }

        if (imagen == null || imagen.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean png = imagen.length >= 8
                && imagen[0] == (byte) 0x89
                && imagen[1] == 0x50
                && imagen[2] == 0x4e
                && imagen[3] == 0x47;

        boolean jpg = imagen.length >= 3
                && imagen[0] == (byte) 0xff
                && imagen[1] == (byte) 0xd8
                && imagen[2] == (byte) 0xff;

        if (!png && !jpg) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(png ? "image/png" : "image/jpeg");
        response.setContentLength(imagen.length);
        response.getOutputStream().write(imagen);
    }
}
