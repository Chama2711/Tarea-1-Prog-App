package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import logica.DTAutenticacion;
import logica.RecursoNoEncontradoException;

public class CancelarInscripcionEdicionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        var sesion = request.getSession(false);

        DTAutenticacion usuario = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo los estudiantes pueden cancelar sus inscripciones.");
            return;
        }

        String nombreEdicion = FormulariosDocente.valor(request, "edicion");

        if (nombreEdicion.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Debe indicar una edición.");
            return;
        }

        String destino = request.getContextPath()
                + "/edicion?nombre="
                + URLEncoder.encode(nombreEdicion, StandardCharsets.UTF_8);

        try {
            InicializadorAplicacion.getClienteCatalogo(getServletContext())
                    .cancelarInscripcionEdicion(usuario.getNick(), nombreEdicion);

            response.sendRedirect(destino + "&cancelacion=ok");

        } catch (RecursoNoEncontradoException error) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, error.getMessage());

        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());

        } catch (IllegalArgumentException error) {
            response.sendRedirect(destino + "&errorInscripcion="
                    + URLEncoder.encode(error.getMessage(), StandardCharsets.UTF_8));

        } catch (WebServiceException error) {
            getServletContext().log("No se pudo cancelar la inscripción por SOAP.", error);

            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo confirmar la cancelación. Revisá el estado de la inscripción.");
        }
    }
}