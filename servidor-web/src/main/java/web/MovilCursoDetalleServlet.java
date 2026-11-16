package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import web.soap.ClienteCatalogoSoap;
import web.soap.generado.DtCursoConsulta;

@WebServlet(name = "MovilCursoDetalleServlet", urlPatterns = {"/movil/curso"})
public class MovilCursoDetalleServlet extends HttpServlet {

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
            DtCursoConsulta curso = cliente.consultarCurso(nombre);

            if (curso == null) {
                response.sendRedirect(request.getContextPath() + "/movil/cursos");
                return;
            }

            request.setAttribute("curso", curso);
            request.setAttribute("categorias", cliente.listarCategoriasCurso(curso.getNombre()));
            request.setAttribute("ediciones", curso.getEdicion());
            request.getRequestDispatcher("/WEB-INF/vistas/movil/detalleCurso.jsp")
                    .forward(request, response);

        } catch (WebServiceException error) {
            getServletContext().log("Error al consultar curso en móvil.", error);
            response.sendRedirect(request.getContextPath() + "/movil/cursos");
        }
    }
}