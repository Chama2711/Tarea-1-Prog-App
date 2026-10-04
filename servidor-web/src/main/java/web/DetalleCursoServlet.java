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

import logica.Curso;
import logica.IServidorCentral;

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

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        Curso curso = servidor.buscarCurso(nombre);

        if (curso == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "El curso solicitado no existe."
            );
            return;
        }

        request.setAttribute("curso", curso);
        
        try {

            request.setAttribute(
                    "categorias",
                    servidor.listarCategoriasCurso(curso.getNombre())
            );

            request.setAttribute(
                    "ediciones",
                    servidor.listarEdicionesCurso(curso)
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Error al obtener los datos del curso.",
                    e
            );
        }

        request.getRequestDispatcher(
                "/WEB-INF/vistas/detalleCurso.jsp"
        ).forward(request, response);
    }
}