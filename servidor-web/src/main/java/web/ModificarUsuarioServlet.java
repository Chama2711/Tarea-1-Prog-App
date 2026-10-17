package web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import logica.DTAutenticacion;
import logica.IServidorCentral;

@WebServlet(name = "ModificarUsuarioServlet", urlPatterns = {"/modificarUsuario"})
public class ModificarUsuarioServlet extends HttpServlet {
    static final String BORRADOR_PERFIL = "borradorModificacionPerfil";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession(false);
        DTAutenticacion autenticado = (session != null) ? (DTAutenticacion) session.getAttribute("usuario") : null;

        // Validar sesión activa
        if (autenticado == null) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        String nickname = autenticado.getNick();
        String nuevoNombre = request.getParameter("nombre");
        String nuevoApellido = request.getParameter("apellido");
        String fechaNacStr = request.getParameter("fechaNacimiento");
        String nuevaClave = request.getParameter("clave");
        String confirmacionClave = request.getParameter("confirmacionClave");

        try {
            LocalDate nuevaFechaNac = null;
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                nuevaFechaNac = LocalDate.parse(fechaNacStr); // Formato YYYY-MM-DD
            }

            // Obtención e invocación del servidor central
            IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
            servidor.modificarUsuario(nickname, nuevoNombre, nuevoApellido, nuevaFechaNac, nuevaClave, confirmacionClave);

            // Actualizar el objeto guardado en la sesión
            autenticado.setNombre(nuevoNombre.trim());
            autenticado.setApellido(nuevoApellido.trim());
            session.setAttribute("usuario", autenticado);

            session.removeAttribute(BORRADOR_PERFIL);
            session.removeAttribute("mensajeError");
            session.setAttribute("mensajeExito", "Perfil modificado correctamente.");
        } catch (DateTimeParseException e) {
            conservarEdicion(session, nickname, nuevoNombre, nuevoApellido, fechaNacStr,
                    "El formato de la fecha de nacimiento no es válido.");
        } catch (IllegalArgumentException e) {
            conservarEdicion(session, nickname, nuevoNombre, nuevoApellido, fechaNacStr, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("No se pudo modificar el perfil.", e);
            conservarEdicion(session, nickname, nuevoNombre, nuevoApellido, fechaNacStr,
                    "No se pudo modificar el perfil. Intentá nuevamente.");
        }

        // Redirigir al perfil del usuario
        response.sendRedirect(request.getContextPath() + "/usuario?nick="
                + URLEncoder.encode(nickname, StandardCharsets.UTF_8) + "#perfil-general");
    }

    private static void conservarEdicion(HttpSession session, String nick, String nombre,
            String apellido, String fecha, String error) {
        // Nunca conservar contraseñas en la sesión ni devolverlas al formulario.
        session.setAttribute(BORRADOR_PERFIL, Map.of("nick", nick,
                "nombre", nombre == null ? "" : nombre,
                "apellido", apellido == null ? "" : apellido,
                "fechaNacimiento", fecha == null ? "" : fecha));
        session.removeAttribute("mensajeExito");
        session.setAttribute("mensajeError", error);
    }
}
