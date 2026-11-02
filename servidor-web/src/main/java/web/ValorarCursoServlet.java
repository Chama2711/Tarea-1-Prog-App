/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import logica.DTAutenticacion;

@WebServlet("/valorar-curso")
public class ValorarCursoServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // 1. Comprobar que existe una sesión autenticada.
        HttpSession sesion = request.getSession(false);

        if (sesion == null
                || !(sesion.getAttribute("usuario")
                        instanceof DTAutenticacion autenticado)) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        // 2. Solo los estudiantes pueden valorar.
        if (autenticado.getRol()
                != DTAutenticacion.Rol.ESTUDIANTE) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo los estudiantes pueden valorar cursos."
            );
            return;
        }

        // 3. Recuperar el curso solicitado.
        String nombreCurso = request.getParameter("curso");

        if (nombreCurso == null || nombreCurso.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Debe indicar el curso que desea valorar."
            );
            return;
        }

        nombreCurso = nombreCurso.trim();

        // 4. Recuperar la puntuación enviada.
        String valorRecibido = request.getParameter("puntuacion");

        if (valorRecibido == null || valorRecibido.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Debe seleccionar una puntuación."
            );
            return;
        }

        int puntuacion;

        try {
            puntuacion = Integer.parseInt(valorRecibido);
        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "La puntuación debe ser un número entero."
            );
            return;
        }

        // 5. Validar el rango de la interfaz actual.
        // Confirmar con A si la escala definitiva será 1 a 5.
        if (puntuacion < 1 || puntuacion > 5) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "La puntuación debe estar entre 1 y 5."
            );
            return;
        }

        // 6. Integración pendiente con Servidor Central / SOAP.
        // El nickname deberá obtenerse de autenticado.getNick().
        // A deberá comprobar la elegibilidad y registrar la valoración.

        response.sendError(
                HttpServletResponse.SC_NOT_IMPLEMENTED,
                "El registro de valoraciones todavía no está disponible."
        );
    }
}
