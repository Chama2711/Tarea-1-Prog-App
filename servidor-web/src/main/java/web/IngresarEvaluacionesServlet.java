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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import logica.DTAutenticacion;
import logica.DTInscripcion;
import logica.IServidorCentral;

@WebServlet("/ingresar-evaluaciones")
public class IngresarEvaluacionesServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/vistas/ingresarEvaluaciones.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Verificar que el usuario sea docente.
        DTAutenticacion docente =
                FormulariosDocente.exigirDocente(request, response);

        if (docente == null) {
            return;
        }

        response.setHeader("Cache-Control", "no-store");

        // 2. Obtener el acceso actual al Servidor Central.
        // Más adelante se adaptará a SOAP.
        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        try {
            // 3. Obtener todas las ediciones del docente.
            List<String> ediciones =
                    servidor.listarEdicionesDocente(docente.getNick());

            // 4. Leer la edición seleccionada.
            String nombreEdicion =
                    FormulariosDocente.valor(request, "edicion").trim();

            List<DTInscripcion> aceptados = List.of();

            // 5. Consultar los estudiantes aceptados.
            if (!nombreEdicion.isEmpty()) {

                if (!ediciones.contains(nombreEdicion)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "No participás en la edición seleccionada."
                    );
                    return;
                }

                aceptados = servidor.listarAceptadosEdicion(
                        docente.getNick(),
                        nombreEdicion
                );
            }

            // 6. Enviar los datos a la JSP.
            request.setAttribute("ediciones", ediciones);
            request.setAttribute("edicion", nombreEdicion);
            request.setAttribute("aceptados", aceptados);

            request.getRequestDispatcher(VISTA)
                    .forward(request, response);

        } catch (SecurityException e) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al consultar las evaluaciones.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudieron consultar las evaluaciones."
            );
        }
    }
}

