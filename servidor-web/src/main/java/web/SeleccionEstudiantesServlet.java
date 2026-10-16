package web;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import logica.*;

@WebServlet("/seleccion-estudiantes")
public class SeleccionEstudiantesServlet extends HttpServlet {
    private static final String VISTA = "/WEB-INF/vistas/seleccionarEstudiantes.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) return;
        response.setHeader("Cache-Control", "no-store");
        try {
            preparar(request, docente);
            mostrar(request, response);
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) return;
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        try {
            preparar(request, docente);
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage()); return;
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage()); return;
        }
        var decisiones = new LinkedHashMap<Long, EstadoInscripcion>();
        try {
            if (valor(request, "edicion").isEmpty()) throw new IllegalArgumentException("Seleccione una edición.");
            for (var parametro : request.getParameterMap().entrySet()) {
                if (!parametro.getKey().startsWith("decision_")) continue;
                String[] valores = parametro.getValue();
                if (valores.length != 1) throw new IllegalArgumentException("Indique una sola decisión por inscripción.");
                if (valores[0].isEmpty()) continue;
                long id;
                EstadoInscripcion estado;
                try {
                    id = Long.parseLong(parametro.getKey().substring("decision_".length()));
                    estado = EstadoInscripcion.valueOf(valores[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("La decisión enviada no es válida.");
                }
                if (id <= 0 || estado == EstadoInscripcion.INSCRIPTO || decisiones.put(id, estado) != null) {
                    throw new IllegalArgumentException("La decisión enviada no es válida.");
                }
            }
            request.setAttribute("decisiones", decisiones);
            InicializadorAplicacion.getServidor(getServletContext()).seleccionarEstudiantes(
                    docente.getNick(), valor(request, "edicion"), decisiones);
            response.sendRedirect(request.getContextPath() + "/seleccion-estudiantes?instituto="
                    + url(valor(request, "instituto")) + "&curso=" + url(valor(request, "curso"))
                    + "&edicion=" + url(valor(request, "edicion")) + "&orden="
                    + url(((OrdenInscripciones) request.getAttribute("orden")).name()) + "&guardado=1");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage()); mostrar(request, response);
        } catch (RuntimeException e) {
            getServletContext().log("No se pudo guardar la selección de estudiantes.", e);
            request.setAttribute("error", "No se pudo guardar la selección. Intentá nuevamente.");
            mostrar(request, response);
        }
    }

    private void preparar(HttpServletRequest request, DTAutenticacion docente) {
        var servidor = InicializadorAplicacion.getServidor(getServletContext());
        String instituto = valor(request, "instituto"), curso = valor(request, "curso"), edicion = valor(request, "edicion");
        String ordenTexto = valor(request, "orden");
        OrdenInscripciones orden;
        try { orden = ordenTexto.isEmpty() ? OrdenInscripciones.FECHA : OrdenInscripciones.valueOf(ordenTexto); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Seleccione un orden válido."); }
        var institutos = servidor.listarNombresInstitutos();
        if (!instituto.isEmpty() && !institutos.contains(instituto)) throw new IllegalArgumentException("El instituto no existe.");
        List<String> cursos = instituto.isEmpty() ? List.of() : servidor.listarNombresCursosPorInstituto(instituto);
        if (!curso.isEmpty() && !cursos.contains(curso)) throw new IllegalArgumentException("El curso no pertenece al instituto.");
        List<DTEdicionConsulta> ediciones = curso.isEmpty() ? List.of()
                : servidor.listarEdicionesParaSeleccion(docente.getNick(), instituto, curso);
        if (!edicion.isEmpty() && ediciones.stream().noneMatch(e -> e.nombre().equals(edicion))) {
            throw new SecurityException("Seleccione una edición vigente en la que participe.");
        }
        request.setAttribute("institutos", institutos); request.setAttribute("cursos", cursos);
        request.setAttribute("ediciones", ediciones); request.setAttribute("instituto", instituto);
        request.setAttribute("curso", curso); request.setAttribute("edicion", edicion);
        request.setAttribute("orden", orden);
        request.setAttribute("detalleEdicion", ediciones.stream().filter(e -> e.nombre().equals(edicion)).findFirst().orElse(null));
        request.setAttribute("ranking", edicion.isEmpty() ? List.of()
                : servidor.listarInscripcionesParaSeleccion(docente.getNick(), edicion, orden));
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }
    private static String valor(HttpServletRequest request, String nombre) {
        // Los formularios HTML envían CRLF; los nombres originales contienen LF.
        return FormulariosDocente.valor(request, nombre).replace("\r\n", "\n").replace('\r', '\n').trim();
    }
    private static String url(String valor) { return URLEncoder.encode(valor, StandardCharsets.UTF_8); }
}
