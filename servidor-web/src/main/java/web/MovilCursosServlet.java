package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import web.soap.ClienteCatalogoSoap;
import web.soap.generado.DtCursoConsulta;

@WebServlet(name = "MovilCursosServlet", urlPatterns = {"/movil/cursos"})
public class MovilCursosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String instituto = request.getParameter("instituto");
        String categoria = request.getParameter("categoria");

        try {
            ClienteCatalogoSoap cliente =
                    InicializadorAplicacion.getClienteCatalogo(getServletContext());
            List<DtCursoConsulta> cursos = ConsultasCursos.listar(cliente, instituto);

            if (categoria != null && !categoria.isBlank()) {
                List<DtCursoConsulta> filtrados = new ArrayList<>();
                for (DtCursoConsulta curso : cursos) {
                    List<String> catsCurso = cliente.listarCategoriasCurso(curso.getNombre());
                    if (catsCurso != null && catsCurso.contains(categoria)) {
                        filtrados.add(curso);
                    }
                }
                cursos = filtrados;
            }

            request.setAttribute("cursos", cursos);
            request.setAttribute("institutoSeleccionado", instituto == null ? "" : instituto);
            request.setAttribute("categoriaSeleccionada", categoria == null ? "" : categoria);
        } catch (WebServiceException error) {
            getServletContext().log("Error al listar cursos en móvil.", error);
            request.setAttribute("error", "No se pudieron cargar los cursos en este momento.");
            request.setAttribute("cursos", List.of());
        }

        request.getRequestDispatcher("/WEB-INF/vistas/movil/cursos.jsp")
                .forward(request, response);
    }
}