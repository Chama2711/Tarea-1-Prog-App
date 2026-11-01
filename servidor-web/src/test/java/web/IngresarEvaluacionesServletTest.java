/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web;

/**
 *
 * @author elizeth
 */
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logica.DTAutenticacion;
import logica.DTInscripcion;
import logica.EstadoInscripcion;
import logica.IServidorCentral;

import org.junit.Test;
import static org.junit.Assert.*;

public class IngresarEvaluacionesServletTest {

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

        final Map<String, String> parametros = new HashMap<>();
        final Map<String, Object> atributos = new HashMap<>();

        DTAutenticacion usuario = new DTAutenticacion(
                "docente",
                "Docente",
                DTAutenticacion.Rol.DOCENTE
        );

        List<String> ediciones = List.of(
                "Edicion 2025",
                "Edicion 2026"
        );

        List<DTInscripcion> aceptados = List.of(
                new DTInscripcion(
                        15L,
                        "alumno1",
                        "Alumno Uno",
                        "Edicion 2025",
                        "Curso Java",
                        LocalDate.of(2025, 4, 10),
                        EstadoInscripcion.ACEPTADA
                )
        );

        RuntimeException fallo;

        int estado = 200;
        int consultasEdiciones = 0;
        int consultasAceptados = 0;
        int logs = 0;

        String redireccion;
        String vista;
        String mensaje;
        String cache;

        final IServidorCentral central = proxy(
                IServidorCentral.class,
                (p, metodo, argumentos) -> {

                    if (fallo != null) {
                        throw fallo;
                    }

                    return switch (metodo.getName()) {

                        case "listarEdicionesDocente" -> {
                            consultasEdiciones++;

                            assertEquals(
                                    "docente",
                                    argumentos[0]
                            );

                            yield ediciones;
                        }

                        case "listarAceptadosEdicion" -> {
                            consultasAceptados++;

                            assertArrayEquals(
                                    new Object[]{
                                        "docente",
                                        "Edicion 2025"
                                    },
                                    argumentos
                            );

                            yield aceptados;
                        }

                        default -> throw new AssertionError(
                                "Operacion inesperada: "
                                + metodo.getName()
                        );
                    };
                }
        );

