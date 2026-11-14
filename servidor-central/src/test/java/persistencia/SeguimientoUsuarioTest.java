package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SeguimientoUsuarioTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler manejador) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, manejador));
    }
    private static class Escenario {
        final Usuario ana = new Estudiante("ana", "ana@test", "Ana", "Uno", LocalDate.of(2000, 1, 1));
        final Usuario ben = new Estudiante("ben", "ben@test", "Ben", "Dos", LocalDate.of(2000, 1, 1));
        int abiertos, commits, rollbacks;
        boolean activa, cerrado, fallo;
        final EntityTransaction tx = proxy(EntityTransaction.class, (p, m, a) -> switch (m.getName()) {
            case "begin" -> { activa = true; yield null; }
            case "isActive" -> activa;
            case "commit" -> { commits++; activa = false; yield null; }
            case "rollback" -> { rollbacks++; activa = false; yield null; }
            default -> throw new AssertionError(m.getName());
        });
        final EntityManager em = proxy(EntityManager.class, (p, m, a) -> switch (m.getName()) {
            case "getTransaction" -> tx;
            case "close" -> { cerrado = true; yield null; }
            case "find" -> {
                if (fallo) throw new PersistenceException("detalle de conexión privado");
                if (a.length == 3) assertEquals(LockModeType.PESSIMISTIC_WRITE, a[2]);
                String nick = (String) a[1];
                yield "ana".equalsIgnoreCase(nick) ? ana : "ben".equalsIgnoreCase(nick) ? ben : null;
            }
            default -> throw new AssertionError(m.getName());
        });
        final ControladorPersistencia repo = new ControladorPersistencia(proxy(EntityManagerFactory.class, (p, m, a) -> {
            if (m.getName().equals("createEntityManager")) { abiertos++; return em; }
            throw new AssertionError(m.getName());
        }));
    }
    @Test public void fachadaAgregaYEliminaLaRelacionEnTransaccion() throws Exception {
        var s = new Escenario(); var servidor = new ServidorCentral(s.repo);
        servidor.seguirUsuario("ana", "ben"); assertEquals(1, s.ana.getSeguidos().size());
        assertSame(s.ben, s.ana.getSeguidos().get(0)); assertEquals(1, s.commits); assertTrue(s.cerrado);
        servidor.dejarDeSeguirUsuario("ana", "ben"); assertTrue(s.ana.getSeguidos().isEmpty());
        assertEquals(2, s.commits); assertEquals(0, s.rollbacks);
    }
    @Test public void destinoInexistenteNoConfirmaNingunaOperacion() {
        for (boolean seguir : new boolean[]{true, false}) {
            var s = new Escenario();
            assertThrows(IllegalArgumentException.class, () -> {
                if (seguir) s.repo.seguirUsuario("ana", "inexistente");
                else s.repo.dejarDeSeguirUsuario("ana", "inexistente");
            });
            assertTrue(s.ana.getSeguidos().isEmpty()); assertEquals(0, s.commits);
            assertEquals(1, s.rollbacks); assertTrue(s.cerrado);
        }
    }
    @Test public void noSePuedeSeguirASiMismoNiConAliasDeMayusculas() {
        for (String destino : new String[]{"ana", "ANA"}) {
            var s = new Escenario();
            assertThrows(IllegalArgumentException.class, () -> s.repo.seguirUsuario("ana", destino));
            assertTrue(s.ana.getSeguidos().isEmpty()); assertEquals(0, s.commits); assertEquals(1, s.rollbacks);
        }
    }
    @Test public void duplicadoYRelacionAusenteNoGeneranCambiosNiExito() {
        var s = new Escenario(); s.ana.getSeguidos().add(s.ben);
        assertThrows(IllegalArgumentException.class, () -> s.repo.seguirUsuario("ana", "BEN"));
        assertEquals(1, s.ana.getSeguidos().size()); assertEquals(0, s.commits);
        var vacio = new Escenario();
        assertThrows(IllegalArgumentException.class, () -> vacio.repo.dejarDeSeguirUsuario("ana", "ben"));
        assertEquals(0, vacio.commits); assertEquals(1, vacio.rollbacks);
    }
    @Test public void identidadAusenteOInexistenteSeRechaza() {
        var s = new Escenario();
        assertThrows(SecurityException.class, () -> s.repo.seguirUsuario(null, "ben")); assertEquals(0, s.abiertos);
        assertThrows(SecurityException.class, () -> s.repo.seguirUsuario("inexistente", "ben"));
        assertEquals(0, s.commits); assertEquals(1, s.rollbacks); assertTrue(s.cerrado);
        var vacio = new Escenario();
        assertThrows(IllegalArgumentException.class, () -> vacio.repo.seguirUsuario("ana", " "));
        assertEquals(0, vacio.abiertos);
    }
    @Test public void falloDeBaseRevierteYCierraSinExponerElDetalle() {
        var s = new Escenario(); s.fallo = true;
        var e = assertThrows(IllegalStateException.class, () -> s.repo.seguirUsuario("ana", "ben"));
        assertEquals("No se pudo actualizar el seguimiento.", e.getMessage());
        assertEquals(0, s.commits); assertEquals(1, s.rollbacks); assertTrue(s.cerrado);
    }
}
