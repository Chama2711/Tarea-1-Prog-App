package web;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.DTAutenticacion;
import logica.DTEdicionConsulta;
import logica.DTInscripcion;
import logica.DTPerfilUsuario;
import logica.IServidorCentral;

/**
 * @author Chama
 */
@WebServlet(name = "ListarAceptadosServlet", urlPatterns = {"/listar-aceptados"})
public class ListarAceptadosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        DTAutenticacion docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) return;

        try {
            prepararConsulta(request, docente);
        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());
            return;
        } catch (IllegalArgumentException error) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error.getMessage());
            return;
        } catch (RuntimeException error) {
            getServletContext().log("No se pudo consultar el listado de aceptados.", error);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo cargar el listado. Intentá nuevamente.");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/vistas/listar-aceptados.jsp")
                .forward(request, response);
    }

    private void prepararConsulta(HttpServletRequest request, DTAutenticacion docente) {
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        String instituto = FormulariosDocente.valor(request, "instituto").trim();
        String curso = FormulariosDocente.valor(request, "curso").trim();
        String edicion = FormulariosDocente.valor(request, "edicion").trim();

        List<String> institutos = servidor.listarNombresInstitutosDocente(docente.getNick());
        if (!instituto.isEmpty() && !institutos.contains(instituto)) {
            throw new SecurityException("Seleccione uno de sus institutos.");
        }
        List<String> cursos = instituto.isEmpty() ? List.of()
                : servidor.listarNombresCursosPorInstituto(instituto);
        if (!curso.isEmpty() && !cursos.contains(curso)) {
            throw new IllegalArgumentException("El curso no pertenece al instituto seleccionado.");
        }

        List<DTEdicionConsulta> ediciones = List.of();
        if (!curso.isEmpty()) {
            DTPerfilUsuario perfil = servidor.consultarPerfilUsuario(docente.getNick(), docente.getNick());
            if (perfil == null || perfil.usuario().rol() != DTAutenticacion.Rol.DOCENTE) {
                throw new SecurityException("El docente ya no está disponible.");
            }
            // El central entrega las ediciones del docente, incluidas las finalizadas.
            ediciones = perfil.ediciones().stream()
                    .filter(item -> curso.equals(item.nombreCurso())).toList();
        }

        DTEdicionConsulta elegida = ediciones.stream()
                .filter(item -> edicion.equals(item.nombre())).findFirst().orElse(null);
        if (!edicion.isEmpty() && elegida == null) {
            throw new SecurityException("Seleccione una edición del curso en la que participe.");
        }
        List<DTInscripcion> aceptados = elegida == null ? List.of()
                : servidor.listarAceptadosEdicion(docente.getNick(), elegida.nombre());

        request.setAttribute("institutos", institutos);
        request.setAttribute("cursos", cursos);
        request.setAttribute("ediciones", ediciones);
        request.setAttribute("institutoSeleccionado", instituto);
        request.setAttribute("cursoSeleccionado", curso);
        request.setAttribute("edicionSeleccionada", edicion);
        request.setAttribute("edicionElegida", elegida);
        request.setAttribute("aceptados", aceptados);
    }
}
