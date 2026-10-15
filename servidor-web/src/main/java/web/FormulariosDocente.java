package web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import logica.DTAutenticacion;

/** Adaptación HTTP común; las reglas de altas y pertenencia están en el central. */
final class FormulariosDocente {
    private FormulariosDocente() { }

    static DTAutenticacion exigirDocente(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        Object identidad = sesion == null ? null : sesion.getAttribute("usuario");
        if (!(identidad instanceof DTAutenticacion usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (usuario.getRol() != DTAutenticacion.Rol.DOCENTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo los docentes pueden registrar cursos y ediciones.");
            return null;
        }
        return usuario;
    }

    static String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor;
    }

    static List<String> seleccion(HttpServletRequest request, String nombre) {
        String[] valores = request.getParameterValues(nombre);
        return valores == null ? List.of() : Arrays.asList(valores);
    }

    static int entero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un número entero válido para " + campo + ".");
        }
    }
}
