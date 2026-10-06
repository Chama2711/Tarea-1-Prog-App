package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.DTEdicionConsulta;

@WebServlet(name = "DetalleEdicionServlet", urlPatterns = {"/edicion"})
public class DetalleEdicionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/cursos");
            return;
        }
        DTEdicionConsulta edicion = InicializadorAplicacion.getServidor(getServletContext())
                .consultarEdicion(nombre);
        if (edicion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "La edición solicitada no existe.");
            return;
        }
        request.setAttribute("edicion", edicion);
        request.getRequestDispatcher("/WEB-INF/vistas/detalleEdicion.jsp").forward(request, response);
    }
}

