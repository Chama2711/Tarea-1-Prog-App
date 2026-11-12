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
import web.soap.ClienteCatalogoSoap;
import jakarta.xml.ws.WebServiceException;


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
        try {
            ClienteCatalogoSoap servidor = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            DTPerfilUsuario perfil = servidor.consultarPerfilUsuario(nick,
                    autenticado == null ? null : autenticado.getNick());
            if (perfil == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "El usuario solicitado no existe.");
                request.setAttribute("usuarios", servidor.listarUsuariosConsulta());
                request.getRequestDispatcher("/WEB-INF/vistas/usuarios.jsp").forward(request, response);
                return;
            }

            request.setAttribute("listaSeguidos", servidor.listarSeguidosConsulta(nick));
            boolean esSeguidor = autenticado != null && !autenticado.getNick().equalsIgnoreCase(nick)
                    && servidor.esSeguidor(autenticado.getNick(), nick);
            request.setAttribute("esSeguidor", esSeguidor);

            request.setAttribute("perfil", perfil);
            if (perfil.propio() && sesion != null
                    && sesion.getAttribute(ModificarUsuarioServlet.BORRADOR_PERFIL) instanceof Map<?, ?> borrador
                    && nick.equals(borrador.get("nick"))) {
                request.setAttribute("datosEdicion", borrador);
                sesion.removeAttribute(ModificarUsuarioServlet.BORRADOR_PERFIL);
            }
            request.getRequestDispatcher("/WEB-INF/vistas/detalleUsuario.jsp").forward(request, response);
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar el perfil por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar el perfil. Intentá nuevamente más tarde.");
        }
    }
}

