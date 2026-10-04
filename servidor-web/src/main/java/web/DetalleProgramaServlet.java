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

import logica.IServidorCentral;
import logica.ProgramaFormacion;

@WebServlet(
        name = "DetalleProgramaServlet",
        urlPatterns = {"/programa"}
)
public class DetalleProgramaServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isBlank()) {
            response.sendRedirect(
                    request.getContextPath() + "/programas"
            );
            return;
        }

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        ProgramaFormacion programa =
                servidor.obtenerDetallePrograma(nombre);

        if (programa == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "El programa solicitado no existe."
            );
            return;
        }

        request.setAttribute("programa", programa);

        request.getRequestDispatcher(
                "/WEB-INF/vistas/detallePrograma.jsp"
        ).forward(request, response);
    }
}
