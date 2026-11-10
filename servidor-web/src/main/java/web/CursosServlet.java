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


@WebServlet(name = "CursosServlet", urlPatterns = {"/cursos"})
public class CursosServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("cursos", ConsultasCursos.listar(
                    InicializadorAplicacion.getClienteCatalogo(getServletContext()), null));
        } catch (WebServiceException error) {
            getServletContext().log("No se pudieron consultar los cursos por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los cursos. Intentá nuevamente más tarde.");
            return;
        }

        request.getRequestDispatcher(
                "/WEB-INF/vistas/cursos.jsp"
        ).forward(request, response);
    }
}
