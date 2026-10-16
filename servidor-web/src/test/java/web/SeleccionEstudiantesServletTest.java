package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SeleccionEstudiantesServletTest {
    private static <T>T proxy(Class<T> t,InvocationHandler h) {
        return t.cast(Proxy.newProxyInstance(t.getClassLoader(),new Class<?>[]{t},h));
    }
    private static class Escenario {
        final Map<String,String[]> parametros=new HashMap<>();
        final Map<String,Object> atributos=new HashMap<>();
        DTAutenticacion usuario=new DTAutenticacion("docente","Docente",DTAutenticacion.Rol.DOCENTE);
        final DTEdicionConsulta edicion=new DTEdicionConsulta("Edición & Uno","Curso",LocalDate.now(),
                LocalDate.now().plusDays(1),LocalDate.now(),1,true,false,List.of());
        List<DTEdicionConsulta> ediciones=List.of(edicion);
        RuntimeException fallo;
        int estado=200,guardados,consultas;
        String vista,redireccion,actor;
        OrdenInscripciones orden;
        String nombreCurso="Curso";
        Map<Long,EstadoInscripcion> decisiones;
        final IServidorCentral central=proxy(IServidorCentral.class,(p,m,a)-> {
            consultas++;
            return switch(m.getName()) {
                case "listarNombresInstitutos" -> List.of("INCO");
                case "listarNombresCursosPorInstituto" -> List.of(nombreCurso);
                case "listarEdicionesParaSeleccion" -> {actor=(String)a[0];yield ediciones;}
                case "listarInscripcionesParaSeleccion" -> {
                    actor=(String)a[0];orden=(OrdenInscripciones)a[2];yield List.of();
                }
                case "seleccionarEstudiantes" -> {
                    if(fallo!=null)throw fallo;
                    if(((Map<?,?>)a[2]).isEmpty())throw new IllegalArgumentException("Seleccione al menos una inscripción.");
                    actor=(String)a[0];decisiones=new LinkedHashMap<>((Map<Long,EstadoInscripcion>)a[2]);
                    guardados++;yield null;
                }
                default -> throw new AssertionError(m.getName());
            };
        });
        final ServletContext contexto=proxy(ServletContext.class,(p,m,a)->switch(m.getName()) {
            case "getAttribute" -> central;case "log" -> null;default -> throw new AssertionError(m.getName());
        });
        final HttpSession sesion=proxy(HttpSession.class,(p,m,a)->usuario);
        final HttpServletRequest request=proxy(HttpServletRequest.class,(p,m,a)->switch(m.getName()) {
            case "getSession" -> usuario==null?null:sesion;
            case "setCharacterEncoding" -> null;
            case "getContextPath" -> "/edext";
            case "getParameter" -> parametros.containsKey(a[0])?parametros.get(a[0])[0]:null;
            case "getParameterMap" -> parametros;
            case "getAttribute" -> atributos.get(a[0]);
            case "setAttribute" -> {atributos.put((String)a[0],a[1]);yield null;}
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class,(q,n,b)->{vista=(String)a[0];return null;});
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response=proxy(HttpServletResponse.class,(p,m,a)-> {
            switch(m.getName()) {
                case "setHeader": break;
                case "sendError":estado=(Integer)a[0];break;
                case "sendRedirect":estado=302;redireccion=(String)a[0];break;
                default:throw new AssertionError(m.getName());
            }return null;
        });
        Escenario(){valor("instituto","INCO");valor("curso","Curso");valor("edicion","Edición & Uno");}
        void valor(String k,String v){parametros.put(k,new String[]{v});}
        void ejecutar(boolean post)throws Exception {
            var servlet=new SeleccionEstudiantesServlet();
            servlet.init(proxy(ServletConfig.class,(p,m,a)->switch(m.getName()) {
                case "getServletContext" -> contexto;case "getServletName" -> "seleccion-test";
                case "getInitParameter" -> null;default -> throw new AssertionError(m.getName());
            }));
            if(post)servlet.doPost(request,response);else servlet.doGet(request,response);
        }
    }
    @Test public void visitanteYEstudianteNoConsultanNiGuardan()throws Exception {
        for(boolean post:new boolean[]{false,true}) {
            var visitante=new Escenario();visitante.usuario=null;visitante.ejecutar(post);
            assertEquals("/edext/login",visitante.redireccion);assertEquals(0,visitante.consultas);
            var estudiante=new Escenario();estudiante.usuario=new DTAutenticacion("est","Est",DTAutenticacion.Rol.ESTUDIANTE);
            estudiante.ejecutar(post);assertEquals(403,estudiante.estado);assertEquals(0,estudiante.consultas);
        }
    }
    @Test public void getPermiteAmbosOrdenesSinGuardar()throws Exception {
        for(var orden:OrdenInscripciones.values()) {
            var s=new Escenario();s.valor("orden",orden.name());s.valor("nickDocente","otro");s.ejecutar(false);
            assertEquals("docente",s.actor);assertEquals(orden,s.orden);assertEquals(0,s.guardados);
            assertEquals("/WEB-INF/vistas/seleccionarEstudiantes.jsp",s.vista);
        }
    }
    @Test public void edicionAjenaONoVigenteNoConsultaElRanking()throws Exception {
        for(boolean post:new boolean[]{false,true}) {
            var s=new Escenario();s.ediciones=List.of();s.ejecutar(post);
            assertEquals(403,s.estado);assertNull(s.orden);assertEquals(0,s.guardados);
        }
    }
    @Test public void cursoFueraDelInstitutoSeRechaza()throws Exception {
        var s=new Escenario();s.valor("curso","Ajeno");s.ejecutar(false);assertEquals(400,s.estado);assertEquals(0,s.guardados);
    }
    @Test public void ordenDesconocidoSeRechaza()throws Exception {
        var s=new Escenario();s.valor("orden","SQL");s.ejecutar(false);assertEquals(400,s.estado);
    }
    @Test public void guardaSoloDecisionesElegidasConActorDeSesionYRedirige()throws Exception {
        var s=new Escenario();s.valor("decision_1","ACEPTADA");s.valor("decision_2","");s.valor("decision_3","RECHAZADA");
        s.valor("nickDocente","otro");s.valor("orden","PRIORIDAD");s.ejecutar(true);
        assertEquals(Map.of(1L,EstadoInscripcion.ACEPTADA,3L,EstadoInscripcion.RECHAZADA),s.decisiones);
        assertEquals("docente",s.actor);assertEquals(302,s.estado);assertTrue(s.redireccion.contains("edicion=Edici%C3%B3n+%26+Uno"));
        assertTrue(s.redireccion.contains("orden=PRIORIDAD&guardado=1"));
    }
    @Test public void noEnvioDeDecisionesMuestraErrorSinGuardar()throws Exception {
        var s=new Escenario();s.valor("decision_1","");s.ejecutar(true);
        assertEquals(0,s.guardados);assertNotNull(s.atributos.get("error"));assertNotNull(s.vista);
    }
    @Test public void estadosIdsOValoresDuplicadosInvalidosNoGuardan()throws Exception {
        for(String estado:List.of("INSCRIPTO","INVENTADO")) {
            var s=new Escenario();s.valor("decision_1",estado);s.ejecutar(true);assertEquals(0,s.guardados);assertNotNull(s.vista);
        }
        for(String id:List.of("abc","-1")) {
            var s=new Escenario();s.valor("decision_"+id,"ACEPTADA");s.ejecutar(true);assertEquals(0,s.guardados);
        }
        var s=new Escenario();s.parametros.put("decision_1",new String[]{"ACEPTADA","RECHAZADA"});s.ejecutar(true);
        assertEquals(0,s.guardados);assertNotNull(s.atributos.get("error"));
    }
    @Test public void reconoceNombresConSaltosDeLineaEnviadosPorFormulariosHtml()throws Exception {
        var s=new Escenario();s.nombreCurso="Curso\nUno";
        s.ediciones=List.of(new DTEdicionConsulta("Edición\nUno",s.nombreCurso,LocalDate.now(),
                LocalDate.now().plusDays(1),LocalDate.now(),1,true,false,List.of()));
        s.valor("curso","Curso\r\nUno");s.valor("edicion","Edición\r\nUno");s.ejecutar(false);
        assertEquals(200,s.estado);assertEquals("Curso\nUno",s.atributos.get("curso"));
        assertEquals("Edición\nUno",s.atributos.get("edicion"));assertNotNull(s.vista);
    }

    @Test public void errorDeCupoConservaDecisionesYLaConsulta()throws Exception {
        var s=new Escenario();s.valor("decision_1","ACEPTADA");s.fallo=new IllegalArgumentException("No quedan plazas.");
        s.ejecutar(true);assertEquals(0,s.guardados);assertNotNull(s.vista);
        assertEquals(Map.of(1L,EstadoInscripcion.ACEPTADA),s.atributos.get("decisiones"));
        assertEquals("No quedan plazas.",s.atributos.get("error"));
    }
}
