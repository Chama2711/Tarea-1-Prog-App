/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

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
import logica.EdicionCurso;
import logica.Estudiante;
import logica.IServidorCentral;
import logica.Usuario;


public class InscripcionEdicionServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        

        HttpSession sesion = request.getSession(false);

        // Debe existir una sesión iniciada
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        DTAutenticacion autenticado =
                (DTAutenticacion) sesion.getAttribute("usuario");

        // Solo los estudiantes pueden inscribirse
        if (autenticado.getRol()
                != DTAutenticacion.Rol.ESTUDIANTE) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo los estudiantes pueden inscribirse a una edición."
            );
            return;
        }

       String nombreEdicion = request.getParameter("edicion");

        if (nombreEdicion != null) {
            nombreEdicion = nombreEdicion.replace("\r\n", "\n");
        }
        


        if (nombreEdicion == null
                || nombreEdicion.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Debe seleccionar una edición."
            );
            return;
        }

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(
                        getServletContext()
                );

        String edicionCodificada =
                URLEncoder.encode(
                        nombreEdicion,
                        StandardCharsets.UTF_8
                );

        try {

            // Recuperar el usuario real desde Servidor Central
            Usuario usuario =
                    servidor.obtenerUsuario(
                            autenticado.getNick()
                    );

            if (!(usuario instanceof Estudiante)) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "El usuario autenticado no es un estudiante."
                );
                return;
            }

            Estudiante estudiante =
                    (Estudiante) usuario;

            // Recuperar la edición real
                EdicionCurso edicion =
                        servidor.buscarEdicion(
                                nombreEdicion
                        );

                System.out.println(
                        ">>> buscarEdicion resultado = "
                        + (edicion == null ? "NULL" : "ENCONTRADA")
                );

                if (edicion == null) {

                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "La edición solicitada no existe."
                    );
                    return;
                }

            // Servidor Central realiza la inscripción
            servidor.inscriboAEdicionCurso(
                    estudiante,
                    edicion,
                    LocalDate.now()
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/edicion?nombre="
                    + edicionCodificada
                    + "&inscripcion=ok"
            );

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            String mensaje =
                    URLEncoder.encode(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "No se pudo realizar la inscripción.",
                            StandardCharsets.UTF_8
                    );

            response.sendRedirect(
                    request.getContextPath()
                    + "/edicion?nombre="
                    + edicionCodificada
                    + "&errorInscripcion="
                    + mensaje
            );
        }
    }
}
