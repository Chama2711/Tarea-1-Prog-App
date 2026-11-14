package logica;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

public class ServidorCentralConexionTest {
    private static class Conexion {
        boolean abierta = true;
        int cierres, consultas, sesionesCerradas;
        final Curso curso;
        final Usuario usuario;
        final EntityManagerFactory fabrica;

        Conexion(String nombre) {
            curso = new Curso(nombre, "1 mes", 10, 1, LocalDate.now(), "Descripción", "");
            usuario = new Estudiante(nombre, nombre + "@test", "Nombre", "Apellido", LocalDate.of(2000, 1, 1));
            fabrica = (EntityManagerFactory) Proxy.newProxyInstance(
                    EntityManagerFactory.class.getClassLoader(), new Class[]{EntityManagerFactory.class},
                    (proxy, metodo, args) -> {
                        switch (metodo.getName()) {
                            case "isOpen": return abierta;
                            case "close": abierta = false; cierres++; return null;
                            case "createEntityManager":
                                if (!abierta) throw new IllegalStateException("Conexión cerrada");
                                return nuevaSesion();
                            default: throw new AssertionError(metodo.getName());
                        }
                    });
        }

        private EntityManager nuevaSesion() {
            return (EntityManager) Proxy.newProxyInstance(
                    EntityManager.class.getClassLoader(), new Class[]{EntityManager.class},
                    (proxy, metodo, args) -> {
                        switch (metodo.getName()) {
                            case "close": sesionesCerradas++; return null;
                            case "createQuery":
                                consultas++;
                                List<?> resultado = args[1] == Curso.class ? List.of(curso) : List.of(usuario);
                                return Proxy.newProxyInstance(TypedQuery.class.getClassLoader(),
                                        new Class[]{TypedQuery.class}, (consulta, operacion, parametros) -> {
                                            if (operacion.getName().equals("getResultList")) return resultado;
                                            throw new AssertionError(operacion.getName());
                                        });
                            default: throw new AssertionError(metodo.getName());
                        }
                    });
        }

        ServidorCentral servidor() {
            return new ServidorCentral(new ControladorPersistencia(fabrica));
        }
    }

    @Test public void dosServidoresConsultanSusPropiasConexiones() {
        Conexion swing = new Conexion("Swing");
        Conexion web = new Conexion("Web");
        try (ServidorCentral primero = swing.servidor(); ServidorCentral segundo = web.servidor()) {
            assertSame(swing.curso, primero.listarCursos().get(0));
            assertSame(web.curso, segundo.listarCursos().get(0));
            assertSame(swing.usuario, primero.obtenerUsuarios().get(0));
            assertSame(web.usuario, segundo.obtenerUsuarios().get(0));
            assertEquals(2, swing.sesionesCerradas);
            assertEquals(2, web.sesionesCerradas);
        }
        assertFalse(swing.abierta);
        assertFalse(web.abierta);
    }

    @Test public void cerrarUnServidorNoCierraElOtroYPermiteRepetirElCierre() {
        Conexion swing = new Conexion("Swing");
        Conexion web = new Conexion("Web");
        ServidorCentral primero = swing.servidor();
        try (ServidorCentral segundo = web.servidor()) {
            primero.close();
            primero.close();
            assertEquals(1, swing.cierres);
            assertThrows(IllegalStateException.class, primero::listarCursos);
            assertSame(web.curso, segundo.listarCursos().get(0));
            assertTrue(web.abierta);
        }
    }

    @Test public void configuracionIncompletaNoUsaLaConexionPredeterminada() {
        for (String url : new String[]{null, "", " ", "localhost:3306/edext_web"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> ServidorCentral.conectarBaseExistente(url, "root", ""));
        }
        assertThrows(IllegalArgumentException.class,
                () -> ServidorCentral.conectarBaseExistente("jdbc:mysql://localhost/edext_web", " ", ""));
        assertThrows(IllegalArgumentException.class,
                () -> ServidorCentral.conectarBaseExistente("jdbc:mysql://localhost/edext_web", "root", null));
        assertThrows(NullPointerException.class, () -> new ServidorCentral(null));
    }
}
