package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.xml.ws.WebServiceException;
import logica.DTAutenticacion;

@WebServlet(name = "DetalleProgramaServlet", urlPatterns = {"/programa"})
public class DetalleProgramaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/programas");
            return;
        }
        try {
            var cliente = InicializadorAplicacion.getClienteCatalogo(getServletContext());
            var programa = cliente.consultarPrograma(nombre);
            if (programa == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El programa solicitado no existe.");
                return;
            }
            var sesion = request.getSession(false);
            DTAutenticacion autenticado = sesion == null ? null : (DTAutenticacion) sesion.getAttribute("usuario");
            boolean yaInscripto = autenticado != null
                    && autenticado.getRol() == DTAutenticacion.Rol.ESTUDIANTE
                    && cliente.estaInscriptoAPrograma(autenticado.getNick(), programa.getId());
            request.setAttribute("programa", programa);
            request.setAttribute("categorias", programa.getCategoria());
            request.setAttribute("yaInscripto", yaInscripto);
            request.getRequestDispatcher("/WEB-INF/vistas/detallePrograma.jsp").forward(request, response);
        } catch (WebServiceException error) {
            getServletContext().log("No se pudo consultar el programa por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo cargar el programa. Intentá nuevamente más tarde.");
        }
    }
}
