package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import logica.DTAutenticacion;
import logica.IServidorCentral;

@WebServlet(name = "AltaCursoServlet", urlPatterns = {"/alta-curso"})
public class AltaCursoServlet extends HttpServlet {
    private static final String VISTA = "/WEB-INF/vistas/altaCurso.jsp";
    private static final String[] CAMPOS = {
        "instituto", "nombre", "descripcion", "duracion", "horas", "creditos", "url"
    };

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
            int horas = FormulariosDocente.entero(FormulariosDocente.valor(request, "horas"), "horas");
            int creditos = FormulariosDocente.entero(FormulariosDocente.valor(request, "creditos"), "créditos");
            servidor.altaCursoDocente(docente.getNick(), FormulariosDocente.valor(request, "instituto"),
                    FormulariosDocente.valor(request, "nombre"), FormulariosDocente.valor(request, "descripcion"),
                    FormulariosDocente.valor(request, "duracion"), horas, creditos,
                    FormulariosDocente.valor(request, "url"), FormulariosDocente.seleccion(request, "previas"),
                    FormulariosDocente.seleccion(request, "categorias"));
            response.sendRedirect(request.getContextPath() + "/curso?nombre="
                    + java.net.URLEncoder.encode(FormulariosDocente.valor(request, "nombre").trim(),
                            java.nio.charset.StandardCharsets.UTF_8));
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response, docente);
        } catch (RuntimeException e) {
            getServletContext().log("No se pudo registrar el curso.", e);
            request.setAttribute("error", "No se pudo guardar el curso. Intentá nuevamente.");
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
            request.setAttribute("categorias", servidor.listarNombresCategorias());
            Map<String, List<String>> previasPorInstituto = new LinkedHashMap<>();
            for (String nombre : institutos) {
                previasPorInstituto.put(nombre, servidor.listarNombresCursosPorInstituto(nombre));
            }
            request.setAttribute("previasPorInstituto", previasPorInstituto);
            for (String campo : CAMPOS) request.setAttribute(campo, FormulariosDocente.valor(request, campo));
            request.setAttribute("instituto", instituto);
            request.setAttribute("categoriasSeleccionadas", FormulariosDocente.seleccion(request, "categorias"));
            request.setAttribute("previasSeleccionadas", FormulariosDocente.seleccion(request, "previas"));
            request.getRequestDispatcher(VISTA).forward(request, response);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
