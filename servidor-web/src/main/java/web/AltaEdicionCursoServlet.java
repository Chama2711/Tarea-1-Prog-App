package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import logica.DTAutenticacion;
import logica.IServidorCentral;

@WebServlet(name = "AltaEdicionCursoServlet", urlPatterns = {"/alta-edicion-curso"})
public class AltaEdicionCursoServlet extends HttpServlet {
    private static final String VISTA = "/WEB-INF/vistas/altaEdicionCurso.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DTAutenticacion docente = FormulariosDocente.exigirDocente(request, response);
        if (docente != null) mostrarFormulario(request, response, docente);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DTAutenticacion docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) return;
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        try {
            LocalDate inicio = LocalDate.parse(FormulariosDocente.valor(request, "fechaInicio"));
            LocalDate fin = LocalDate.parse(FormulariosDocente.valor(request, "fechaFin"));
            String cupos = FormulariosDocente.valor(request, "cupos");
            int cupo = cupos.isBlank() ? -1 : FormulariosDocente.entero(cupos, "cupo");
            servidor.altaEdicionCursoDocente(docente.getNick(), FormulariosDocente.valor(request, "instituto"),
                    FormulariosDocente.valor(request, "curso"), FormulariosDocente.valor(request, "nombre"),
                    inicio, fin, cupo, FormulariosDocente.seleccion(request, "docentes"));
            response.sendRedirect(request.getContextPath() + "/edicion?nombre="
                    + java.net.URLEncoder.encode(FormulariosDocente.valor(request, "nombre").trim(),
                            java.nio.charset.StandardCharsets.UTF_8));
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Ingrese fechas válidas de inicio y finalización.");
            mostrarFormulario(request, response, docente);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response, docente);
        } catch (RuntimeException e) {
            getServletContext().log("No se pudo registrar la edición.", e);
            request.setAttribute("error", "No se pudo guardar la edición. Intentá nuevamente.");
            mostrarFormulario(request, response, docente);
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
            DTAutenticacion docente) throws ServletException, IOException {
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        try {
            List<String> institutos = servidor.listarNombresInstitutosDocente(docente.getNick());
            String instituto = FormulariosDocente.valor(request, "instituto").trim();
            if (instituto.isEmpty() && institutos.size() == 1) instituto = institutos.get(0);
            if (!instituto.isEmpty() && !institutos.contains(instituto)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Seleccione uno de sus institutos.");
                return;
            }
            request.setAttribute("institutos", institutos);
            request.setAttribute("instituto", instituto);
            request.setAttribute("cursos", instituto.isEmpty() ? List.of()
                    : servidor.listarNombresCursosPorInstituto(instituto));
            request.setAttribute("docentes", instituto.isEmpty() ? List.of()
                    : servidor.listarNicknamesDocentesPorInstituto(instituto));
            for (String campo : List.of("curso", "nombre", "fechaInicio", "fechaFin", "cupos")) {
                request.setAttribute(campo, FormulariosDocente.valor(request, campo));
            }
            request.setAttribute("docentesSeleccionados", FormulariosDocente.seleccion(request, "docentes"));
            request.getRequestDispatcher(VISTA).forward(request, response);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
