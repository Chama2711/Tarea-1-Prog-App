/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.IServidorCentral;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;

import jakarta.servlet.http.HttpSession;
import logica.DTAutenticacion;

/**
 *
 * @author Chama
 */
@WebServlet(name = "AltaCursoServlet", urlPatterns = {"/alta-curso"})
public class AltaCursoServlet extends HttpServlet 
{
    private boolean esDocente(HttpServletRequest request)
    {
        
        HttpSession sesion = request.getSession(false);
        
        if(sesion == null)
        {
            return false;
        }
        
        DTAutenticacion usuario = (DTAutenticacion) sesion.getAttribute("usuario");
        
        return usuario != null && usuario.getRol() == DTAutenticacion.Rol.DOCENTE;
    }
    
    
    
    private void mostrarFormularioError(HttpServletRequest request, HttpServletResponse response, String mensaje) throws ServletException, IOException
    {
        request.setAttribute("error", mensaje);
        
        String instituto = request.getParameter("instituto");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String duracion = request.getParameter("duracion");
        String horasTexto = request.getParameter("horas");
        String creditosTexto = request.getParameter("creditos");
        String url = request.getParameter("url");

        String[] categoriasSeleccionadas = request.getParameterValues("categorias");

        String[] previasSeleccionadas = request.getParameterValues("previas");
        
        
        request.setAttribute("nombreIngresado", nombre);
        request.setAttribute("descripcionIngresada", descripcion);
        request.setAttribute("duracionIngresada", duracion);
        request.setAttribute("urlIngresada", url);
        
        try
        {
            if (horasTexto != null && !horasTexto.isBlank())
            {
                request.setAttribute("horasIngresadas",Integer.valueOf(horasTexto));
            }
        }
        catch (NumberFormatException e)
        {
           
        }

        try
        {
            if (creditosTexto != null && !creditosTexto.isBlank())
            {
                request.setAttribute("creditosIngresados",Integer.valueOf(creditosTexto));
            }
        }
        catch (NumberFormatException e)
        {
            
        }
        
        request.setAttribute("categoriasSeleccionadas",categoriasSeleccionadas == null ? new ArrayList<String>() : Arrays.asList(categoriasSeleccionadas));
        
        request.setAttribute("previasSeleccionadas",previasSeleccionadas == null ? new ArrayList<String>() : Arrays.asList(previasSeleccionadas));
        
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());

        request.setAttribute("institutos",servidor.listarNombresInstitutos());

        request.setAttribute("categorias",servidor.listarNombresCategorias());

        if (instituto != null && !instituto.isBlank())
        {
            request.setAttribute("previas",servidor.listarNombresCursosPorInstituto(instituto));
        }

        request.getRequestDispatcher("/WEB-INF/vistas/altaCurso.jsp").forward(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null)
        {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        
        if (!esDocente(request))
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden dar de alta cursos");
            return;
        }
        
        
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        
        List<String> institutos = servidor.listarNombresInstitutos();
        request.setAttribute("institutos", institutos);
        
        List<String> categorias = servidor.listarNombresCategorias();
        request.setAttribute("categorias", categorias);
        
        String institutoSeleccionado = request.getParameter("instituto");
        
        if(institutoSeleccionado != null && !institutoSeleccionado.isEmpty())
        {
            List<String> previas = servidor.listarNombresCursosPorInstituto(institutoSeleccionado);
            
            request.setAttribute("previas", previas);
        }
        
        request.getRequestDispatcher("/WEB-INF/vistas/altaCurso.jsp").forward(request, response);

    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        
         if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null)
        {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        
        
        if (!esDocente(request))
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden dar de alta cursos");
            return;
        }
        
        
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        
        
        String instituto = request.getParameter("instituto");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String duracion = request.getParameter("duracion");
        String url = request.getParameter("url");
        
        String horasTexto = request.getParameter("horas");
        String creditosTexto = request.getParameter("creditos");
        
        String[] categoriasSeleccionadas = request.getParameterValues("categorias");
        
        String[] previasSeleccionadas = request.getParameterValues("previas");
        
        if (instituto == null || instituto.isBlank()
        || nombre == null || nombre.isBlank()
        || descripcion == null || descripcion.isBlank()
        || duracion == null || duracion.isBlank()
        || horasTexto == null || horasTexto.isBlank()
        || creditosTexto == null || creditosTexto.isBlank()
        || url == null || url.isBlank()
        || categoriasSeleccionadas == null)
        {
            mostrarFormularioError(request, response, "Debe completar todos los campos");

            return;
        }
        
        int horas;
        int creditos;
        
        try
        {
            horas = Integer.parseInt(horasTexto);
            creditos = Integer.parseInt(creditosTexto);
        }
        catch(NumberFormatException e)
        {
            mostrarFormularioError(request, response, "Ingrese unos numeros validos para horas y creditos");
            return;
        }
        
        if (horas <= 0 || creditos < 0)
        {
            mostrarFormularioError(request, response, "Debe ingresar correctamente las horas y los creditos");    
            
            return;
            
        }
        
        List<String> previas = new ArrayList<>();
        List<String> categorias = new ArrayList<>();
        
        if (categoriasSeleccionadas != null)
        {
            categorias = Arrays.asList(categoriasSeleccionadas);
        }
        
        if (previasSeleccionadas != null)
        {
            previas = Arrays.asList(previasSeleccionadas);
        }
        
        
        if(servidor.existeCurso(nombre))
        {
            mostrarFormularioError(request, response, "Ya existe un curso con ese nombre");

            return;
        }
        else
        {
            LocalDate fechaActual = LocalDate.now();
            
            servidor.altaCurso
            (
                instituto, 
                nombre, 
                descripcion, 
                duracion, 
                horas, 
                creditos, 
                url, 
                fechaActual, 
                previas, 
                categorias
            );
            
            response.sendRedirect(request.getContextPath() + "/cursos");
        }
        
    }

    
    
}
