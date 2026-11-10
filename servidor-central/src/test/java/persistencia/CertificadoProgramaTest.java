package persistencia;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import logica.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;
import static org.junit.Assert.*;

public class CertificadoProgramaTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, h));
    }
    private static class Escenario {
        final LocalDate fecha = LocalDate.of(2026, 11, 10);
        final Estudiante estudiante = new Estudiante("ana", "a@test.edu", "Ana María", "Pérez", fecha.minusYears(20));
        final ProgramaFormacion programa = new ProgramaFormacion("Formación & Educación ñ", "", fecha, fecha, fecha);
        final Curso curso = new Curso("Robótica & Educación", "", 20, 3, fecha, "", "");
        final EdicionCurso edicion = new EdicionCurso("Edición 2026", fecha, fecha, 10, fecha);
        Usuario usuario = estudiante;
        boolean encontrado = true, cerrada;
        final ControladorPersistencia persistencia;
        Escenario() {
            try { var campo = Curso.class.getDeclaredField("id"); campo.setAccessible(true); campo.set(curso, 1L); }
            catch (ReflectiveOperationException e) { throw new AssertionError(e); }
            programa.agregarCurso(curso); edicion.setCurso(curso);
            var em = proxy(EntityManager.class, (p,m,a) -> switch(m.getName()) {
                case "find" -> usuario;
                case "createQuery" -> proxy(TypedQuery.class, (q,n,b) -> switch(n.getName()) {
                    case "setParameter" -> q;
                    case "getResultList" -> encontrado ? List.of(programa) : List.of();
                    default -> throw new AssertionError(n.getName());
                });
                case "close" -> { cerrada = true; yield null; }
                default -> throw new AssertionError("No se esperan escrituras: " + m.getName());
            });
            persistencia = new ControladorPersistencia(proxy(EntityManagerFactory.class, (p,m,a) -> em));
        }
        Inscripcion resultado(int nota, boolean cerrar) {
            var inscripcion = new Inscripcion(fecha, edicion);
            inscripcion.cambiarEstado(EstadoInscripcion.ACEPTADA);
            inscripcion.registrarCalificacion(nota, fecha);
            estudiante.agregoInscripcion(inscripcion);
            if (cerrar) edicion.cerrar();
            return inscripcion;
        }
        DTCertificadoPrograma consultar() { return persistencia.consultarCertificadoPrograma("ana", programa.getNombre()); }
    }
    @Test public void seisApruebaYElPdfSeGeneraDesdeLaFachadaConSusResultados() throws Exception {
        var s = new Escenario(); s.resultado(6, true);
        byte[] pdf = new ServidorCentral(s.persistencia).generarCertificadoPrograma("ana", s.programa.getNombre());
        try(var documento = Loader.loadPDF(pdf)) {
            String texto = new PDFTextStripper().getText(documento);
            assertTrue(texto.contains("Ana María Pérez")); assertTrue(texto.contains("Robótica & Educación"));
            assertTrue(texto.contains("APROBADO")); assertTrue(texto.contains("6/12"));
            assertTrue(texto.contains("10/11/2026")); assertTrue(texto.contains("Formación & Educación ñ"));
        }
        assertTrue(s.cerrada);
    }
    @Test public void cincoNoAprueba() {
        var s = new Escenario(); s.resultado(5,true);
        assertThrows(IllegalArgumentException.class,s::consultar); assertTrue(s.cerrada);
    }
    @Test public void noCertificaAntesDelCierreAunqueLaNotaSeaDoce() {
        var s = new Escenario(); s.resultado(12,false);
        assertThrows(IllegalArgumentException.class,s::consultar);
    }
    @Test public void exigeTodosLosCursosDelPrograma() {
        var s = new Escenario(); s.resultado(12,true);
        s.programa.agregarCurso(new Curso("Otro curso", "", 1, 1, s.fecha, "", ""));
        assertThrows(IllegalArgumentException.class,s::consultar);
    }
    @Test public void sinCursosNoExpideUnCertificadoVacio() {
        var s = new Escenario(); s.programa.getCursos().clear();
        assertThrows(IllegalArgumentException.class,s::consultar);
    }
    @Test public void inscripcionPendienteORechazadaNoEsAprobacion() {
        for (boolean rechazada : List.of(false,true)) {
            var s = new Escenario(); var i = new Inscripcion(s.fecha,s.edicion);
            if (rechazada) i.cambiarEstado(EstadoInscripcion.RECHAZADA);
            s.estudiante.agregoInscripcion(i); s.edicion.cerrar();
            assertThrows(IllegalArgumentException.class,s::consultar);
        }
    }
    @Test public void noConfundeUnaInscripcionAProgramaConUnResultado() {
        var s = new Escenario(); s.estudiante.agregoInscripcion(new Inscripcion(s.fecha,s.programa));
        assertThrows(IllegalArgumentException.class,s::consultar);
    }
    @Test public void unResultadoAnteriorAprobadoSirveAunqueHayaOtroReprobado() {
        var s = new Escenario(); s.resultado(6,true);
        var otra = new EdicionCurso("Otra edición",s.fecha,s.fecha,10,s.fecha); otra.setCurso(s.curso);
        var i = new Inscripcion(s.fecha,otra); i.cambiarEstado(EstadoInscripcion.ACEPTADA);
        i.registrarCalificacion(3,s.fecha); otra.cerrar(); s.estudiante.agregoInscripcion(i);
        assertEquals(6,s.consultar().resultados().getFirst().nota());
    }
    @Test public void eligeUnaSolaEdicionAprobadaPorCurso() {
        var s = new Escenario(); s.resultado(6,true);
        var otra = new EdicionCurso("Posterior",s.fecha,s.fecha,10,s.fecha); otra.setCurso(s.curso);
        var i = new Inscripcion(s.fecha,otra); i.cambiarEstado(EstadoInscripcion.ACEPTADA);
        i.registrarCalificacion(12,s.fecha.plusDays(1)); otra.cerrar(); s.estudiante.agregoInscripcion(i);
        var resultados=s.consultar().resultados();assertEquals(1,resultados.size());assertEquals(12,resultados.getFirst().nota());
    }
    @Test public void estudianteOProgramaInexistenteYParametrosVacios() {
        var s = new Escenario(); s.usuario = null;
        assertThrows(SecurityException.class,s::consultar);assertTrue(s.cerrada);
        s = new Escenario(); s.encontrado = false;
        assertThrows(RecursoNoEncontradoException.class,s::consultar);assertTrue(s.cerrada);
        var p = new Escenario().persistencia;
        assertThrows(SecurityException.class,()->p.consultarCertificadoPrograma(" ","Programa"));
        assertThrows(IllegalArgumentException.class,()->p.consultarCertificadoPrograma("ana",null));
    }
}
