package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import logica.DTAutenticacion;
import logica.EstadoInscripcion;
import logica.RecursoNoEncontradoException;
import web.soap.generado.OperacionRechazada;

@WebServlet("/valorar-curso")
public class ValorarCursoServlet extends HttpServlet {
    private static final String VISTA = "/WEB-INF/vistas/valorarCurso.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DTAutenticacion estudiante = exigirEstudiante(request, response);
        if (estudiante == null) return;
        response.setHeader("Cache-Control", "no-store");
        try {
            var cliente = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            var perfil = cliente.consultarPerfilUsuario(estudiante.getNick(), estudiante.getNick());
            if (perfil == null || !perfil.propio() || perfil.usuario().rol() != DTAutenticacion.Rol.ESTUDIANTE) {
                throw new SecurityException("El estudiante autenticado ya no está disponible.");
            }
            List<String> cursos = perfil.inscripciones().stream()
                    .filter(i -> i.getEstado() == EstadoInscripcion.ACEPTADA)
                    .map(logica.DTInscripcion::getNombreCurso)
                    .filter(nombre -> nombre != null && !nombre.isBlank())
                    .distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
            String nombreCurso = FormulariosDocente.valor(request, "curso").trim();
            if (!nombreCurso.isEmpty() && !cursos.contains(nombreCurso)) {
                throw new SecurityException("Solo podés valorar cursos en los que tenés una inscripción aceptada.");
            }
            request.setAttribute("cursosValorables", cursos);
            request.setAttribute("cursoSeleccionado", nombreCurso);
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("promedioValoraciones", cliente.obtenerPromedioValoraciones(nombreCurso));
                request.setAttribute("valoracionPropia", cliente.obtenerValoracionEstudiante(estudiante.getNick(), nombreCurso));
            }
            request.getRequestDispatcher(VISTA).forward(request, response);
        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());
        } catch (RecursoNoEncontradoException error) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, error.getMessage());
        } catch (IllegalArgumentException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error.getMessage());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudieron consultar las valoraciones por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar las valoraciones. Intentá nuevamente más tarde.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DTAutenticacion estudiante = exigirEstudiante(request, response);
        if (estudiante == null) return;
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        String nombreCurso = FormulariosDocente.valor(request, "curso").trim();
        if (nombreCurso.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe indicar el curso que desea valorar.");
            return;
        }
        String valor = FormulariosDocente.valor(request, "puntuacion").trim();
        if (valor.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe seleccionar una puntuación.");
            return;
        }
        int puntuacion;
        try {
            puntuacion = Integer.parseInt(valor);
        } catch (NumberFormatException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La puntuación debe ser un número entero.");
            return;
        }
        if (puntuacion < 1 || puntuacion > 5) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La puntuación debe estar entre 1 y 5.");
            return;
        }
        try {
            InicializadorAplicacion.getClienteCatalogo(getServletContext()).registrarValoracionCurso(
                    estudiante.getNick(), nombreCurso, puntuacion);
            response.sendRedirect(request.getContextPath() + "/valorar-curso?curso="
                    + URLEncoder.encode(nombreCurso, StandardCharsets.UTF_8) + "&guardada=1");
        } catch (IllegalStateException error) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            request.setAttribute("errorValoracion", error.getMessage());
            doGet(request, response);
        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());
        } catch (RecursoNoEncontradoException error) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, error.getMessage());
        } catch (IllegalArgumentException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error.getMessage());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo confirmar la valoración por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo confirmar la valoración. Consultá su estado antes de volver a enviarla.");
        }
    }

    private static DTAutenticacion exigirEstudiante(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        var sesion = request.getSession(false);
        Object identidad = sesion == null ? null : sesion.getAttribute("usuario");
        if (!(identidad instanceof DTAutenticacion usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo los estudiantes pueden valorar cursos.");
            return null;
        }
        return usuario;
    }
}
