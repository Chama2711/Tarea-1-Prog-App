package web;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import logica.DTAutenticacion;

@WebFilter(urlPatterns = "/movil/*")
public class MovilAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ruta = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String rutaCompleta = pathInfo == null ? ruta : ruta + pathInfo;

        if ("/movil".equals(rutaCompleta) || "/movil/".equals(rutaCompleta)
                || "/movil/login".equals(rutaCompleta)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(false);
        DTAutenticacion usuario = sesion == null
                ? null : (DTAutenticacion) sesion.getAttribute("usuario");

        if (usuario == null || usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            res.sendRedirect(req.getContextPath() + "/movil/login");
            return;
        }

        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(request, response);
    }
}