/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import logica.DTAutenticacion;
import web.soap.ClienteCatalogoSoap;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/vistas/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String identificador = request.getParameter("identificador");
        String clave = request.getParameter("clave");

        try {
            ClienteCatalogoSoap servidor =
                    InicializadorAplicacion.getClienteCatalogo(getServletContext());

            DTAutenticacion usuario =
                    servidor.autenticarUsuario(identificador, clave);

            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuario);

            response.sendRedirect(
                    request.getContextPath() + "/inicio"
            );

        } catch (jakarta.xml.ws.WebServiceException error) {
            getServletContext().log("No se pudo iniciar sesión por SOAP.", error);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("error", "El servidor central no está disponible. Intentá nuevamente más tarde.");
            request.setAttribute("identificador", identificador);
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());
            request.setAttribute("identificador", identificador);

            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp")
                    .forward(request, response);
        }
    }
}
