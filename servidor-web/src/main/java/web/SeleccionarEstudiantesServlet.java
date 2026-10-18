package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.DTAutenticacion;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import logica.IServidorCentral;
import logica.Curso;
import logica.EdicionCurso;
import logica.Docente;
import logica.DTInscripcion;
import java.util.Map;
import java.util.HashMap;
import logica.EstadoInscripcion;


@WebServlet(name = "SeleccionarEstudiantesServlet", urlPatterns = {"/seleccionar-estudiantes"})

public class SeleccionarEstudiantesServlet extends HttpServlet 
{
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        HttpSession sesion = request.getSession(false);
        
        DTAutenticacion usuario = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");
        
        if(usuario == null || usuario.getRol() != DTAutenticacion.Rol.DOCENTE)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden acceder a esta pagina");
            
            return;
        }
        String nickDocente = usuario.getNick();
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        List<String> institutos = servidor.listarNombresInstitutosDocente(nickDocente);
        
        request.setAttribute("institutos", institutos);
        String institutoSeleccionado = request.getParameter("instituto");
        request.setAttribute("institutoSeleccionado", institutoSeleccionado);
        
        List<String> cursos = List.of();
        
        if(institutoSeleccionado != null && !institutoSeleccionado.isBlank() && institutos.contains(institutoSeleccionado))
        {
            cursos = servidor.listarNombresCursosPorInstituto(institutoSeleccionado);
        }
        
        request.setAttribute("cursos", cursos);
        String cursoSeleccionado = request.getParameter("curso");
        request.setAttribute("cursoSeleccionado", cursoSeleccionado);
        
        EdicionCurso edicionVigente = null;
        
        if(cursoSeleccionado != null && !cursoSeleccionado.isBlank() && cursos.contains(cursoSeleccionado))
        {
            Curso curso = servidor.buscarCurso(cursoSeleccionado);
            
            if(curso != null)
            {
                for(EdicionCurso edicion : servidor.listarEdicionesCurso(curso))
                {
                    boolean participa = false;
                    
                    for(Docente docente : edicion.getDocentes())
                    {
                        if(nickDocente.equals(docente.getNick()))
                        {
                            participa = true;
                            break;
                        }
                    }
                    
                    if(edicion.esVigente() && participa)
                    {
                        edicionVigente = edicion;
                        break;
                    }
                }
            }
        }
        
        request.setAttribute("edicionVigente", edicionVigente);
        
        List<DTInscripcion> inscripciones = List.of();
        
        String orden = request.getParameter("orden");
        
        if (!"prioridad".equals(orden)) 
        {
            orden = "fecha";
        }
        
        if (edicionVigente != null) 
        {
            if (orden.equals("prioridad")) 
            {
                inscripciones = servidor.listarInscripcionesPorPrioridad(nickDocente, edicionVigente.getNombre());
            } 
            else 
            {
                inscripciones = servidor.listarInscripcionesEdicion(nickDocente, edicionVigente.getNombre());
            }
        }
        
        request.setAttribute("inscripciones", inscripciones);
        request.setAttribute("ordenSeleccionado", orden);
        
        request.getRequestDispatcher("/WEB-INF/vistas/seleccionar-estudiante.jsp").forward(request, response);
    }
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        HttpSession sesion = request.getSession(false);
        
        DTAutenticacion usuario = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");
        
        if(usuario == null || usuario.getRol() != DTAutenticacion.Rol.DOCENTE)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Solo los docentes pueden seleccionar estudiantes");
            
            return;
        }
        
        String nickDocente = usuario.getNick();
        
        String nombreEdicion = request.getParameter("edicion");
        String institutoSeleccionado = request.getParameter("instituto");
        String cursoSeleccionado = request.getParameter("curso");

        if (nombreEdicion == null || nombreEdicion.isBlank()) 
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No se indicó una edición de curso");

            return;
        }
        
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        
        List<DTInscripcion> inscripciones;

        try 
        {
            inscripciones = servidor.listarInscripcionesEdicion(nickDocente,nombreEdicion);

        } 
        catch (IllegalArgumentException | IllegalStateException e) 
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());

            return;
        }
        
        Map<Long, EstadoInscripcion> decisiones = new HashMap<>();
        
        for (DTInscripcion inscripcion : inscripciones) 
        {
            if (inscripcion.getEstado() == EstadoInscripcion.INSCRIPTO) 
            {
                String nombreParametro = "decision_" + inscripcion.getId();

                String decision = request.getParameter(nombreParametro);

                if (decision != null && !decision.isBlank()) 
                {
                    EstadoInscripcion estadoSeleccionado;
                    
                    if (decision.equals("ACEPTADA")) 
                    {
                        estadoSeleccionado = EstadoInscripcion.ACEPTADA;
                    } 
                    else if (decision.equals("RECHAZADA")) 
                    {
                        estadoSeleccionado = EstadoInscripcion.RECHAZADA;
                    } 
                    else 
                    {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,"La decisión enviada no es válida");

                        return;
                    }

                    decisiones.put(inscripcion.getId(), estadoSeleccionado);
                }
            }
        }
        
            try 
            {
                servidor.seleccionarEstudiantes(nickDocente, nombreEdicion, decisiones);
            } 
            catch (IllegalArgumentException | IllegalStateException e) 
            {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

                return;
            }
            
            response.sendRedirect(request.getContextPath() + "/seleccionar-estudiantes");
    }
}