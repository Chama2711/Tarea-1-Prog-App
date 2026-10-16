package web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
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
            autenticado.setNombre(nuevoNombre);
            autenticado.setApellido(nuevoApellido);
            session.setAttribute("usuario", autenticado);

            session.setAttribute("mensajeExito", "Perfil modificado correctamente.");
        } catch (DateTimeParseException e) {
            session.setAttribute("mensajeError", "El formato de la fecha de nacimiento no es válido.");
        } catch (Exception e) {
            session.setAttribute("mensajeError", e.getMessage());
        }

        // Redirigir al perfil del usuario
        response.sendRedirect(request.getContextPath() + "/usuario?nick=" + nickname);
    }
}