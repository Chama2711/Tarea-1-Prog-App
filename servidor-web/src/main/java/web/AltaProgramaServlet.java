/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

/**
 *
 * @author elizeth
 */
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import logica.IServidorCentral;

@WebServlet(name = "AltaProgramaServlet", urlPatterns = {"/alta-programa"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AltaProgramaServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/vistas/altaPrograma.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (FormulariosDocente.exigirDocente(request, response) == null) {
            return;
        }

        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (FormulariosDocente.exigirDocente(request, response) == null) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        byte[] imagen;
        try {
            imagen = ImagenFormulario.leer(request);
        } catch (IllegalStateException e) {
            response.sendError(413, "El archivo supera los 5 MB o el formulario supera los 6 MB. Volvé a elegir una imagen más pequeña.");
            return;
        } catch (ServletException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se pudo recibir la imagen. Volvé a enviar el formulario.");
            return;
        }
        String nombre = FormulariosDocente.valor(request, "nombre").trim();
        String descripcion = FormulariosDocente.valor(request, "descripcion").trim();

        try {
            LocalDate fechaInicio = LocalDate.parse(
                    FormulariosDocente.valor(request, "fechaInicio"));

            LocalDate fechaFin = LocalDate.parse(
                    FormulariosDocente.valor(request, "fechaFin"));

            LocalDate fechaAlta = LocalDate.now();

            IServidorCentral servidor =
                    InicializadorAplicacion.getServidor(getServletContext());

            servidor.altaProgramaFormacion(
                    nombre, descripcion, fechaInicio, fechaFin, fechaAlta, imagen);

            response.sendRedirect(
                    request.getContextPath()
                    + "/programa?nombre="
                    + URLEncoder.encode(nombre, StandardCharsets.UTF_8)
            );

        } catch (DateTimeParseException e) {
            request.setAttribute("error",
                    "Ingresá fechas válidas para el programa.");
            mostrarFormulario(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response);

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al crear el programa de formación.", e);

            request.setAttribute("error",
                    "No se pudo crear el programa. Intentá nuevamente.");

            mostrarFormulario(request, response);
        }
    }

    private void mostrarFormulario(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String[] campos = {
            "nombre", "descripcion",
            "fechaInicio", "fechaFin"
        };

        for (String campo : campos) {
            request.setAttribute(
                    campo, FormulariosDocente.valor(request, campo));
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }
}
