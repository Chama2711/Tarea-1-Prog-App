package web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import web.soap.generado.DtCursoConsulta;

@WebServlet("/busqueda")
public class BusquedaServlet extends HttpServlet {
    public record Resultado(String nombre, String descripcion, String tipo, LocalDate publicacion) { }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String consulta = valor(request, "q");
        String tipo = valor(request, "tipo");
        String orden = valor(request, "orden");
        String instituto = valor(request, "instituto");
        String categoria = valor(request, "categoria");
        try {
            var cliente = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            List<Resultado> resultados = new ArrayList<>();
            List<DtCursoConsulta> cursos = ConsultasCursos.listar(
                    cliente, instituto);
            Map<String, DtCursoConsulta> cursosPorNombre = new HashMap<>();
            for (var curso : cursos) cursosPorNombre.put(curso.getNombre(), curso);
            if (!tipo.equals("programas")) {
                for (DtCursoConsulta curso : cursos) {
                    if (coincide(curso, instituto, categoria) && contiene(curso.getNombre(), curso.getDescripcion(), consulta))
                        resultados.add(new Resultado(curso.getNombre(), curso.getDescripcion(), "curso", ConsultasCursos.fecha(curso.getFechaRegistro())));
                }
            }
            if (!tipo.equals("cursos")) {
                for (var programa : cliente.listarProgramas()) {
                    boolean filtro = instituto.isEmpty() && categoria.isEmpty()
                            || programa.getCurso().stream().map(web.soap.generado.DtCursoResumen::getNombre)
                                    .map(cursosPorNombre::get).filter(Objects::nonNull)
                                    .anyMatch(c -> coincide(c, instituto, categoria));
                    if (filtro && contiene(programa.getNombre(), programa.getDescripcion(), consulta))
                        resultados.add(new Resultado(programa.getNombre(), programa.getDescripcion(), "programa", ConsultasCursos.fecha(programa.getFechaAlta())));
                }
            }
            Comparator<Resultado> porNombre = Comparator.comparing(Resultado::nombre, String.CASE_INSENSITIVE_ORDER);
            resultados.sort(orden.equals("fecha")
                    ? Comparator.comparing(Resultado::publicacion, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(porNombre)
                    : porNombre);
            request.setAttribute("resultados", resultados);
            request.getRequestDispatcher("/WEB-INF/vistas/busqueda.jsp").forward(request, response);
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar el catálogo de búsqueda por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar el catálogo. Intentá nuevamente más tarde.");
        }
    }

    private boolean coincide(DtCursoConsulta curso, String instituto, String categoria) {
        return (instituto.isEmpty() || instituto.equals(curso.getNombreInstituto()))
                && (categoria.isEmpty() || curso.getCategoria().contains(categoria));
    }
    private boolean contiene(String nombre, String descripcion, String consulta) {
        return (Objects.toString(nombre, "") + " " + Objects.toString(descripcion, "")).toLowerCase(Locale.ROOT)
                .contains(consulta.toLowerCase(Locale.ROOT));
    }
    private String valor(HttpServletRequest request, String nombre) {
        return Objects.toString(request.getParameter(nombre), "").trim();
    }
}
