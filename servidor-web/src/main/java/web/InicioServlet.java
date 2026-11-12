package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "InicioServlet", urlPatterns = {"", "/inicio"})
public class InicioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            var cliente = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            request.setAttribute("cursos", ConsultasCursos.listar(
                    cliente, null));
            var programas = cliente.listarProgramas();
            request.setAttribute("programas", programas);
            request.setAttribute("cantidadProgramas", programas.size());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar el catálogo por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar el catálogo. Intentá nuevamente más tarde.");
            return;
        }
        // La navegación ya consultó las categorías por SOAP en esta petición.
        var categorias = (java.util.List<?>) request.getAttribute("navCategorias");
        request.setAttribute("cantidadCategorias", categorias == null ? 0 : categorias.size());
        request.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(request, response);
    }
}
