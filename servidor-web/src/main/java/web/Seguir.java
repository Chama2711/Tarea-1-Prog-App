package web;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import logica.DTAutenticacion;
import logica.IServidorCentral;

@WebServlet(name = "Seguir", urlPatterns = {"/Seguir"})
public class Seguir extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession(false);
        Object identidad = session == null ? null : session.getAttribute("usuario");
        if (!(identidad instanceof DTAutenticacion autenticado)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String nickDestino = FormulariosDocente.valor(request, "nickDestino").trim();
        String accion = request.getParameter("accion");
        if (nickDestino.isEmpty() || (!"seguir".equals(accion) && !"dejar_seguir".equals(accion))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Indique un usuario y una acción válida.");
            return;
        }

        session.removeAttribute("mensajeExito");
        session.removeAttribute("mensajeError");
        String destino = nickDestino;
        try {
            IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
            if ("seguir".equals(accion)) {
                servidor.seguirUsuario(autenticado.getNick(), nickDestino);
                session.setAttribute("mensajeExito", "Ahora seguís a @" + nickDestino);
            } else {
                servidor.dejarDeSeguirUsuario(autenticado.getNick(), nickDestino);
                session.setAttribute("mensajeExito", "Dejaste de seguir a @" + nickDestino);
            }
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            session.setAttribute("mensajeError", e.getMessage());
            destino = autenticado.getNick();
        } catch (Exception e) {
            getServletContext().log("No se pudo actualizar el seguimiento de usuario.", e);
            session.setAttribute("mensajeError", "No se pudo realizar la acción. Intentá nuevamente.");
            destino = autenticado.getNick();
        }

        response.sendRedirect(request.getContextPath() + "/usuario?nick="
                + URLEncoder.encode(destino, StandardCharsets.UTF_8));
    }
}
