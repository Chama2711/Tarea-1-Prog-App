/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "DetalleCursoServlet", urlPatterns = {"/curso"})
public class DetalleCursoServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isBlank()) {
            response.sendRedirect(
                    request.getContextPath() + "/cursos"
            );
            return;
        }

        try {
            var curso = InicializadorAplicacion.getClienteCatalogo(getServletContext()).consultarCurso(nombre);
            if (curso == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El curso solicitado no existe.");
                return;
            }
            request.setAttribute("curso", curso);
            request.setAttribute("categorias", curso.getCategoria());
            request.setAttribute("ediciones", curso.getEdicion());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar el detalle del curso por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar el curso. Intentá nuevamente más tarde.");
            return;
        }

        request.getRequestDispatcher(
                "/WEB-INF/vistas/detalleCurso.jsp"
        ).forward(request, response);
    }
}