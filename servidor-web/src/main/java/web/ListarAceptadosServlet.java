/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

import logica.DTAutenticacion;
import logica.IServidorCentral;
import logica.Curso;
import logica.EdicionCurso;
import logica.Docente;
import logica.DTInscripcion;

/**
 *
 * @author Chama
 */
@WebServlet(name = "ListarAceptadosServlet", urlPatterns = {"/listar-aceptados"})

public class ListarAceptadosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException 
    {
        HttpSession sesion = request.getSession(false);
        
        DTAutenticacion usuario = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");
        
        if (usuario == null || usuario.getRol() != DTAutenticacion.Rol.DOCENTE) 
        {
             response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden consultar estudiantes aceptados");
             
             return;
        }
        
        String nickDocente = usuario.getNick();

        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());

        List<String> institutos =  servidor.listarNombresInstitutosDocente(nickDocente);

        request.setAttribute("institutos", institutos);
        
        String institutoSeleccionado = request.getParameter("instituto");

        request.setAttribute("institutoSeleccionado", institutoSeleccionado);

        List<String> cursos = List.of();

        if (institutoSeleccionado != null && !institutoSeleccionado.isBlank() && institutos.contains(institutoSeleccionado))
        {
            cursos = servidor.listarNombresCursosPorInstituto(institutoSeleccionado);
        }

        request.setAttribute("cursos", cursos);
        
        String cursoSeleccionado = request.getParameter("curso");

        request.setAttribute("cursoSeleccionado", cursoSeleccionado);

        List<EdicionCurso> ediciones = new ArrayList<>();

        if (cursoSeleccionado != null && !cursoSeleccionado.isBlank() && cursos.contains(cursoSeleccionado))
        {
            Curso curso = servidor.buscarCurso(cursoSeleccionado);

            if (curso != null)
            {
                for (EdicionCurso edicion : servidor.listarEdicionesCurso(curso))
                {
                    boolean participa = false;

                    for (Docente docente : edicion.getDocentes())
                    {
                        if (nickDocente.equals(docente.getNick()))
                        {
                            participa = true;
                            break;
                        }
                    }

                    if (participa)
                    {
                        ediciones.add(edicion);
                    }
                }
            }
        }

        request.setAttribute("ediciones", ediciones);
        
        String edicionSeleccionada = request.getParameter("edicion");
        
        request.setAttribute("edicionSeleccionada", edicionSeleccionada);
        
        List<DTInscripcion> aceptados = List.of();
        
        EdicionCurso edicionElegida = null;

        for (EdicionCurso edicion : ediciones)
        {
            if (edicion.getNombre().equals(edicionSeleccionada))
            {
                edicionElegida = edicion;
                break;
            }
        }

        if (edicionElegida != null)
        {
            aceptados = servidor.listarAceptadosEdicion(nickDocente, edicionElegida.getNombre());
        }

        request.setAttribute("edicionElegida", edicionElegida);
        request.setAttribute("aceptados", aceptados);
        
        request.getRequestDispatcher("/WEB-INF/vistas/listar-aceptados.jsp").forward(request, response);
    
    }
}
