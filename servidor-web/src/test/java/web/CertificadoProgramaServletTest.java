package web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.*;
import java.util.*;
import logica.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CertificadoProgramaServletTest {
    private static <T> T proxy(Class<T> tipo, InvocationHandler h) {
        return tipo.cast(Proxy.newProxyInstance(tipo.getClassLoader(),new Class<?>[]{tipo},h));
    }
    private static class Escenario {
        DTAutenticacion usuario=new DTAutenticacion("ana","Ana",DTAutenticacion.Rol.ESTUDIANTE);
        final Map<String,String> parametros=new HashMap<>(),cabeceras=new HashMap<>();
        final Map<String,Object> atributos=new HashMap<>();
        final ByteArrayOutputStream salida=new ByteArrayOutputStream();
        int estado=200,llamadas; String vista,redireccion,actor,programa;
        RuntimeException error;
        byte[] pdf="%PDF-1.7 prueba".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        web.soap.ClienteCatalogoSoap cliente;
        final IServidorCentral central=proxy(IServidorCentral.class,(p,m,a)->{
            llamadas++;
            if(m.getName().equals("listarNombresProgramas"))return List.of("Programa ñ");
            if(m.getName().equals("generarCertificadoPrograma")){
                actor=(String)a[0];programa=(String)a[1];if(error!=null)throw error;return pdf;
            }
            throw new AssertionError(m.getName());
        });
        final ServletContext contexto=proxy(ServletContext.class,(p,m,a)->switch(m.getName()){
            case "getAttribute" -> {assertTrue(((String)a[0]).endsWith(".clienteCatalogo"));yield cliente;}
            case "log" -> null;
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletRequest request=proxy(HttpServletRequest.class,(p,m,a)->switch(m.getName()){
            case "getSession" -> usuario==null?null:proxy(HttpSession.class,(q,n,b)->usuario);
            case "getParameter" -> parametros.get(a[0]);
            case "getContextPath" -> "/edext";
            case "setAttribute" -> {atributos.put((String)a[0],a[1]);yield null;}
            case "getRequestDispatcher" -> proxy(RequestDispatcher.class,(q,n,b)->{vista=(String)a[0];return null;});
            default -> throw new AssertionError(m.getName());
        });
        final HttpServletResponse response=proxy(HttpServletResponse.class,(p,m,a)->{
            switch(m.getName()){
                case "setHeader":cabeceras.put((String)a[0],(String)a[1]);break;
                case "setContentType":cabeceras.put("Content-Type",(String)a[0]);break;
                case "setContentLength":assertEquals(pdf.length,a[0]);break;
                case "setStatus":case "sendError":estado=(Integer)a[0];break;
                case "sendRedirect":estado=302;redireccion=(String)a[0];break;
                case "getOutputStream":return new ServletOutputStream(){
                    public void write(int b){salida.write(b);}
                    public boolean isReady(){return true;}
                    public void setWriteListener(WriteListener w){}
                };
                default:throw new AssertionError(m.getName());
            }return null;
        });
        void ejecutar()throws Exception{
            var servlet=new CertificadoProgramaServlet();
            servlet.init(proxy(ServletConfig.class,(p,m,a)->m.getName().equals("getServletContext")?contexto:null));
            try(var soap=new CatalogoSoapPrueba(central)){cliente=soap.cliente;servlet.doGet(request,response);}
        }
        void descargar(){parametros.put("descargar","1");parametros.put("programa","Programa ñ");}
    }
    @Test public void visitanteYDocenteNoPuedenDescargar()throws Exception{
        var s=new Escenario();s.usuario=null;s.descargar();s.ejecutar();assertEquals(302,s.estado);assertEquals(0,s.llamadas);
        s=new Escenario();s.usuario=new DTAutenticacion("doc","Docente",DTAutenticacion.Rol.DOCENTE);
        s.descargar();s.ejecutar();assertEquals(403,s.estado);assertEquals(0,s.llamadas);
    }
    @Test public void formularioListaProgramasSinEmitirCertificados()throws Exception{
        var s=new Escenario();s.ejecutar();assertEquals(200,s.estado);
        assertEquals(List.of("Programa ñ"),s.atributos.get("programasCertificado"));
        assertEquals("/WEB-INF/vistas/certificadoPrograma.jsp",s.vista);assertEquals(0,s.salida.size());
    }
    @Test public void descargaUsaLaSesionYEntregaLosBytesPorSoap()throws Exception{
        var s=new Escenario();s.descargar();s.parametros.put("nick","otra-persona");s.ejecutar();
        assertEquals("ana",s.actor);assertEquals("Programa ñ",s.programa);assertArrayEquals(s.pdf,s.salida.toByteArray());
        assertEquals("application/pdf",s.cabeceras.get("Content-Type"));
        assertEquals("no-store",s.cabeceras.get("Cache-Control"));
        assertTrue(s.cabeceras.get("Content-Disposition").startsWith("attachment;"));
    }
    @Test public void cursoPendienteMuestraElMotivoSinEmitirPdf()throws Exception{
        var s=new Escenario();s.descargar();s.error=new IllegalArgumentException("Falta aprobar Curso ñ.");s.ejecutar();
        assertEquals(400,s.estado);assertEquals("Falta aprobar Curso ñ.",s.atributos.get("errorCertificado"));
        assertEquals(0,s.salida.size());assertNotNull(s.vista);
    }
    @Test public void programaInexistenteYAccesoRechazadoNoDevuelvenPdf()throws Exception{
        var s=new Escenario();s.descargar();s.error=new RecursoNoEncontradoException("No existe");s.ejecutar();assertEquals(404,s.estado);
        s=new Escenario();s.descargar();s.error=new SecurityException("Estudiante inválido");s.ejecutar();assertEquals(403,s.estado);
    }
    @Test public void falloTecnicoOPdfVacioSeInformaComoServicioNoDisponible()throws Exception{
        var s=new Escenario();s.descargar();s.error=new IllegalStateException("jdbc:detalle privado");s.ejecutar();assertEquals(503,s.estado);
        s=new Escenario();s.descargar();s.pdf=new byte[0];s.ejecutar();assertEquals(503,s.estado);assertEquals(0,s.salida.size());
    }
}
