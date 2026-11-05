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
<<<<<<< HEAD
import web.soap.ClienteCatalogoSoap;
=======
import java.time.LocalDate;
import jakarta.xml.ws.WebServiceException;
import logica.EstadoInscripcion;
>>>>>>> be1a875fce9b23b64c7167af09cf9afcd3bccde2

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

<<<<<<< HEAD
        // 2. Obtener el acceso actual al Servidor Central.
        // Más adelante se adaptará a SOAP.
        ClienteCatalogoSoap servidor =
                InicializadorAplicacion.getClienteCatalogo(getServletContext());
=======
 
>>>>>>> be1a875fce9b23b64c7167af09cf9afcd3bccde2

       
        try {
            // Obtener todas las ediciones del docente.
           List<String> ediciones = consultarEdiciones(docente.getNick());

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
  
}

