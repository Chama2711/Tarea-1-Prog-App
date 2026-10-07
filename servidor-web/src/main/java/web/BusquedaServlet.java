package web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import logica.*;

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
        var servidor = InicializadorAplicacion.getServidor(getServletContext());
        List<Resultado> resultados = new ArrayList<>();
        List<Curso> cursos = servidor.listarCursos();
        if (!tipo.equals("programas")) {
            for (Curso curso : cursos) {
                if (coincide(curso, instituto, categoria) && contiene(curso.getNombre(), curso.getDescripcion(), consulta))
                    resultados.add(new Resultado(curso.getNombre(), curso.getDescripcion(), "curso", curso.getFechaRegistro()));
            }
        }
        if (!tipo.equals("cursos")) {
            for (ProgramaFormacion programa : servidor.obtenerProgramas()) {
                boolean filtro = instituto.isEmpty() && categoria.isEmpty()
                        || programa.getCursos().values().stream().anyMatch(c -> coincide(c, instituto, categoria));
                if (filtro && contiene(programa.getNombre(), programa.getDescripcion(), consulta))
                    resultados.add(new Resultado(programa.getNombre(), programa.getDescripcion(), "programa", programa.getFechaAlta()));
            }
        }
        Comparator<Resultado> porNombre = Comparator.comparing(Resultado::nombre, String.CASE_INSENSITIVE_ORDER);
        resultados.sort(orden.equals("fecha")
                ? Comparator.comparing(Resultado::publicacion, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(porNombre)
                : porNombre);
        request.setAttribute("resultados", resultados);
        request.getRequestDispatcher("/WEB-INF/vistas/busqueda.jsp").forward(request, response);
    }

    private boolean coincide(Curso curso, String instituto, String categoria) {
        return (instituto.isEmpty() || curso.getInstituto() != null && instituto.equals(curso.getInstituto().getNombre()))
                && (categoria.isEmpty() || curso.getCategorias().stream().anyMatch(c -> categoria.equals(c.getNombre())));
    }
    private boolean contiene(String nombre, String descripcion, String consulta) {
        return (Objects.toString(nombre, "") + " " + Objects.toString(descripcion, "")).toLowerCase(Locale.ROOT)
                .contains(consulta.toLowerCase(Locale.ROOT));
    }
    private String valor(HttpServletRequest request, String nombre) {
        return Objects.toString(request.getParameter(nombre), "").trim();
    }
}
