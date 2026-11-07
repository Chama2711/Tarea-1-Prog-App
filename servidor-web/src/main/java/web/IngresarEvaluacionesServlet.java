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
import java.time.LocalDate;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import logica.EstadoInscripcion;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@WebServlet("/ingresar-evaluaciones")
public class IngresarEvaluacionesServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/vistas/ingresarEvaluaciones.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que el usuario sea docente.
        DTAutenticacion docente =
                FormulariosDocente.exigirDocente(request, response);

        if (docente == null) {
            return;
        }

        response.setHeader("Cache-Control", "no-store");


        try {
            // Obtener todas las ediciones del docente mediante SOAP.
            List<String> ediciones =
                    consultarEdiciones(docente.getNick());


            //  Leer la edición seleccionada.
            String nombreEdicion =  FormulariosDocente.valor(request, "edicion").trim();

            List<DTInscripcion> aceptados = List.of();

            //  Consultar los estudiantes aceptados.
            if (!nombreEdicion.isEmpty()) {

                if (!ediciones.contains(nombreEdicion)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "No participás en la edición seleccionada."
                    );
                    return;
                }

                aceptados = consultarAceptados(docente.getNick(),nombreEdicion);
            }

            //  Enviar los datos a la JSP.
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
            
        } catch (WebServiceException e) {
            getServletContext().log(
                    "No se pudo consultar evaluaciones por SOAP.", e
            );

            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "El servicio de evaluaciones no está disponible."
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
    
    
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. Verificar que el usuario sea docente.
        DTAutenticacion docente =
                FormulariosDocente.exigirDocente(request, response);

        if (docente == null) {
            return;
        }

        response.setHeader("Cache-Control", "no-store");


        response.setHeader("Cache-Control", "no-store");

        // Identificar si el docente quiere cerrar la edición.
        if ("cerrar".equals(request.getParameter("accion"))) {
            procesarCierre(
                    request,
                    response,
                    docente.getNick()
            );
            return;
        }

        // 2. Leer los datos enviados por la JSP.
        String edicionParametro = request.getParameter("edicion");
        String idParametro = request.getParameter("idInscripcion");
        String notaParametro = request.getParameter("nota");

        if (edicionParametro == null || edicionParametro.isBlank()
                || idParametro == null || notaParametro == null) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Faltan datos para registrar la calificación."
            );
            return;
        }

        String nombreEdicion = edicionParametro.trim();

        try {
            // 3. Validar el ID y la nota.
            Long idInscripcion = Long.valueOf(idParametro.trim());
            int nota = Integer.parseInt(notaParametro.trim());

            if (idInscripcion <= 0 || nota < 1 || nota > 12) {
                throw new IllegalArgumentException(
                        "La inscripción o la calificación es inválida."
                );
            }

            // 4. Verificar que la edición pertenezca al docente.
            List<String> ediciones =
                    consultarEdiciones(docente.getNick());

            if (!ediciones.contains(nombreEdicion)) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "No participás en la edición seleccionada."
                );
                return;
            }

            // 5. Verificar que la inscripción esté aceptada.
            List<DTInscripcion> aceptados =
                    consultarAceptados(docente.getNick(), nombreEdicion);

            boolean inscripcionValida = aceptados.stream()
                    .anyMatch(i -> Objects.equals(
                            i.getId(), idInscripcion
                    ));

            if (!inscripcionValida) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "La inscripción no pertenece a los estudiantes aceptados."
                );
                return;
            }

            // 6. Registrar la nota mediante SOAP.
            registrarCalificacion(
                    docente.getNick(),
                    nombreEdicion,
                    idInscripcion,
                    nota
            );

            // 7. Volver a la edición seleccionada.
            String destino = request.getContextPath()
                    + "/ingresar-evaluaciones?edicion="
                    + URLEncoder.encode(
                            nombreEdicion,
                            StandardCharsets.UTF_8
                    )
                    + "&guardada=1";

            response.sendRedirect(
                    response.encodeRedirectURL(destino)
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Los datos de la calificación no son válidos."
            );

        } catch (WebServiceException e) {

            getServletContext().log(
                    "Error al registrar la calificación por SOAP.", e
            );

            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo registrar la calificación."
            );

        } catch (RuntimeException e) {

            getServletContext().log(
                    "Error inesperado al registrar la calificación.", e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ocurrió un error al registrar la calificación."
            );
        }
    }

    
        private void procesarCierre(
                HttpServletRequest request,
                HttpServletResponse response,
                String nickDocente) throws IOException {

            String parametro = request.getParameter("edicion");

            if (parametro == null || parametro.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Seleccione una edición para cerrar."
                );
                return;
            }

            String nombreEdicion = parametro.trim();

            try {
                // Verificar que la edición corresponde al docente.
                List<String> ediciones =
                        consultarEdiciones(nickDocente);

                if (!ediciones.contains(nombreEdicion)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "No participás en la edición seleccionada."
                    );
                    return;
                }

                // Solicitar el cierre al Servidor Central mediante SOAP.
                cerrarEdicion(nickDocente, nombreEdicion);

                // Redirigir para evitar repetir el POST al actualizar.
                String destino = request.getContextPath()
                        + "/ingresar-evaluaciones?edicion="
                        + URLEncoder.encode(
                                nombreEdicion,
                                StandardCharsets.UTF_8
                        )
                        + "&cerrada=1";

                response.sendRedirect(
                        response.encodeRedirectURL(destino)
                );

            } catch (SOAPFaultException e) {
                getServletContext().log(
                        "El servicio SOAP rechazó el cierre.", e
                );

                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "No se pudo cerrar la edición. "
                        + "Comprobá que todos los estudiantes "
                        + "aceptados tengan calificación y "
                        + "que la edición no esté cerrada."
                );

            } catch (WebServiceException e) {
                getServletContext().log(
                        "Error de comunicación al cerrar la edición.", e
                );

                response.sendError(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "El servicio de evaluaciones no está disponible."
                );

            } catch (IllegalArgumentException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "La edición seleccionada no es válida."
                );

            } catch (RuntimeException e) {
                getServletContext().log(
                        "Error inesperado al cerrar la edición.", e
                );

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo completar el cierre."
                );
            }
        }

        // Facilita probar la llamada sin conectarse a SOAP.
        protected void cerrarEdicion(
                String nickDocente,
                String nombreEdicion) {

            InicializadorAplicacion
                    .getClienteEvaluaciones(getServletContext())
                    .cerrarEdicion(nickDocente, nombreEdicion);
        }

    
    
    
    protected List<String> consultarEdiciones(String nickDocente) {
        return InicializadorAplicacion
                .getClienteEvaluaciones(getServletContext())
                .listarEdicionesDocente(nickDocente);
    }

    protected List<DTInscripcion> consultarAceptados(
            String nickDocente,
            String nombreEdicion) {

        var inscripcionesSoap = InicializadorAplicacion
                .getClienteEvaluaciones(getServletContext())
                .listarAceptadosEdicion(
                        nickDocente,
                        nombreEdicion
                );

        return inscripcionesSoap.stream()
                .map(dto -> new DTInscripcion(
                        dto.getId(),
                        dto.getNicknameEstudiante(),
                        dto.getNombreEstudiante(),
                        dto.getNombreEdicion(),
                        dto.getNombreCurso(),
                        dto.getFechaInscripcion() == null
                                || dto.getFechaInscripcion().isBlank()
                                ? null
                                : LocalDate.parse(
                                        dto.getFechaInscripcion()
                                ),
                        dto.getEstado() == null
                                || dto.getEstado().isBlank()
                                ? null
                                : EstadoInscripcion.valueOf(
                                        dto.getEstado()
                                )
                ))
                .toList();
    }
  
    
    protected void registrarCalificacion(
            String nickDocente,
            String nombreEdicion,
            Long idInscripcion,
            int nota) {

        InicializadorAplicacion
                .getClienteEvaluaciones(getServletContext())
                .registrarCalificacion(
                        nickDocente,
                        nombreEdicion,
                        idInscripcion,
                        nota
                );
    }

}

