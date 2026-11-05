package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceException;

@WebServlet(name = "ProgramasServlet", urlPatterns = {"/programas"})
public class ProgramasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("programas", InicializadorAplicacion.getClienteCatalogo(getServletContext())
                    .listarProgramas());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudieron consultar los programas por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los programas. Intentá nuevamente más tarde.");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/vistas/programas.jsp").forward(request, response);
    }
}
