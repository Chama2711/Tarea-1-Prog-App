package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import web.soap.ClienteCatalogoSoap;
import web.soap.generado.DtEdicionConsulta;

@WebServlet(name = "MovilEdicionDetalleServlet", urlPatterns = {"/movil/edicion"})
public class MovilEdicionDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/movil/cursos");
            return;
        }

        try {
            ClienteCatalogoSoap cliente =
                    InicializadorAplicacion.getClienteCatalogo(getServletContext());
            DtEdicionConsulta edicion = cliente.consultarEdicion(nombre);

            if (edicion == null) {
                response.sendRedirect(request.getContextPath() + "/movil/cursos");
                return;
            }

            request.setAttribute("edicion", edicion);
            request.getRequestDispatcher("/WEB-INF/vistas/movil/detalleEdicion.jsp")
                    .forward(request, response);

        } catch (WebServiceException error) {
            getServletContext().log("Error al consultar edición en móvil.", error);
            response.sendRedirect(request.getContextPath() + "/movil/cursos");
        }
    }
}