package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Verifica composición, autorización y ciclo de consulta sin acceder a una base personal. */
public class ConsultaRankingTest {
    private static final LocalDate HOY=LocalDate.of(2026,10,16);
    private static <T>T proxy(Class<T> type,InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},handler));
    }
    private static class Escenario {
        final EdicionCurso edicion=new EdicionCurso("Actual",HOY.minusDays(1),HOY.plusDays(1),20,HOY);
        List<DTInscripcion> actuales=List.of(new DTInscripcion(1L,"ana","Ana","Actual","Curso",HOY,EstadoInscripcion.INSCRIPTO),
                new DTInscripcion(2L,"ben","Ben","Actual","Curso",HOY,EstadoInscripcion.ACEPTADA));
        List<Object[]> antecedentes=Collections.singletonList(new Object[]{"ana",2L});
        final Map<String,Object> parametros=new HashMap<>();
        int consultas;
        boolean cerrada,existe=true;
        final ControladorPersistencia persistencia;
        Escenario() {
            edicion.agregoDocente(new Docente("docente","doc@test","Doc","Uno",HOY.minusYears(30)));
            edicion.setCurso(new Curso(){@Override public Long getId(){return 7L;}});
            EntityManager em=proxy(EntityManager.class,(p,m,a)->switch(m.getName()) {
                case "find" -> existe?edicion:null;
                case "close" -> {cerrada=true;yield null;}
                case "createQuery" -> {
                    consultas++;boolean esActual=a[1]==DTInscripcion.class;
                    yield proxy(TypedQuery.class,(q,metodo,args)->switch(metodo.getName()) {
                        case "setParameter" -> {parametros.put((String)args[0],args[1]);yield q;}
                        case "getResultList" -> esActual?actuales:antecedentes;
                        default -> throw new AssertionError(metodo.getName());
                    });
                }
                default -> throw new AssertionError("Operación inesperada (incluidas escrituras): "+m.getName());
            });
            persistencia=new ControladorPersistencia(proxy(EntityManagerFactory.class,(p,m,a)-> {
                if(m.getName().equals("createEntityManager"))return em;
                throw new AssertionError(m.getName());
            }));
        }
        List<DTRankingInscripcion> consultar(String nick){return persistencia.listarInscripcionesConPrioridad(nick,"Actual",HOY);}
    }
    @Test public void combinaContadoresYSinAntecedentesAsignaCeroSinCambiarEstados() {
        var s=new Escenario();var filas=s.consultar("docente");
        assertEquals(1,filas.get(0).getPrioridad(),0);assertEquals(0,filas.get(1).getPrioridad(),0);
        assertEquals(EstadoInscripcion.ACEPTADA,filas.get(1).getInscripcion().getEstado());
        assertEquals(2,s.consultas);assertTrue(s.cerrada);
        assertEquals(List.of("ana","ben"),s.parametros.get("nicks"));
        assertEquals(7L,s.parametros.get("curso"));
        assertEquals(EstadoInscripcion.RECHAZADA,s.parametros.get("rechazada"));
        assertEquals(HOY,s.parametros.get("referencia"));
    }
    @Test public void sinInscripcionesNoConsultaAntecedentes() {
        var s=new Escenario();s.actuales=List.of();assertTrue(s.consultar("docente").isEmpty());
        assertEquals(1,s.consultas);assertTrue(s.cerrada);
    }
    @Test public void docenteAjenoNoConsultaInscripciones() {
        var s=new Escenario();assertThrows(IllegalArgumentException.class,()->s.consultar("otro"));
        assertEquals(0,s.consultas);assertTrue(s.cerrada);
    }
    @Test public void edicionInexistenteSeRechazaYCierraLaConsulta() {
        var s=new Escenario();s.existe=false;assertThrows(IllegalArgumentException.class,()->s.consultar("docente"));
        assertTrue(s.cerrada);
    }
    @Test public void edicionesFuturasOFinalizadasNoAdmitenSeleccion() {
        for(boolean futura:new boolean[]{false,true}) {
            var s=new Escenario();
            if(futura)s.edicion.setFechaInicio(HOY.plusDays(1));else s.edicion.setFechaFin(HOY.minusDays(1));
            assertThrows(IllegalArgumentException.class,()->s.consultar("docente"));
            assertEquals(0,s.consultas);assertTrue(s.cerrada);
        }
    }
    @Test public void diaDeInicioYFinIncluidosEnVigencia() {
        var s=new Escenario();s.edicion.setFechaInicio(HOY);s.edicion.setFechaFin(HOY);
        assertEquals(2,s.consultar("docente").size());
    }
}
