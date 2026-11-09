/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.lang.reflect.*;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValoracionCursoPersistenciaTest {

    private static <T> T proxy(
            Class<T> tipo, InvocationHandler manejador) {

        return tipo.cast(Proxy.newProxyInstance(
                tipo.getClassLoader(),
                new Class<?>[]{tipo},
                manejador
        ));
    }

    private static class Escenario {

        final Estudiante estudiante = new Estudiante();
        final Curso curso = new Curso();

        long participaciones = 1L;
        long valoracionesExistentes = 0L;

        boolean activa;
        boolean cerrado;

        int commits;
        int rollbacks;
        int conexiones;

        ValoracionCurso guardada;

        final EntityTransaction tx = proxy(
                EntityTransaction.class,
                (p, metodo, args) -> switch (metodo.getName()) {

                    case "begin" -> {
                        activa = true;
                        yield null;
                    }

                    case "isActive" -> activa;

                    case "commit" -> {
                        commits++;
                        activa = false;
                        yield null;
                    }

                    case "rollback" -> {
                        rollbacks++;
                        activa = false;
                        yield null;
                    }

                    default -> throw new AssertionError(
                            metodo.getName()
                    );
                }
        );

        final EntityManager em = proxy(
                EntityManager.class,
                (p, metodo, args) -> switch (metodo.getName()) {

                    case "getTransaction" -> tx;

                    case "find" -> {
                        if (args[0] == Estudiante.class) {
                            yield estudiante;
                        }
                        if (args[0] == Curso.class) {
                            yield curso;
                        }
                        throw new AssertionError("Entidad inesperada");
                    }

                    case "createQuery" -> {
                        String jpql = (String) args[0];

                        boolean esParticipacion =
                                jpql.contains("FROM Estudiante e");

                        assertTrue(
                                "Consulta JPQL inesperada",
                                esParticipacion
                                || jpql.contains("FROM ValoracionCurso v")
                        );

                        yield proxy(
                                TypedQuery.class,
                                (q, operacion, parametros) ->
                                        switch (operacion.getName()) {

                                            case "setParameter" -> q;

                                            case "getSingleResult" ->
                                                esParticipacion
                                                ? participaciones
                                                : valoracionesExistentes;

                                            default -> throw new AssertionError(
                                                    operacion.getName()
                                            );
                                        }
                        );
                    }

                    case "persist" -> {
                        guardada = (ValoracionCurso) args[0];
                        yield null;
                    }

                    case "close" -> {
                        cerrado = true;
                        yield null;
                    }

                    default -> throw new AssertionError(
                            metodo.getName()
                    );
                }
        );

        final EntityManagerFactory emf = proxy(
                EntityManagerFactory.class,
                (p, metodo, args) -> {
                    if ("createEntityManager".equals(metodo.getName())) {
                        conexiones++;
                        return em;
                    }
                    throw new AssertionError(metodo.getName());
                }
        );

        final ControladorPersistencia persistencia =
                new ControladorPersistencia(emf);

        void valorar(int puntos) {
            persistencia.registrarValoracionCurso(
                    "estudiante1", 1L, puntos
            );
        }
    }

    @Test
    public void guardaValoracionValida() {

        Escenario s = new Escenario();

        s.valorar(5);

        assertNotNull(s.guardada);
        assertSame(s.estudiante, s.guardada.getEstudiante());
        assertSame(s.curso, s.guardada.getCurso());
        assertEquals(5, s.guardada.getPuntuacion());

        assertEquals(1, s.commits);
        assertEquals(0, s.rollbacks);
        assertTrue(s.cerrado);
    }

    @Test
    public void rechazaEstudianteSinParticipacion() {

        Escenario s = new Escenario();
        s.participaciones = 0L;

        assertThrows(
                SecurityException.class,
                () -> s.valorar(4)
        );

        assertNull(s.guardada);
        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.cerrado);
    }

    @Test
    public void rechazaValoracionDuplicada() {

        Escenario s = new Escenario();
        s.valoracionesExistentes = 1L;

        assertThrows(
                IllegalStateException.class,
                () -> s.valorar(3)
        );

        assertNull(s.guardada);
        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.cerrado);
    }

    @Test
    public void rechazaPuntuacionInvalidaAntesDeConectarse() {

        Escenario s = new Escenario();

        assertThrows(
                IllegalArgumentException.class,
                () -> s.valorar(6)
        );

        assertEquals(0, s.conexiones);
        assertNull(s.guardada);
    }
}