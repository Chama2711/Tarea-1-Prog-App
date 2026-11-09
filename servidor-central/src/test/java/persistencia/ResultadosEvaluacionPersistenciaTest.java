/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package persistencia;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import logica.DTResultadoEvaluacion;
import logica.EdicionCurso;
import logica.EstadoInscripcion;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResultadosEvaluacionPersistenciaTest {

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

        boolean existeEdicion = true;
        boolean falloConsulta = false;
        boolean entityManagerCerrado = false;

        int conexiones = 0;
        int consultas = 0;

        String jpql;
        final Map<String, Object> parametros = new HashMap<>();

        List<DTResultadoEvaluacion> resultados = List.of(
                new DTResultadoEvaluacion(
                        15L,
                        "alumno1",
                        "Alumno Uno",
                        10,
                        LocalDate.of(2026, 11, 6)
                ),
                new DTResultadoEvaluacion(
                        16L,
                        "alumno2",
                        "Alumno Dos",
                        8,
                        LocalDate.of(2026, 11, 6)
                )
        );

        final EdicionCurso edicion = new EdicionCurso(
                "Edición de prueba",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 11, 30),
                20,
                LocalDate.of(2026, 2, 1)
        ) {
            @Override
            public void validarDocenteParticipante(
                    String nickDocente) {

                if (!"docente".equals(nickDocente)) {
                    throw new IllegalArgumentException(
                            "El docente no participa en la edición."
                    );
                }
            }
        };

        final TypedQuery<?> consulta = proxy(
                TypedQuery.class,
                (p, metodo, args) -> switch (metodo.getName()) {

                    case "setParameter" -> {
                        parametros.put(
                                (String) args[0],
                                args[1]
                        );
                        yield p;
                    }

                    case "getResultList" -> {
                        if (falloConsulta) {
                            throw new PersistenceException(
                                    "Error de consulta simulado."
                            );
                        }

                        yield resultados;
                    }

                    default -> throw new AssertionError(
                            "Método de consulta no simulado: "
                            + metodo.getName()
                    );
                }
        );

        final EntityManager em = proxy(
                EntityManager.class,
                (p, metodo, args) -> switch (metodo.getName()) {

                    case "find" -> {
                        assertEquals(EdicionCurso.class, args[0]);
                        assertEquals("Edición de prueba", args[1]);

                        yield existeEdicion ? edicion : null;
                    }

                    case "createQuery" -> {
                        consultas++;
                        jpql = (String) args[0];

                        assertEquals(
                                DTResultadoEvaluacion.class,
                                args[1]
                        );

                        yield consulta;
                    }

                    case "close" -> {
                        entityManagerCerrado = true;
                        yield null;
                    }

                    default -> throw new AssertionError(
                            "Método de EntityManager no simulado: "
                            + metodo.getName()
                    );
                }
        );

        final ControladorPersistencia persistencia =
                new ControladorPersistencia(
                        proxy(
                                EntityManagerFactory.class,
                                (p, metodo, args) -> {
                                    if (!"createEntityManager".equals(
                                            metodo.getName())) {

                                        throw new AssertionError(
                                                metodo.getName()
                                        );
                                    }

                                    conexiones++;
                                    return em;
                                }
                        )
                );

        List<DTResultadoEvaluacion> consultar() {
            return persistencia.consultarResultadosEdicion(
                    "docente",
                    "Edición de prueba"
            );
        }
    }

    @Test
    public void docenteConsultaResultadosDeEdicionCerrada() {
        Escenario s = new Escenario();
        s.edicion.cerrar();

        List<DTResultadoEvaluacion> obtenidos =
                s.consultar();

        assertEquals(2, obtenidos.size());

        assertEquals(
                Long.valueOf(15L),
                obtenidos.get(0).getIdInscripcion()
        );

        assertEquals(
                "alumno1",
                obtenidos.get(0).getNicknameEstudiante()
        );

        assertEquals(
                Integer.valueOf(10),
                obtenidos.get(0).getCalificacion()
        );

        assertEquals(
                LocalDate.of(2026, 11, 6),
                obtenidos.get(0).getFechaCalificacion()
        );

        assertEquals(
                Integer.valueOf(8),
                obtenidos.get(1).getCalificacion()
        );

        assertEquals(1, s.conexiones);
        assertEquals(1, s.consultas);

        assertEquals(
                "Edición de prueba",
                s.parametros.get("edicion")
        );

        assertEquals(
                EstadoInscripcion.ACEPTADA,
                s.parametros.get("estado")
        );

        assertTrue(s.jpql.contains(
                "new logica.DTResultadoEvaluacion"
        ));
        assertTrue(s.jpql.contains("i.calificacion"));
        assertTrue(s.jpql.contains("i.fechaCalificacion"));
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void edicionAbiertaNoPermiteConsultarResultados() {
        Escenario s = new Escenario();

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                s::consultar
        );

        assertTrue(error.getMessage().contains("abierta"));
        assertEquals(0, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void docenteAjenoNoPuedeConsultarResultados() {
        Escenario s = new Escenario();
        s.edicion.cerrar();

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.consultarResultadosEdicion(
                        "otroDocente",
                        "Edición de prueba"
                )
        );

        assertEquals(0, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void edicionInexistenteNoPermiteConsultar() {
        Escenario s = new Escenario();
        s.existeEdicion = false;

        assertThrows(
                IllegalArgumentException.class,
                s::consultar
        );

        assertEquals(0, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void datosInvalidosNoAbrenConexion() {
        Escenario s = new Escenario();

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.consultarResultadosEdicion(
                        null,
                        "Edición de prueba"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.consultarResultadosEdicion(
                        "docente",
                        " "
                )
        );

        assertEquals(0, s.conexiones);
        assertEquals(0, s.consultas);
    }

    @Test
    public void edicionCerradaSinAceptadosDevuelveListaVacia() {
        Escenario s = new Escenario();
        s.edicion.cerrar();
        s.resultados = List.of();

        List<DTResultadoEvaluacion> obtenidos =
                s.consultar();

        assertTrue(obtenidos.isEmpty());
        assertEquals(1, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void errorDePersistenciaCierraEntityManager() {
        Escenario s = new Escenario();
        s.edicion.cerrar();
        s.falloConsulta = true;

        assertThrows(
                PersistenceException.class,
                s::consultar
        );

        assertEquals(1, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }
}
