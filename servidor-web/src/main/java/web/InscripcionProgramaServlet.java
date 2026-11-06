package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import logica.DTAutenticacion;
import logica.RecursoNoEncontradoException;

public class InscripcionProgramaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var sesion = request.getSession(false);
        var usuario = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo los estudiantes pueden inscribirse a un programa.");
            return;
        }
        String nombre = FormulariosDocente.valor(request, "programa");
        if (nombre.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe seleccionar un programa.");
            return;
        }
        String destino = request.getContextPath() + "/programa?nombre=" + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
        try {
            InicializadorAplicacion.getClienteCatalogo(getServletContext()).inscribirEstudianteAPrograma(usuario.getNick(), nombre);
            response.sendRedirect(destino + "&inscripcion=ok");
        } catch (RecursoNoEncontradoException error) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, error.getMessage());
        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());
        } catch (IllegalArgumentException error) {
            response.sendRedirect(destino + "&errorInscripcion=" + URLEncoder.encode(error.getMessage(), StandardCharsets.UTF_8));
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo registrar la inscripción por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo confirmar la inscripción. Revisá su estado antes de volver a enviarla.");
        }
    }
}
