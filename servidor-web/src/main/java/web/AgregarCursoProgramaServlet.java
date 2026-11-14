/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

/**
 *
 * @author elizeth
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import web.soap.ClienteCatalogoSoap;

public class AgregarCursoProgramaServlet extends HttpServlet {

    @Override
    protected void doGet(
             HttpServletRequest request,
             HttpServletResponse response)
             throws ServletException, IOException {

        var docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) {
             return;
        }

        try {
        ClienteCatalogoSoap servidor =
                InicializadorAplicacion.getClienteCatalogo(getServletContext());

        List<String> programas = servidor.listarNombresProgramas();
        List<String> cursos = servidor.listarNombresCursos();

        String programaSeleccionado =
                 FormulariosDocente.valor(request, "programa");

        request.setAttribute("programas", programas);
        request.setAttribute("cursos", cursos);
        request.setAttribute("programaSeleccionado", programaSeleccionado);

        request.getRequestDispatcher("/WEB-INF/vistas/agregarCursoPrograma.jsp").forward(request, response);
        } catch (jakarta.xml.ws.WebServiceException error) {
            getServletContext().log("No se pudieron consultar los cursos y programas por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudieron cargar los cursos y programas. Intentá nuevamente más tarde.");
        }
    }
    
    @Override
        protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

        // 1. Verificar que sea docente
        var docente = FormulariosDocente.exigirDocente(request, response);
        if (docente == null) {
            return;
        }

        // 2. Recuperar los datos del formulario
        String nombrePrograma =
                FormulariosDocente.valor(request, "programa").trim();

        String nombreCurso =
                FormulariosDocente.valor(request, "curso").trim();

        // 3. Validar campos obligatorios
        if (nombrePrograma.isEmpty() || nombreCurso.isEmpty()) {
            request.setAttribute("error",
                    "Debés seleccionar un programa y un curso.");
            doGet(request, response);
            return;
        }

        // 4. Obtener el servidor central
        ClienteCatalogoSoap servidor =
                InicializadorAplicacion.getClienteCatalogo(getServletContext());

        try {
            // 5. Ejecutar la operación
            servidor.agregarCursoAPrograma(docente.getNick(), nombrePrograma, nombreCurso);

            // 6. Volver al detalle del programa
            response.sendRedirect(
                    request.getContextPath()
                    + "/programa?nombre="
                    + java.net.URLEncoder.encode(
                            nombrePrograma,
                            java.nio.charset.StandardCharsets.UTF_8)
                    + "&cursoAgregado=ok"
            );

        } catch (jakarta.xml.ws.WebServiceException error) {
            getServletContext().log("No se pudo agregar el curso al programa por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo confirmar el cambio. Revisá el programa antes de volver a enviarlo.");
        } catch (SecurityException error) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error.getMessage());
        } catch (Exception e) {
        request.setAttribute("error",
                "No se pudo agregar el curso: " + e.getMessage());

        doGet(request, response);
       }
    }
    
    
    
    
}
