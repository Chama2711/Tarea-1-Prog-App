package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.IServidorCentral;

@WebServlet(name = "InicioServlet", urlPatterns = {"", "/inicio"})
public class InicioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        try {
            request.setAttribute("cursos", ConsultasCursos.listar(
                    InicializadorAplicacion.getClienteCatalogo(getServletContext()), null));
        } catch (WebServiceException error) {
            getServletContext().log("No se pudieron consultar los cursos por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los cursos. Intentá nuevamente más tarde.");
            return;
        }
        request.setAttribute("programas", servidor.obtenerProgramas());
        request.setAttribute("cantidadProgramas", servidor.listarNombresProgramas().size());
        // La navegación ya consultó las categorías por SOAP en esta petición.
        var categorias = (java.util.List<?>) request.getAttribute("navCategorias");
        request.setAttribute("cantidadCategorias", categorias == null ? 0 : categorias.size());
        request.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(request, response);
    }
}
