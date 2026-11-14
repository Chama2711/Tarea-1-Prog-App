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
                    "Esta acción requiere una sesión de docente.");
            return null;
        }
        return usuario;
    }

    static String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return normalizarSaltos(valor);
    }

    static List<String> seleccion(HttpServletRequest request, String nombre) {
        String[] valores = request.getParameterValues(nombre);
        return valores == null ? List.of() : Arrays.stream(valores)
                .map(FormulariosDocente::normalizarSaltos).toList();
    }

    // HTML envía CRLF; los nombres originales de cursos se almacenan con LF.
    static String normalizarSaltos(String valor) {
        return valor == null ? "" : valor.replace("\r\n", "\n").replace('\r', '\n');
    }

    static int entero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un número entero válido para " + campo + ".");
        }
    }
}
