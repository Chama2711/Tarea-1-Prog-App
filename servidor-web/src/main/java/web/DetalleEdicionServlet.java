package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;

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
        try {
            var edicion = InicializadorAplicacion.getClienteCatalogo(getServletContext()).consultarEdicion(nombre);
            if (edicion == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "La edición solicitada no existe.");
                return;
            }
            request.setAttribute("edicion", edicion);
            request.getRequestDispatcher("/WEB-INF/vistas/detalleEdicion.jsp").forward(request, response);
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar la edición por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar la edición. Intentá nuevamente más tarde.");
        }
    }
}

