/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logica.IServidorCentral;
import logica.ProgramaFormacion;

@WebServlet(name = "ProgramasServlet", urlPatterns = {"/programas"})
public class ProgramasServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        List<ProgramaFormacion> programas =
                servidor.obtenerProgramas();

        request.setAttribute("programas", programas);

        request.getRequestDispatcher(
                "/WEB-INF/vistas/programas.jsp"
        ).forward(request, response);
    }
}