package web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import logica.IServidorCentral;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/registro"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class RegistroServlet extends HttpServlet {
    private static final String VISTA = "/WEB-INF/vistas/registro.jsp";
    private static final String[] CAMPOS = {
        "tipo", "nick", "correo", "nombre", "apellido", "fechaNacimiento", "instituto"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (redirigirAutenticado(request, response)) return;
        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (redirigirAutenticado(request, response)) return;

        Part archivo;
        try {
            // Procesar primero: el contenedor comprueba el tamaño de la carga completa.
            archivo = request.getPart("imagen");
        } catch (IllegalStateException e) {
            request.setAttribute("error", "La imagen no puede superar los 5 MB. Volvé a completar el formulario.");
            mostrarFormulario(request, response);
            return;
        } catch (ServletException e) {
            request.setAttribute("error", "No se pudo recibir el formulario. Volvé a enviarlo.");
            mostrarFormulario(request, response);
            return;
        }

        // Se conservan los datos normales; nunca las contraseñas ni el archivo recibido.
        for (String campo : CAMPOS) request.setAttribute(campo, request.getParameter(campo));
        try {
            String tipo = request.getParameter("tipo");
            if (!"estudiante".equals(tipo) && !"docente".equals(tipo)) {
                throw new IllegalArgumentException("Elegí estudiante o docente.");
            }
            String fecha = request.getParameter("fechaNacimiento");
            if (fecha == null || fecha.isBlank()) {
                throw new IllegalArgumentException("Ingresá la fecha de nacimiento.");
            }
            LocalDate nacimiento;
            try {
                nacimiento = LocalDate.parse(fecha);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
            }
            byte[] imagen = null;
            if (archivo != null && archivo.getSize() > 0) {
                try (var entrada = archivo.getInputStream()) {
                    imagen = entrada.readAllBytes();
                }
            }
            IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
            String nick = request.getParameter("nick");
            String correo = request.getParameter("correo");
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String clave = request.getParameter("clave");
            String confirmacion = request.getParameter("confirmacion");
            if ("docente".equals(tipo)) {
                servidor.registrarDocente(nick, correo, nombre, apellido, nacimiento,
                        request.getParameter("instituto"), clave, confirmacion, imagen);
            } else {
                servidor.registrarEstudiante(nick, correo, nombre, apellido, nacimiento,
                        clave, confirmacion, imagen);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response);
            return;
        } catch (IllegalStateException e) {
            getServletContext().log("No se pudo registrar un usuario.", e);
            request.setAttribute("error", "No se pudo guardar la cuenta. Intentá nuevamente.");
            mostrarFormulario(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login?registro=correcto");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        request.setAttribute("institutos", servidor.listarNombresInstitutos());
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    private boolean redirigirAutenticado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return true;
        }
        return false;
    }
}
