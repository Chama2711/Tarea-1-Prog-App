package web;

import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import logica.DTAutenticacion;
import web.soap.ClienteCatalogoSoap;

@WebServlet(name = "AltaCursoServlet", urlPatterns = {"/alta-curso"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
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
        request.setCharacterEncoding("UTF-8");
        byte[] imagen;
        try {
            imagen = ImagenFormulario.leer(request);
        } catch (IllegalStateException e) {
            response.sendError(413, "El archivo supera los 5 MB o el formulario supera los 6 MB. Volvé a elegir una imagen más pequeña.");
            return;
        } catch (ServletException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se pudo recibir la imagen. Volvé a enviar el formulario.");
            return;
        }
        ClienteCatalogoSoap servidor = InicializadorAplicacion.getClienteCatalogo(getServletContext());
        try {
            int horas = FormulariosDocente.entero(FormulariosDocente.valor(request, "horas"), "horas");
            int creditos = FormulariosDocente.entero(FormulariosDocente.valor(request, "creditos"), "créditos");
            servidor.altaCursoDocente(docente.getNick(), FormulariosDocente.valor(request, "instituto"),
                    FormulariosDocente.valor(request, "nombre"), FormulariosDocente.valor(request, "descripcion"),
                    FormulariosDocente.valor(request, "duracion"), horas, creditos,
                    FormulariosDocente.valor(request, "url"), FormulariosDocente.seleccion(request, "previas"),
                    FormulariosDocente.seleccion(request, "categorias"), imagen);
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
        ClienteCatalogoSoap servidor = InicializadorAplicacion.getClienteCatalogo(getServletContext());
        try {
            List<String> institutos = InicializadorAplicacion.getClienteCatalogo(getServletContext())
                    .listarInstitutosDocente(docente.getNick());
            String instituto = FormulariosDocente.valor(request, "instituto").trim();
            if (instituto.isEmpty() && institutos.size() == 1) instituto = institutos.get(0);
            if (!instituto.isEmpty() && !institutos.contains(instituto)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Seleccione uno de sus institutos.");
                return;
            }
            request.setAttribute("institutos", institutos);
            request.setAttribute("categorias", InicializadorAplicacion.getClienteCatalogo(getServletContext()).listarCategorias());
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
        } catch (WebServiceException e) {
            getServletContext().log("No se pudieron consultar los institutos o las categorías por SOAP.", e);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los institutos o las categorías. Intentá nuevamente más tarde.");
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
