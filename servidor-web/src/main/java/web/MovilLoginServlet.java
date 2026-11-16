package web;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;
import logica.DTAutenticacion;
import web.soap.ClienteCatalogoSoap;

@WebServlet(name = "MovilLoginServlet", urlPatterns = {"/movil", "/movil/login"})
public class MovilLoginServlet extends HttpServlet {

    private static final String COOKIE_RECORDAR = "edext_movil_recordar";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        DTAutenticacion actual = sesion == null
                ? null : (DTAutenticacion) sesion.getAttribute("usuario");

        if (actual != null && actual.getRol() == DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendRedirect(request.getContextPath() + "/movil/inicio");
            return;
        }

        String recordado = leerCookieRecordar(request);
        if (recordado != null && !recordado.isBlank()) {
            request.setAttribute("identificador", recordado);
            request.setAttribute("recordarme", Boolean.TRUE);
        }

        request.getRequestDispatcher("/WEB-INF/vistas/movil/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identificador = request.getParameter("identificador");
        String clave = request.getParameter("clave");
        boolean recordar = "on".equals(request.getParameter("recordarme"));

        try {
            ClienteCatalogoSoap servidor =
                    InicializadorAplicacion.getClienteCatalogo(getServletContext());
            var inicio = servidor.iniciarSesion(identificador, clave);

            if (inicio.usuario().getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
                request.setAttribute("error",
                        "El acceso desde dispositivo móvil es exclusivo para estudiantes.");
                request.setAttribute("identificador", identificador);
                request.setAttribute("recordarme", recordar);
                request.getRequestDispatcher("/WEB-INF/vistas/movil/login.jsp")
                        .forward(request, response);
                return;
            }

            actualizarCookieRecordar(request, response, identificador, recordar);

            HttpSession sesion = request.getSession();
            request.changeSessionId();
            sesion.setAttribute("usuario", inicio.usuario());
            sesion.setAttribute(LoginServlet.TOKEN_NOTAS, inicio.tokenNotas());

            response.sendRedirect(request.getContextPath() + "/movil/inicio");

        } catch (WebServiceException error) {
            getServletContext().log("No se pudo iniciar sesión móvil por SOAP.", error);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("error",
                    "El servidor central no está disponible. Intentá nuevamente más tarde.");
            request.setAttribute("identificador", identificador);
            request.setAttribute("recordarme", recordar);
            request.getRequestDispatcher("/WEB-INF/vistas/movil/login.jsp")
                    .forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("identificador", identificador);
            request.setAttribute("recordarme", recordar);
            request.getRequestDispatcher("/WEB-INF/vistas/movil/login.jsp")
                    .forward(request, response);
        }
    }

    private String leerCookieRecordar(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (COOKIE_RECORDAR.equals(c.getName())) {
                return URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void actualizarCookieRecordar(HttpServletRequest request,
            HttpServletResponse response, String identificador, boolean recordar) {
        String ruta = request.getContextPath() + "/movil";
        if (recordar && identificador != null && !identificador.isBlank()) {
            Cookie cookie = new Cookie(COOKIE_RECORDAR,
                    URLEncoder.encode(identificador.trim(), StandardCharsets.UTF_8));
            cookie.setPath(ruta);
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 días
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie(COOKIE_RECORDAR, "");
            cookie.setPath(ruta);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}