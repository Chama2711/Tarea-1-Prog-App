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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import logica.DTAutenticacion;
import logica.Estudiante;
import logica.IServidorCentral;
import logica.ProgramaFormacion;
import logica.Usuario;


public class InscripcionProgramaServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        DTAutenticacion autenticado =
                (DTAutenticacion) sesion.getAttribute("usuario");

        if (autenticado.getRol()
                != DTAutenticacion.Rol.ESTUDIANTE) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo los estudiantes pueden inscribirse a un programa."
            );
            return;
        }
        
        String nombrePrograma = request.getParameter("programa");

        if (nombrePrograma == null || nombrePrograma.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Debe seleccionar un programa de formación."
            );
            return;
        }   
        
        IServidorCentral servidor =
        InicializadorAplicacion.getServidor(getServletContext());
        
        try {

                Usuario usuario = servidor.obtenerUsuario(
                        autenticado.getNick()
                );

                if (!(usuario instanceof Estudiante)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "El usuario autenticado no es un estudiante."
                    );
                    return;
                }

                Estudiante estudiante = (Estudiante) usuario;

                ProgramaFormacion programa =
                        servidor.obtenerDetallePrograma(nombrePrograma);

                if (programa == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "El programa solicitado no existe."
                    );
                    return;
                }

                servidor.inscriboAProgramaFormacion(
                        estudiante,
                        programa,
                        LocalDate.now()
                );
                
                String programaCodificado = URLEncoder.encode(
                        nombrePrograma,
                        StandardCharsets.UTF_8
                );

                response.sendRedirect(
                        request.getContextPath()
                        + "/programa?nombre="
                        + programaCodificado
                        + "&inscripcion=ok"
                );

              } catch (IllegalArgumentException | IllegalStateException e) {

                String mensaje = URLEncoder.encode(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "No se pudo realizar la inscripción.",
                        StandardCharsets.UTF_8
                );

                String programaCodificado = URLEncoder.encode(
                        nombrePrograma,
                        StandardCharsets.UTF_8
                );

                response.sendRedirect(
                        request.getContextPath()
                        + "/programa?nombre="
                        + programaCodificado
                        + "&errorInscripcion="
                        + mensaje
                );
            }

        
    }
}
