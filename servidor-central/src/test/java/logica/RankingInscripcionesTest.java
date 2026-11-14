package logica;

import java.lang.reflect.Proxy;
import java.time.*;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import org.junit.Test;
import persistencia.ControladorPersistencia;
import static org.junit.Assert.*;

public class RankingInscripcionesTest {
    private static final LocalDate HOY = LocalDate.of(2026, 10, 16);
    private static DTRankingInscripcion fila(long id, String nick, int dia, long rechazos) {
        return new DTRankingInscripcion(new DTInscripcion(id, nick, nick, "Edición", "Curso",
                LocalDate.of(2026, 10, dia), EstadoInscripcion.INSCRIPTO), rechazos);
    }
    private static class PersistenciaPrueba extends ControladorPersistencia {
        List<DTRankingInscripcion> filas = List.of();
        int consultas;
        LocalDate referencia;
        PersistenciaPrueba() {
            super((EntityManagerFactory) Proxy.newProxyInstance(EntityManagerFactory.class.getClassLoader(),
                    new Class<?>[]{EntityManagerFactory.class}, (p,m,a) -> { throw new AssertionError(m.getName()); }));
        }
        @Override public List<DTRankingInscripcion> listarInscripcionesConPrioridad(String docente,
                String edicion, LocalDate fecha) {
            consultas++; referencia = fecha; return filas;
        }
    }
    private ControladorInscripcion control(PersistenciaPrueba p) {
        return new ControladorInscripcion(p, Clock.fixed(HOY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
    }
    @Test public void calculaMedioPuntoPorRechazoSinAlmacenarLaPrioridad() {
        for (int n=0;n<=3;n++) assertEquals(n*0.5, fila(1,"ana",1,n).getPrioridad(),0);
        assertThrows(IllegalArgumentException.class, () -> fila(1,"ana",1,-1));
        assertThrows(NullPointerException.class, () -> new DTRankingInscripcion(null,0));
    }
    @Test public void prioridadEsDescendenteAunqueLaInscripcionSeaMasReciente() {
        var p=new PersistenciaPrueba();
        p.filas=List.of(fila(1,"bruno",1,1),fila(2,"cecilia",2,0),fila(3,"ana",3,2));
        var resultado=control(p).listarInscripcionesParaSeleccion("docente","Edición",OrdenInscripciones.PRIORIDAD);
        assertEquals(List.of("ana","bruno","cecilia"),resultado.stream().map(f->f.getInscripcion().getNicknameEstudiante()).toList());
        assertEquals(HOY,p.referencia);
        assertEquals(EstadoInscripcion.INSCRIPTO,resultado.get(0).getInscripcion().getEstado());
        assertEquals("bruno",p.filas.get(0).getInscripcion().getNicknameEstudiante());
    }
    @Test public void fechaEsAscendenteYNoDependeDeLaPrioridad() {
        var p=new PersistenciaPrueba();p.filas=List.of(fila(1,"ana",3,3),fila(2,"bruno",1,0));
        var resultado=control(p).listarInscripcionesParaSeleccion("docente","Edición",OrdenInscripciones.FECHA);
        assertEquals("bruno",resultado.get(0).getInscripcion().getNicknameEstudiante());
    }
    @Test public void empatesSeResuelvenPorFechaNicknameEIdEnAmbosOrdenes() {
        var p=new PersistenciaPrueba();
        p.filas=List.of(fila(9,"zeta",3,1),fila(5,"ana",3,1),fila(2,"ana",3,1),fila(8,"zeta",1,1));
        for(var orden:OrdenInscripciones.values()) {
            var resultado=control(p).listarInscripcionesParaSeleccion("docente","Edición",orden);
            assertEquals(List.of(8L,2L,5L,9L),resultado.stream().map(f->f.getInscripcion().getId()).toList());
        }
    }
    @Test public void identificadoresUOrdenInvalidosNoConsultanPersistencia() {
        var p=new PersistenciaPrueba();var c=control(p);
        assertThrows(IllegalArgumentException.class,()->c.listarInscripcionesParaSeleccion("","Edición",OrdenInscripciones.FECHA));
        assertThrows(IllegalArgumentException.class,()->c.listarInscripcionesParaSeleccion("docente",null,OrdenInscripciones.FECHA));
        assertThrows(IllegalArgumentException.class,()->c.listarInscripcionesParaSeleccion("docente","Edición",null));
        assertEquals(0,p.consultas);
    }
    @Test public void listaVaciaSeDevuelveSinDecisiones() {
        assertTrue(control(new PersistenciaPrueba()).listarInscripcionesParaSeleccion("docente","Edición",OrdenInscripciones.PRIORIDAD).isEmpty());
    }
}
