package web;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.IServidorCentral;

@WebServlet(name = "InicioServlet", urlPatterns = {"", "/inicio"})
public class InicioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        IServidorCentral servidor = InicializadorAplicacion.getServidor(getServletContext());
        request.setAttribute("cursos", servidor.listarCursos());
        request.setAttribute("programas", servidor.obtenerProgramas());
        request.setAttribute("cantidadProgramas", servidor.listarNombresProgramas().size());
        request.setAttribute("cantidadCategorias", servidor.listarCategorias().size());
        request.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(request, response);
    }
}
