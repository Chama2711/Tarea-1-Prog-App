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
import web.soap.generado.DtEdicionResumen;

@WebServlet(name = "MovilEdicionesServlet", urlPatterns = {"/movil/ediciones"})
public class MovilEdicionesServlet extends HttpServlet {

    public record EdicionConCurso(String nombreCurso, String instituto, DtEdicionResumen edicion) { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String instituto = request.getParameter("instituto");
        String categoria = request.getParameter("categoria");
        String cursoFiltro = request.getParameter("curso");

        try {
            ClienteCatalogoSoap cliente =
                    InicializadorAplicacion.getClienteCatalogo(getServletContext());
            List<DtCursoConsulta> cursos = ConsultasCursos.listar(cliente, instituto);

            if (categoria != null && !categoria.isBlank()) {
                List<DtCursoConsulta> filtrados = new ArrayList<>();
                for (DtCursoConsulta c : cursos) {
                    List<String> cats = cliente.listarCategoriasCurso(c.getNombre());
                    if (cats != null && cats.contains(categoria)) {
                        filtrados.add(c);
                    }
                }
                cursos = filtrados;
            }

            List<EdicionConCurso> ediciones = new ArrayList<>();
            for (DtCursoConsulta c : cursos) {
                if (cursoFiltro != null && !cursoFiltro.isBlank()
                        && !cursoFiltro.equals(c.getNombre())) {
                    continue;
                }
                if (c.getEdicion() != null) {
                    for (DtEdicionResumen ed : c.getEdicion()) {
                        ediciones.add(new EdicionConCurso(
                                c.getNombre(), c.getNombreInstituto(), ed));
                    }
                }
            }

            request.setAttribute("cursosDisponibles", cursos);
            request.setAttribute("ediciones", ediciones);
            request.setAttribute("institutoSeleccionado", instituto == null ? "" : instituto);
            request.setAttribute("categoriaSeleccionada", categoria == null ? "" : categoria);
            request.setAttribute("cursoSeleccionado", cursoFiltro == null ? "" : cursoFiltro);

        } catch (WebServiceException error) {
            getServletContext().log("Error al listar ediciones en móvil.", error);
            request.setAttribute("error", "No se pudieron cargar las ediciones en este momento.");
            request.setAttribute("cursosDisponibles", List.of());
            request.setAttribute("ediciones", List.of());
        }

        request.getRequestDispatcher("/WEB-INF/vistas/movil/ediciones.jsp")
                .forward(request, response);
    }
}