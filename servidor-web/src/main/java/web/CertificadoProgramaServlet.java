package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import logica.DTAutenticacion;
import logica.RecursoNoEncontradoException;

@WebServlet("/certificado-programa")
public class CertificadoProgramaServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        var sesion = request.getSession(false);
        Object identidad = sesion == null ? null : sesion.getAttribute("usuario");
        if (!(identidad instanceof DTAutenticacion usuario)) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }
        if (usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendError(403, "Solo los estudiantes pueden tramitar certificados."); return;
        }
        String programa = FormulariosDocente.valor(request, "programa").trim();
        try {
            var cliente = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            if ("1".equals(request.getParameter("descargar"))) {
                byte[] pdf = cliente.generarCertificadoPrograma(usuario.getNick(), programa);
                if (pdf == null || pdf.length == 0) throw new WebServiceException("El central no devolvió el PDF.");
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"certificado-programa.pdf\"");
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setContentLength(pdf.length);
                response.getOutputStream().write(pdf);
                return;
            }
            mostrarFormulario(request, response, cliente, programa);
        } catch (RecursoNoEncontradoException error) {
            response.sendError(404, error.getMessage());
        } catch (IllegalArgumentException error) {
            response.setStatus(400);
            request.setAttribute("errorCertificado", error.getMessage());
            try {
                mostrarFormulario(request, response,
                        InicializadorAplicacion.getClienteCatalogo(getServletContext()), programa);
            } catch (WebServiceException indisponible) {
                response.sendError(503, "No se pudo consultar el central. Intentá nuevamente más tarde.");
            }
        } catch (SecurityException error) { response.sendError(403, error.getMessage());
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo generar el certificado por SOAP.", error);
            response.sendError(503, "No se pudo generar el certificado. Intentá nuevamente más tarde.");
        }
    }
    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
            web.soap.ClienteCatalogoSoap cliente, String programa) throws ServletException, IOException {
        request.setAttribute("programasCertificado", cliente.listarNombresProgramas());
        request.setAttribute("programaSeleccionado", programa);
        request.getRequestDispatcher("/WEB-INF/vistas/certificadoPrograma.jsp").forward(request, response);
    }
}
