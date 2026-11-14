package web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import logica.DTEdicionConsulta;

/** Selección por instituto y curso para las dos pantallas de evaluaciones. */
final class SeleccionEvaluaciones {
    private SeleccionEvaluaciones() { }

    static String preparar(HttpServletRequest request, ServletContext contexto, String docente,
            List<String> autorizadas, String edicion, boolean resultados) {
        String instituto = valor(request, "instituto");
        String curso = valor(request, "curso");
        request.setAttribute("institutoEvaluaciones", instituto);
        request.setAttribute("cursoEvaluaciones", curso);
        if (instituto.isEmpty() && curso.isEmpty() && edicion.isEmpty()) {
            return edicion;
        }

        var catalogo = InicializadorAplicacion.getClienteCatalogo(contexto);
        if (instituto.isEmpty() && curso.isEmpty()) {
            request.setAttribute("detalleEdicion", catalogo.consultarDetalleEdicion(edicion));
            return edicion;
        }

        if (!catalogo.listarInstitutos().contains(instituto)) {
            throw new IllegalArgumentException("Seleccioná un instituto válido.");
        }
        List<String> cursos = catalogo.listarNombresCursosPorInstituto(instituto);
        request.setAttribute("cursosEvaluaciones", cursos);
        if (curso.isEmpty()) {
            return "";
        }
        if (!cursos.contains(curso)) {
            throw new IllegalArgumentException("El curso no pertenece al instituto seleccionado.");
        }

        List<DTEdicionConsulta> opciones;
        if (resultados) {
            opciones = autorizadas.stream()
                    .map(catalogo::consultarDetalleEdicion)
                    .filter(Objects::nonNull)
                    .filter(detalle -> curso.equals(detalle.nombreCurso()))
                    .filter(DTEdicionConsulta::cerrada)
                    .toList();
        } else {
            opciones = catalogo.listarEdicionesParaSeleccion(docente, instituto, curso);
        }
        request.setAttribute("edicionesEvaluaciones", opciones);

        if (!edicion.isEmpty()) {
            var elegida = opciones.stream().filter(opcion -> opcion.nombre().equals(edicion)).findFirst()
                    .orElseThrow(() -> new SecurityException("La edición no está disponible para la selección indicada."));
            request.setAttribute("detalleEdicion", elegida);
            return elegida.nombre();
        }
        if (opciones.size() == 1) {
            var elegida = opciones.getFirst();
            request.setAttribute("detalleEdicion", elegida);
            return elegida.nombre();
        }
        return "";
    }

    static String parametros(HttpServletRequest request) {
        String instituto = valor(request, "instituto");
        String curso = valor(request, "curso");
        return instituto.isEmpty() || curso.isEmpty() ? ""
                : "&instituto=" + URLEncoder.encode(instituto, StandardCharsets.UTF_8)
                + "&curso=" + URLEncoder.encode(curso, StandardCharsets.UTF_8);
    }

    private static String valor(HttpServletRequest request, String nombre) {
        return FormulariosDocente.valor(request, nombre).trim();
    }
}
