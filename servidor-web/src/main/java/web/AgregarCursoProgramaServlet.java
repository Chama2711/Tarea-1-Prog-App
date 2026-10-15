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
import logica.IServidorCentral;

public class AgregarCursoProgramaServlet extends HttpServlet {

    @Override
    protected void doGet(
             HttpServletRequest request,
             HttpServletResponse response)
             throws ServletException, IOException {

        if (FormulariosDocente.exigirDocente(request, response) == null) {
             return;
        }

        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        List<String> programas = servidor.listarNombresProgramas();
        List<String> cursos = servidor.listarNombresCursos();

        String programaSeleccionado =
                 FormulariosDocente.valor(request, "programa");

        request.setAttribute("programas", programas);
        request.setAttribute("cursos", cursos);
        request.setAttribute("programaSeleccionado", programaSeleccionado);

        request.getRequestDispatcher("/WEB-INF/vistas/agregarCursoPrograma.jsp").forward(request, response);
    }
    
    @Override
        protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

        // 1. Verificar que sea docente
        if (FormulariosDocente.exigirDocente(request, response) == null) {
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
        IServidorCentral servidor =
                InicializadorAplicacion.getServidor(getServletContext());

        try {
            // 5. Ejecutar la operación
            servidor.agregarCursoAPrograma(nombrePrograma, nombreCurso);

            // 6. Volver al detalle del programa
            response.sendRedirect(
                    request.getContextPath()
                    + "/programa?nombre="
                    + java.net.URLEncoder.encode(
                            nombrePrograma,
                            java.nio.charset.StandardCharsets.UTF_8)
                    + "&cursoAgregado=ok"
            );

        } catch (Exception e) {
        request.setAttribute("error",
                "No se pudo agregar el curso: " + e.getMessage());

        doGet(request, response);
       }
    }
    
    
    
    
}
