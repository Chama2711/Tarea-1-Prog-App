package web;

import java.io.IOException;
import java.util.List;
import jakarta.xml.ws.WebServiceException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

/** Datos compartidos de navegación para las pantallas, sin consultas desde las JSP. */
@WebFilter(urlPatterns = "/*", dispatcherTypes = DispatcherType.REQUEST)
public class NavegacionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ruta = ((HttpServletRequest) request).getServletPath();
        if (!ruta.startsWith("/css/") && !ruta.startsWith("/js/")
                && !ruta.startsWith("/imagenes/") && !ruta.equals("/imagen-usuario")) {
            var servidor = InicializadorAplicacion.getServidor(request.getServletContext());
            var clienteInstitutos = InicializadorAplicacion.getClienteInstitutos(request.getServletContext());
            try {
                request.setAttribute("navInstitutos", clienteInstitutos.listarInstitutos());
            } catch (WebServiceException error) {
                request.getServletContext().log("No se pudieron consultar los institutos por SOAP.", error);
                request.setAttribute("navInstitutos", List.of());
                request.setAttribute("errorInstitutos",
                        "No se pudieron cargar los institutos. Intentá nuevamente más tarde.");
            }
            request.setAttribute("navCategorias", servidor.listarCategorias());
        }
        chain.doFilter(request, response);
    }
}
