/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import logica.DTAutenticacion;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValorarCursoServletTest {

    // Crea objetos simulados a partir de interfaces.
    private static <T> T proxy(
            Class<T> tipo,
            InvocationHandler manejador) {

        return tipo.cast(Proxy.newProxyInstance(
                tipo.getClassLoader(),
                new Class<?>[]{tipo},
                manejador
        ));
    }

    private static class Escenario {

        // Datos que enviaría el formulario.
        final Map<String, String> parametros = new HashMap<>();

        // Por defecto, trabajamos con un estudiante.
        DTAutenticacion usuario = new DTAutenticacion(
                "alumno1",
                "Alumno Uno",
                DTAutenticacion.Rol.ESTUDIANTE
        );

        boolean sesionDisponible = true;

        int estado = 200;
        String redireccion;
        String mensaje;

        final HttpSession sesion = proxy(
                HttpSession.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "getAttribute" -> {
                            assertEquals("usuario", argumentos[0]);
                            yield usuario;
                        }

                        default -> throw new AssertionError(
                                "Operacion inesperada en sesion: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletRequest request = proxy(
                HttpServletRequest.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "getSession" -> {
                            assertEquals(false, argumentos[0]);
                            yield sesionDisponible ? sesion : null;
                        }

                        case "getContextPath" -> "/edext";

                        case "getParameter" ->
                            parametros.get((String) argumentos[0]);

                        default -> throw new AssertionError(
                                "Operacion inesperada en request: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final HttpServletResponse response = proxy(
                HttpServletResponse.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "sendRedirect" -> {
                            redireccion = (String) argumentos[0];
                            yield null;
                        }

                        case "sendError" -> {
                            estado = (Integer) argumentos[0];

                            if (argumentos.length > 1) {
                                mensaje = (String) argumentos[1];
                            }

                            yield null;
                        }

                        default -> throw new AssertionError(
                                "Operacion inesperada en response: "
                                + metodo.getName()
                        );
                    };
                }
        );

        void ejecutar() throws IOException {
            new ValorarCursoServlet().doPost(
                    request,
                    response
            );
        }

        void datosValidos() {
            parametros.put("curso", "Curso Java");
            parametros.put("puntuacion", "5");
        }
    }

    @Test
    public void visitanteSinSesionRedirigeAlLogin()
            throws IOException {

        Escenario e = new Escenario();
        e.sesionDisponible = false;

        e.ejecutar();

        assertEquals("/edext/login", e.redireccion);
        assertEquals(200, e.estado);
    }

    @Test
    public void sesionSinUsuarioRedirigeAlLogin()
            throws IOException {

        Escenario e = new Escenario();
        e.usuario = null;

        e.ejecutar();

        assertEquals("/edext/login", e.redireccion);
        assertEquals(200, e.estado);
    }

    @Test
    public void docenteNoPuedeValorar()
            throws IOException {

        Escenario e = new Escenario();

        e.usuario = new DTAutenticacion(
                "docente1",
                "Docente Uno",
                DTAutenticacion.Rol.DOCENTE
        );

        e.datosValidos();
        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_FORBIDDEN,
                e.estado
        );
    }

    @Test
    public void rechazaCursoAusente()
            throws IOException {

        Escenario e = new Escenario();
        e.parametros.put("puntuacion", "4");

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_BAD_REQUEST,
                e.estado
        );
    }

    @Test
    public void rechazaPuntuacionAusente()
            throws IOException {

        Escenario e = new Escenario();
        e.parametros.put("curso", "Curso Java");

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_BAD_REQUEST,
                e.estado
        );
    }

    @Test
    public void rechazaPuntuacionNoNumerica()
            throws IOException {

        Escenario e = new Escenario();
        e.datosValidos();
        e.parametros.put("puntuacion", "excelente");

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_BAD_REQUEST,
                e.estado
        );
    }

    @Test
    public void rechazaPuntuacionMenorQueUno()
            throws IOException {

        Escenario e = new Escenario();
        e.datosValidos();
        e.parametros.put("puntuacion", "0");

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_BAD_REQUEST,
                e.estado
        );
    }

    @Test
    public void rechazaPuntuacionMayorQueCinco()
            throws IOException {

        Escenario e = new Escenario();
        e.datosValidos();
        e.parametros.put("puntuacion", "6");

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_BAD_REQUEST,
                e.estado
        );
    }

    @Test
    public void solicitudValidaAunNoGuarda()
            throws IOException {

        Escenario e = new Escenario();
        e.datosValidos();

        e.ejecutar();

        assertEquals(
                HttpServletResponse.SC_NOT_IMPLEMENTED,
                e.estado
        );

        assertNull(e.redireccion);
    }
}
