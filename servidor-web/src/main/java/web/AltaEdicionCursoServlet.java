/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import jakarta.servlet.http.HttpSession;
import logica.DTAutenticacion;

/**
 *
 * @author Chama
 */
@WebServlet(name = "AltaEdicionCursoServlet", urlPatterns = {"/alta-edicion-curso"})
public class AltaEdicionCursoServlet extends HttpServlet {

    private boolean esDocente(HttpServletRequest request) 
    {

        HttpSession sesion = request.getSession(false);

        if (sesion == null) 
        {
            return false;
        }

        DTAutenticacion usuario = (DTAutenticacion) sesion.getAttribute("usuario");

        return usuario != null && usuario.getRol() == DTAutenticacion.Rol.DOCENTE;
    }
    
 

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) 
        {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!esDocente(request)) 
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden crear ediciones de cursos");
            return;
        }
        
        IServidorCentral servidor =
        InicializadorAplicacion.getServidor(getServletContext());

        List<String> institutos = servidor.listarNombresInstitutos();

        request.setAttribute("institutos", institutos);
        
        
        String instituto = request.getParameter("instituto");
        
        if (instituto != null && !instituto.isBlank()) 
        {
            List<String> cursos = servidor.listarNombresCursosPorInstituto(instituto);
             
            List<String> docentes = servidor.listarNicknamesDocentesPorInstituto(instituto);
        
            request.setAttribute("cursos", cursos);
            request.setAttribute("docentes", docentes);
            request.setAttribute("institutoSeleccionado", instituto);
        }
        
        request.getRequestDispatcher("/WEB-INF/vistas/altaEdicionCurso.jsp").forward(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) 
        {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!esDocente(request)) 
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden crear ediciones de cursos");
            return;
        }
        
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        
        String instituto = request.getParameter("instituto");
        String curso = request.getParameter("curso");
        String nombre = request.getParameter("nombre");
        String fechaInicioTexto = request.getParameter("fechaInicio");
        String fechaFinTexto = request.getParameter("fechaFin");
        String cuposTexto = request.getParameter("cupos");
        String[] docentesSeleccionados = request.getParameterValues("docentes");
                
        
        try 
        {
            if (instituto == null || instituto.isBlank()
                || curso == null || curso.isBlank()
                || nombre == null || nombre.isBlank()
                || fechaInicioTexto == null || fechaInicioTexto.isBlank()
                || fechaFinTexto == null || fechaFinTexto.isBlank()) 
            {
                throw new IllegalArgumentException("Debe completar todos los campos obligatorios");
            }

        
        
            LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto);
            LocalDate fechaFin = LocalDate.parse(fechaFinTexto);

            if(fechaFin.isBefore(fechaInicio))
            {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio");
            }

            int cupos = -1;

            if(cuposTexto != null && !cuposTexto.isBlank())
            {
                cupos = Integer.parseInt(cuposTexto);

                if(cupos <= 0)
                {
                    throw new IllegalArgumentException("Los cupos deben ser mayores que cero.");
                }
            }

            List<String> docentes = new ArrayList<>();

            if(docentesSeleccionados != null)
            {
                docentes = Arrays.asList(docentesSeleccionados);
            }
            
            servidor.altaEdicionCurso(instituto, curso, nombre, fechaInicio, fechaFin, cupos, docentes);
            
            response.sendRedirect(request.getContextPath() + "/inicio");
        }
        catch(IllegalArgumentException e)
        {
            request.setAttribute("error", e.getMessage());  
            
            request.setAttribute("cursoIngresado", curso);
            request.setAttribute("nombreIngresado", nombre);
            request.setAttribute("fechaInicioIngresada", fechaInicioTexto);
            request.setAttribute("fechaFinIngresada", fechaFinTexto);
            request.setAttribute("cuposIngresados", cuposTexto);

            request.setAttribute("docentesSeleccionados",docentesSeleccionados == null ? new ArrayList<String>() : Arrays.asList(docentesSeleccionados));

            request.setAttribute("institutos",servidor.listarNombresInstitutos());

            if(instituto != null && !instituto.isBlank())
            {
                request.setAttribute("institutoSeleccionado", instituto);

                request.setAttribute("cursos",servidor.listarNombresCursosPorInstituto(instituto));

                request.setAttribute("docentes",servidor.listarNicknamesDocentesPorInstituto(instituto));
            }
            
             request.getRequestDispatcher("/WEB-INF/vistas/altaEdicionCurso.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
