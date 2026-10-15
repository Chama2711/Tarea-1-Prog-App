package web;

import java.io.IOException;
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

        HttpSession session = request.getSession(false);
        DTAutenticacion autenticado = (session != null) ? (DTAutenticacion) session.getAttribute("usuario") : null;
        
        String nickDestino = request.getParameter("nickDestino");
        String accion = request.getParameter("accion");

        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());

        if (servidor != null && autenticado != null && nickDestino != null) {
            try {
                String nickLogueado = autenticado.getNick();
                if ("seguir".equals(accion)) {
                    servidor.seguirUsuario(nickLogueado, nickDestino);
                    session.setAttribute("mensajeExito", "Ahora sigues a @" + nickDestino);
                } else if ("dejar_seguir".equals(accion)) {
                    servidor.dejarDeSeguirUsuario(nickLogueado, nickDestino);
                    session.setAttribute("mensajeExito", "Has dejado de seguir a @" + nickDestino);
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("mensajeError", "No se pudo realizar la acción: " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/usuario?nick=" + nickDestino);
    }
}