        final ServletContext contexto = proxy(
                ServletContext.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "getAttribute" -> central;

                        case "log" -> {
                            logs++;
                            yield null;
                        }

                        default -> throw new AssertionError(
                                metodo.getName()
                        );
                    };
                }
        );

        final HttpSession sesion = proxy(
                HttpSession.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "getAttribute" -> usuario;

                        default -> throw new AssertionError(
                                metodo.getName()
                        );
                    };
                }
        );

        final HttpServletRequest request = proxy(
                HttpServletRequest.class,
                (p, metodo, argumentos) -> {

                    return switch (metodo.getName()) {

                        case "getSession" ->
                            usuario == null ? null : sesion;

                        case "getContextPath" -> "/edext";

                        case "getParameter" ->
                            parametros.get(argumentos[0]);

                        case "setAttribute" -> {
                            atributos.put(
                                    (String) argumentos[0],
                                    argumentos[1]
                            );
                            yield null;
                        }

                        case "getRequestDispatcher" -> {
                            String destino =
                                    (String) argumentos[0];

                            yield proxy(
                                    RequestDispatcher.class,
                                    (q, m, a) -> {
                                        vista = destino;
                                        return null;
                                    }
                            );
                        }

                        default -> throw new AssertionError(
                                metodo.getName()
                        );
                    };
                }
        );

        final HttpServletResponse response = proxy(
                HttpServletResponse.class,
                (p, metodo, argumentos) -> {

                    switch (metodo.getName()) {

                        case "setHeader" ->
                            cache = (String) argumentos[1];

                        case "sendRedirect" -> {
                            estado = 302;
                            redireccion =
                                    (String) argumentos[0];
                        }

                        case "sendError" -> {
                            estado = (Integer) argumentos[0];

                            if (argumentos.length > 1) {
                                mensaje =
                                        (String) argumentos[1];
                            }
                        }

                        default -> throw new AssertionError(
                                metodo.getName()
                        );
                    }

                    return null;
                }
        );

        void seleccionarEdicion() {
            parametros.put("edicion", "Edicion 2025");
        }

        void ejecutar() throws Exception {

            var servlet = new IngresarEvaluacionesServlet();

            servlet.init(proxy(
                    ServletConfig.class,
                    (p, metodo, argumentos) -> {

                        return switch (metodo.getName()) {

                            case "getServletContext" -> contexto;

                            case "getServletName" ->
                                "ingresar-evaluaciones-test";

                            case "getInitParameter" -> null;

                            default -> throw new AssertionError(
                                    metodo.getName()
                            );
                        };
                    }
            ));

            servlet.doGet(request, response);
        }
    }

    @Test
    public void visitanteEsRedirigidoAlLogin()
            throws Exception {

        var escenario = new Escenario();
        escenario.usuario = null;

        escenario.ejecutar();

        assertEquals(302, escenario.estado);
        assertEquals("/edext/login", escenario.redireccion);
        assertEquals(0, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void estudianteNoPuedeAcceder()
            throws Exception {

        var escenario = new Escenario();

        escenario.usuario = new DTAutenticacion(
                "estudiante",
                "Estudiante",
                DTAutenticacion.Rol.ESTUDIANTE
        );

        escenario.ejecutar();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
    }

    @Test
    public void aperturaInicialMuestraEdiciones()
            throws Exception {

        var escenario = new Escenario();

        escenario.ejecutar();

        assertEquals(200, escenario.estado);

        assertEquals(
                "/WEB-INF/vistas/ingresarEvaluaciones.jsp",
                escenario.vista
        );

        assertEquals(
                escenario.ediciones,
                escenario.atributos.get("ediciones")
        );

        assertEquals(
                "",
                escenario.atributos.get("edicion")
        );

        assertEquals(
                List.of(),
                escenario.atributos.get("aceptados")
        );

        assertEquals(1, escenario.consultasEdiciones);
        assertEquals(0, escenario.consultasAceptados);
        assertEquals("no-store", escenario.cache);
    }

    @Test
    public void docenteConsultaEstudiantesAceptados()
            throws Exception {

        var escenario = new Escenario();
        escenario.seleccionarEdicion();

        escenario.ejecutar();

        assertEquals(200, escenario.estado);

        assertEquals(
                1,
                escenario.consultasAceptados
        );

        assertEquals(
                escenario.aceptados,
                escenario.atributos.get("aceptados")
        );

        assertEquals(
                "Edicion 2025",
                escenario.atributos.get("edicion")
        );
    }

    @Test
    public void docenteNoPuedeConsultarEdicionAjena()
            throws Exception {

        var escenario = new Escenario();

        escenario.parametros.put(
                "edicion",
                "Edicion de otro docente"
        );

        escenario.ejecutar();

        assertEquals(403, escenario.estado);
        assertEquals(0, escenario.consultasAceptados);
        assertNull(escenario.vista);
    }

    @Test
    public void edicionSinAceptadosMuestraListaVacia()
            throws Exception {

        var escenario = new Escenario();
        escenario.seleccionarEdicion();
        escenario.aceptados = List.of();

        escenario.ejecutar();

        assertEquals(200, escenario.estado);

        assertEquals(
                List.of(),
                escenario.atributos.get("aceptados")
        );

        assertEquals(1, escenario.consultasAceptados);
    }

    @Test
    public void falloInternoDevuelveError500()
            throws Exception {

        var escenario = new Escenario();

        escenario.fallo = new IllegalStateException(
                "Detalle interno JDBC"
        );

        escenario.ejecutar();

        assertEquals(500, escenario.estado);
        assertEquals(1, escenario.logs);
        assertNull(escenario.vista);

        assertEquals(
                "No se pudieron consultar las evaluaciones.",
                escenario.mensaje
        );
    }

    @Test
    public void edicionSinAsignarNoConsultaAceptados()
            throws Exception {

        var escenario = new Escenario();
        escenario.ediciones = List.of();

        escenario.ejecutar();

        assertEquals(200, escenario.estado);
        assertEquals(0, escenario.consultasAceptados);

        assertEquals(
                List.of(),
                escenario.atributos.get("ediciones")
        );
    }
}
