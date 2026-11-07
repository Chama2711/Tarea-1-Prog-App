/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package persistencia;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import javax.persistence.*;
import logica.EdicionCurso;
import logica.EstadoInscripcion;
import org.junit.Test;
import static org.junit.Assert.*;

public class CierreEdicionPersistenciaTest {

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

        long pendientes = 0;
        boolean existeEdicion = true;
        boolean falloConsulta = false;
        boolean activa = false;
        boolean entityManagerCerrado = false;

        int conexiones = 0;
        int commits = 0;
        int rollbacks = 0;
        int bloqueos = 0;
        int consultas = 0;

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
                            "El docente no participa en esta edición."
                    );
                }
            }
        };

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
                        assertEquals(EdicionCurso.class, args[0]);
                        assertEquals("Edición de prueba", args[1]);
                        yield existeEdicion ? edicion : null;
                    }

                    case "refresh" -> {
                        assertSame(edicion, args[0]);
                        assertEquals(
                                LockModeType.PESSIMISTIC_WRITE,
                                args[1]
                        );
                        bloqueos++;
                        yield null;
                    }

                    case "createQuery" -> {
                        consultas++;

                        String jpql = (String) args[0];

                        assertTrue(jpql.contains("COUNT(i)"));
                        assertTrue(jpql.contains("i.calificacion IS NULL"));
                        assertTrue(jpql.contains("i.estado = :estado"));

                        yield proxy(
                                TypedQuery.class,
                                (q, m, a) -> switch (m.getName()) {

                                    case "setParameter" -> {
                                        if ("edicion".equals(a[0])) {
                                            assertSame(edicion, a[1]);
                                        }

                                        if ("estado".equals(a[0])) {
                                            assertEquals(
                                                    EstadoInscripcion.ACEPTADA,
                                                    a[1]
                                            );
                                        }

                                        yield q;
                                    }

                                    case "getSingleResult" -> {
                                        if (falloConsulta) {
                                            throw new PersistenceException(
                                                    "Error simulado."
                                            );
                                        }

                                        yield pendientes;
                                    }

                                    default -> throw new AssertionError(
                                            m.getName()
                                    );
                                }
                        );
                    }

                    case "close" -> {
                        entityManagerCerrado = true;
                        yield null;
                    }

                    default -> throw new AssertionError(
                            metodo.getName()
                    );
                }
        );

        final ControladorPersistencia persistencia =
                new ControladorPersistencia(
                        proxy(
                                EntityManagerFactory.class,
                                (p, metodo, args) -> {
                                    if (!metodo.getName().equals(
                                            "createEntityManager")) {
                                        throw new AssertionError(
                                                metodo.getName()
                                        );
                                    }

                                    conexiones++;
                                    return em;
                                }
                        )
                );

        void cerrar() {
            persistencia.cerrarEdicion(
                    "docente",
                    "Edición de prueba"
            );
        }
    }

    @Test
    public void permiteCerrarCuandoNoFaltanNotas() {
        Escenario s = new Escenario();

        s.cerrar();

        assertTrue(s.edicion.isCerrada());
        assertEquals(1, s.commits);
        assertEquals(0, s.rollbacks);
        assertEquals(1, s.bloqueos);
        assertEquals(1, s.consultas);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void rechazaCerrarCuandoFaltanNotas() {
        Escenario s = new Escenario();
        s.pendientes = 3;

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                s::cerrar
        );

        assertTrue(error.getMessage().contains("3"));
        assertFalse(s.edicion.isCerrada());
        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void impideCerrarDosVeces() {
        Escenario s = new Escenario();

        s.cerrar();

        assertThrows(
                IllegalStateException.class,
                s::cerrar
        );

        assertTrue(s.edicion.isCerrada());
        assertEquals(1, s.commits);
        assertEquals(1, s.rollbacks);
        assertEquals(1, s.consultas);
    }

    @Test
    public void docenteAjenoNoPuedeCerrar() {
        Escenario s = new Escenario();

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.cerrarEdicion(
                        "otroDocente",
                        "Edición de prueba"
                )
        );

        assertFalse(s.edicion.isCerrada());
        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void edicionInexistenteNoPuedeCerrarse() {
        Escenario s = new Escenario();
        s.existeEdicion = false;

        assertThrows(
                IllegalArgumentException.class,
                s::cerrar
        );

        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void errorDeConsultaRevierteTransaccion() {
        Escenario s = new Escenario();
        s.falloConsulta = true;

        assertThrows(
                PersistenceException.class,
                s::cerrar
        );

        assertFalse(s.edicion.isCerrada());
        assertEquals(0, s.commits);
        assertEquals(1, s.rollbacks);
        assertTrue(s.entityManagerCerrado);
    }

    @Test
    public void datosInvalidosNoAbrenConexion() {
        Escenario s = new Escenario();

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.cerrarEdicion(
                        null,
                        "Edición de prueba"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> s.persistencia.cerrarEdicion(
                        "docente",
                        " "
                )
        );

        assertEquals(0, s.conexiones);
        assertEquals(0, s.commits);
    }
}
