package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuariosServlet", urlPatterns = {"/usuarios"})
public class UsuariosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("usuarios", InicializadorAplicacion
                    .getClienteCatalogo(getServletContext()).listarUsuariosConsulta());
            request.getRequestDispatcher("/WEB-INF/vistas/usuarios.jsp").forward(request, response);
        } catch (jakarta.xml.ws.WebServiceException error) {
            getServletContext().log("No se pudieron consultar los usuarios por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los usuarios. Intentá nuevamente más tarde.");
        }
    }
}

