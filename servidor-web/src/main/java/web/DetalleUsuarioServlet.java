package web;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.DTAutenticacion;
import logica.DTPerfilUsuario;
import logica.IServidorCentral;
import logica.Usuario;

@WebServlet(name = "DetalleUsuarioServlet", urlPatterns = {"/usuario"})
public class DetalleUsuarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        String nick = request.getParameter("nick");
        if (nick == null || nick.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/usuarios");
            return;
        }
        // La identidad se obtiene exclusivamente de la sesión, nunca de parámetros del navegador.
        var sesion = request.getSession(false);
        DTAutenticacion autenticado = sesion == null ? null
                : (DTAutenticacion) sesion.getAttribute("usuario");
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        DTPerfilUsuario perfil = servidor.consultarPerfilUsuario(nick,
                autenticado == null ? null : autenticado.getNick());
        if (perfil == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", "El usuario solicitado no existe.");
            request.setAttribute("usuarios", servidor.listarUsuariosConsulta());
            request.getRequestDispatcher("/WEB-INF/vistas/usuarios.jsp").forward(request, response);
            return;
        }
        
        // =========================================================
        // LÓGICA PARA PESTAÑA SEGUIDOS Y BOTÓN SEGUIR
        // =========================================================
        Usuario uConsultado = servidor.obtenerUsuario(nick);
        if (uConsultado != null) {
            // Pasa la lista de usuarios seguidos por el usuario del perfil
            request.setAttribute("listaSeguidos", uConsultado.getSeguidos());
        }

        boolean esSeguidor = false;
        if (autenticado != null && uConsultado != null && !autenticado.getNick().equalsIgnoreCase(nick)) {
            Usuario uLogueado = servidor.obtenerUsuario(autenticado.getNick());
            if (uLogueado != null && uLogueado.getSeguidos() != null) {
                // Comparación por nickname para asegurar coincidencia de objetos en memoria
                esSeguidor = uLogueado.getSeguidos().stream()
                .anyMatch(u -> u.getNick().equalsIgnoreCase(uConsultado.getNick()));
            }
        }
        request.setAttribute("esSeguidor", esSeguidor);
        
        request.setAttribute("perfil", perfil);
        if (perfil.propio() && sesion != null
                && sesion.getAttribute(ModificarUsuarioServlet.BORRADOR_PERFIL) instanceof Map<?, ?> borrador
                && nick.equals(borrador.get("nick"))) {
            request.setAttribute("datosEdicion", borrador);
            sesion.removeAttribute(ModificarUsuarioServlet.BORRADOR_PERFIL);
        }
        request.getRequestDispatcher("/WEB-INF/vistas/detalleUsuario.jsp").forward(request, response);
    }
}

