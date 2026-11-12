package web;

import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import web.soap.ClienteCatalogoSoap;
import web.soap.ClienteEvaluacionesSoap;

/** La aplicación web se comunica exclusivamente con el central externo mediante SOAP. */
@WebListener
public class InicializadorAplicacion implements ServletContextListener {
    private static final String CLIENTE_CATALOGO = InicializadorAplicacion.class.getName() + ".clienteCatalogo";
    private static final String CLIENTE_EVALUACIONES = InicializadorAplicacion.class.getName() + ".clienteEvaluaciones";

    public static IServidorCentral getServidorCentral(ServletContext contexto) {
        Object atributo = contexto.getAttribute(SERVIDOR);
        if (atributo instanceof IServidorCentral servidor) {
            return servidor;
        }
        throw new IllegalStateException("El Servidor Central no ha sido inicializado en el ServletContext.");
    }

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        try {
            ClienteCatalogoSoap catalogo = new ClienteCatalogoSoap();
            ClienteEvaluacionesSoap evaluaciones = new ClienteEvaluacionesSoap();
            contexto.setAttribute(CLIENTE_CATALOGO, catalogo);
            contexto.setAttribute(CLIENTE_EVALUACIONES, evaluaciones);
            contexto.log("edEXT: clientes SOAP de catálogo y evaluaciones preparados.");
        } catch (IOException | RuntimeException error) {
            contexto.removeAttribute(CLIENTE_CATALOGO);
            contexto.removeAttribute(CLIENTE_EVALUACIONES);
            throw new IllegalStateException("No se pudieron iniciar los clientes SOAP de edEXT web.", error);
        }
    }

    public static ClienteCatalogoSoap getClienteCatalogo(ServletContext contexto) {
        Object cliente = contexto.getAttribute(CLIENTE_CATALOGO);
        if (!(cliente instanceof ClienteCatalogoSoap)) {
            throw new IllegalStateException("El cliente SOAP de catálogo no está disponible.");
        }
        return (ClienteCatalogoSoap) cliente;
    }

    public static ClienteEvaluacionesSoap getClienteEvaluaciones(ServletContext contexto) {
        Object cliente = contexto.getAttribute(CLIENTE_EVALUACIONES);
        if (!(cliente instanceof ClienteEvaluacionesSoap)) {
            throw new IllegalStateException("El cliente SOAP de evaluaciones no está disponible.");
        }
        return (ClienteEvaluacionesSoap) cliente;
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        contexto.removeAttribute(CLIENTE_CATALOGO);
        contexto.removeAttribute(CLIENTE_EVALUACIONES);
        contexto.log("edEXT: clientes SOAP retirados del contexto web.");
    }
}
