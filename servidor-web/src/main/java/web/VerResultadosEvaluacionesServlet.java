/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import logica.DTAutenticacion;
import logica.DTResultadoEvaluacion;

@WebServlet("/ver-resultados-evaluaciones")
public class VerResultadosEvaluacionesServlet
        extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/vistas/verResultadosEvaluaciones.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Exigir sesión de docente.
        DTAutenticacion docente =
                FormulariosDocente.exigirDocente(
                        request, response
                );

        if (docente == null) {
            return;
        }

        response.setHeader("Cache-Control", "no-store");

        try {
            // 2. Consultar las ediciones del docente.
            List<String> ediciones =
                    consultarEdiciones(docente.getNick());

            String nombreEdicion =
                    FormulariosDocente.valor(
                            request, "edicion"
                    ).trim();

            List<DTResultadoEvaluacion> resultados =
                    List.of();
            String avisoResultados = null;

            if (!nombreEdicion.isEmpty() && !ediciones.contains(nombreEdicion)) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "No participás en la edición seleccionada."
                );
                return;
            }

            nombreEdicion = prepararSeleccion(request, docente.getNick(), ediciones, nombreEdicion);
            if (!nombreEdicion.isEmpty()) {
                try {
                    resultados = consultarResultados(
                            docente.getNick(),
                            nombreEdicion
                    );

                } catch (SOAPFaultException e) {

                    String motivo = e.getFault() == null
                            ? ""
                            : e.getFault().getFaultString();

                    // Trata la edición abierta como una advertencia
                    // y no como un fallo de toda la página.
                    if (motivo != null
                            && motivo.contains("todavía está abierta")) {

                        avisoResultados =
                                "Esta edición todavía está abierta. "
                                + "Los resultados definitivos estarán disponibles "
                                + "cuando se registren todas las calificaciones "
                                + "y se cierre la edición.";

                    } else {
                        // Los demás errores SOAP siguen su tratamiento normal.
                        throw e;
                    }
                }
            }

            // 4. Preparar los datos para la JSP.
            request.setAttribute("ediciones", ediciones);
            request.setAttribute("edicion", nombreEdicion);
            request.setAttribute("resultados", resultados);
            request.setAttribute("avisoResultados", avisoResultados);

            // 5. Mostrar la pantalla.
            request.getRequestDispatcher(VISTA)
                    .forward(request, response);

        } catch (SecurityException e) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tenés permisos para consultar los resultados."
            );

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SOAPFaultException e) {
            getServletContext().log(
                    "SOAP rechazó la consulta de resultados.", e
            );

            response.sendError(
                    HttpServletResponse.SC_CONFLICT,
                    "No se pudieron consultar los resultados. "
                    + "Comprobá que la edición esté cerrada."
            );

        } catch (WebServiceException e) {
            getServletContext().log(
                    "Error de comunicación con evaluaciones.", e
            );

            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "El servicio de evaluaciones no está disponible."
            );

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error inesperado al consultar resultados.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudieron consultar los resultados."
            );
        }
    }

    protected String prepararSeleccion(HttpServletRequest request, String nickDocente,
            List<String> ediciones, String edicion) {
        return SeleccionEvaluaciones.preparar(request, getServletContext(), nickDocente,
                ediciones, edicion, true);
    }

    // Obtener las ediciones autorizadas mediante SOAP.
    protected List<String> consultarEdiciones(
            String nickDocente) {

        return InicializadorAplicacion
                .getClienteEvaluaciones(getServletContext())
                .listarEdicionesDocente(nickDocente);
    }

    // Recuperar los resultados desde SOAP y convertirlos
    // al DTO compartido utilizado por nuestra JSP.
    protected List<DTResultadoEvaluacion> consultarResultados(
            String nickDocente,
            String nombreEdicion) {

        var resultadosSoap = InicializadorAplicacion
                .getClienteEvaluaciones(getServletContext())
                .consultarResultadosEdicion(
                        nickDocente,
                        nombreEdicion
                );

        return resultadosSoap.stream()
                .map(dto -> new DTResultadoEvaluacion(
                        dto.getIdInscripcion(),
                        dto.getNicknameEstudiante(),
                        dto.getNombreEstudiante(),
                        dto.getCalificacion(),
                        dto.getFechaCalificacion() == null
                                || dto.getFechaCalificacion().isBlank()
                                ? null
                                : LocalDate.parse(
                                        dto.getFechaCalificacion()
                                )
                ))
                .toList();
    }
}
