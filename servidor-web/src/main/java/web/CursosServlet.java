/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logica.Curso;
import logica.IServidorCentral;

@WebServlet(name = "CursosServlet", urlPatterns = {"/cursos"})
public class CursosServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        ArrayList<Curso> cursos = servidor.listarCursos();

        request.setAttribute("cursos", cursos);

        request.getRequestDispatcher(
                "/WEB-INF/vistas/cursos.jsp"
        ).forward(request, response);
    }
}
