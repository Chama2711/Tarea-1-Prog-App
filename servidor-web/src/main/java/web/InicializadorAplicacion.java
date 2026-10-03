package web;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import logica.IServidorCentral;
import logica.ServidorCentral;

@WebListener
public class InicializadorAplicacion implements ServletContextListener {
    private static final String SERVIDOR = InicializadorAplicacion.class.getName() + ".servidor";

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        String url = parametro(contexto, "edext.jdbc.url", false);
        String usuario = parametro(contexto, "edext.jdbc.usuario", false);
        String contrasena = parametro(contexto, "edext.jdbc.contrasena", true);
        ServidorCentral servidor = ServidorCentral.conectarBaseExistente(url, usuario, contrasena);
        try {
            // La fábrica JPA puede conectar de forma diferida; comprobar el acceso al arrancar.
            servidor.listarCategorias();
            contexto.setAttribute(SERVIDOR, servidor);
            contexto.log("edEXT: servidor central iniciado para la aplicación web.");
        } catch (RuntimeException error) {
            try {
                servidor.close();
            } finally {
                liberarControladorJdbc(contexto);
            }
            throw new IllegalStateException("No se pudo iniciar la conexión de edEXT web.", error);
        }
    }

    private static String parametro(ServletContext contexto, String nombre, boolean permiteVacio) {
        String valor = contexto.getInitParameter(nombre);
        if (valor == null || (!permiteVacio && valor.isBlank())) {
            throw new IllegalStateException("Falta configurar el parámetro de Tomcat: " + nombre);
        }
        return valor;
    }

    public static IServidorCentral getServidor(ServletContext contexto) {
        Object servidor = contexto.getAttribute(SERVIDOR);
        if (!(servidor instanceof IServidorCentral)) {
            throw new IllegalStateException("El servidor central no está disponible.");
        }
        return (IServidorCentral) servidor;
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        Object servidor = contexto.getAttribute(SERVIDOR);
        contexto.removeAttribute(SERVIDOR);
        try {
            if (servidor instanceof ServidorCentral) {
                ((ServidorCentral) servidor).close();
                contexto.log("edEXT: conexión del servidor central cerrada.");
            }
        } finally {
            liberarControladorJdbc(contexto);
        }
    }

    private void liberarControladorJdbc(ServletContext contexto) {
        ClassLoader cargadorWeb = getClass().getClassLoader();
        // El driver está dentro del WAR: liberar únicamente los recursos de esta aplicación.
        if (AbandonedConnectionCleanupThread.class.getClassLoader() == cargadorWeb) {
            AbandonedConnectionCleanupThread.checkedShutdown();
        }
        Enumeration<Driver> controladores = DriverManager.getDrivers();
        while (controladores.hasMoreElements()) {
            Driver controlador = controladores.nextElement();
            if (controlador.getClass().getClassLoader() == cargadorWeb) {
                try {
                    DriverManager.deregisterDriver(controlador);
                } catch (SQLException error) {
                    contexto.log("No se pudo liberar el controlador JDBC de edEXT.", error);
                }
            }
        }
    }
}
