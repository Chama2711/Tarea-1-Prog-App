<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    ProgramaFormacion programa = (ProgramaFormacion) request.getAttribute("programa");
    String tituloPagina = programa.getNombre();
    String bajadaPagina = programa.getDescripcion();
    String paginaActiva = "programas";
    
    DTAutenticacion usuarioPrograma =
        (DTAutenticacion) session.getAttribute("usuario");

    boolean esEstudiante =
        usuarioPrograma != null
        && usuarioPrograma.getRol() == DTAutenticacion.Rol.ESTUDIANTE;
    
    boolean esDocente =
        usuarioPrograma != null
        && usuarioPrograma.getRol() == DTAutenticacion.Rol.DOCENTE;
    
    
    LocalDate hoy = LocalDate.now();

    boolean inscripcionAbierta =
        !hoy.isAfter(programa.getFechaFin())
        && !hoy.isAfter(
                programa.getFechaInicio().plusDays(14)
    );
    
    boolean yaInscripto =
        Boolean.TRUE.equals(request.getAttribute("yaInscripto"));
    
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>


<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">

    <a class="volver" href="${pageContext.request.contextPath}/programas">← Volver a programas</a>
    
    <%
        String resultadoInscripcion = request.getParameter("inscripcion");
        String errorInscripcion = request.getParameter("errorInscripcion");
    %>
    
    <%
    String resultadoCurso = request.getParameter("cursoAgregado");
    %>

    <% if ("ok".equals(resultadoCurso)) { %>
        <div class="aviso" role="status">
            El curso se agregó correctamente al programa de formación.
        </div>
    <% } %>
    
    

    <% if ("ok".equals(resultadoInscripcion)) { %>
        <div class="aviso" role="status">
            Te inscribiste correctamente al programa de formación.
        </div>
    <% } %>

    <% if (errorInscripcion != null && !errorInscripcion.isBlank()) { %>
        <div class="aviso" role="alert">
            <%= escapar(errorInscripcion) %>
        </div>
    <% } %>
    

    <section class="panel" aria-label="Información del programa">

            <h1><%= escapar(programa.getNombre()) %></h1>
            <p><%= escapar(programa.getDescripcion()) %></p>

            <% if (programa.getImagen() != null && !programa.getImagen().isBlank()) { %>
                <img class="imagen-detalle"
                     src="${pageContext.request.contextPath}/imagen-programa?nombre=<%= parametro(programa.getNombre()) %>"
                     alt="Imagen del programa <%= escapar(programa.getNombre()) %>">
            <% } else { %>
                <img class="imagen-detalle"
                     src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                     alt="Programa sin imagen">
            <% } %>

            <div class="detalle-datos">
            <div class="dato"><span>Fecha de inicio</span><strong><%= fecha(programa.getFechaInicio()) %></strong></div>
            <div class="dato"><span>Fecha de finalización</span><strong><%= fecha(programa.getFechaFin()) %></strong></div>
            <div class="dato"><span>Fecha de alta</span><strong><%= fecha(programa.getFechaAlta()) %></strong></div>
            <div class="dato"><span>Cursos</span><strong><%= programa.getCursos() == null ? 0 : programa.getCursos().size() %></strong></div>
        </div>
    </section>
        
<% if (esEstudiante) { %>
    <section class="panel" aria-label="Inscripción al programa">
        <h2>Inscripción al programa</h2>

        <% if (yaInscripto) { %>

            <div class="aviso" role="status">
                Ya estás inscripto en este programa de formación.
            </div>

        <% } else if (inscripcionAbierta) { %>

            <form method="post"
                  action="<%= request.getContextPath() %>/inscripcion-programa">

                <input type="hidden"
                       name="programa"
                       value="<%= escapar(programa.getNombre()) %>">

                <button type="submit" class="boton">
                    Inscribirme al programa
                </button>
            </form>

        <% } else { %>

            <div class="aviso" role="status">
                El período de inscripción a este programa ya finalizó.
            </div>

        <% } %>
    </section>
<% } %>
 
<% if (esDocente) { %>
    <section class="panel" aria-label="Administración del programa">
        <h2>Administrar programa</h2>

        <p>Podés agregar cursos a este programa de formación.</p>

        <a class="boton"
           href="<%= request.getContextPath() %>/agregar-curso-programa?programa=<%= parametro(programa.getNombre()) %>">
            Agregar curso al programa
        </a>
    </section>
<% } %>

    <section aria-labelledby="titulo-cursos-programa">
        <h2 id="titulo-cursos-programa">Cursos del programa</h2>
        <% if (programa.getCursos() == null || programa.getCursos().isEmpty()) { %>
            <div class="aviso">Este programa todavía no tiene cursos asociados.</div>
        <% } else { %>
            <div class="cursos-programa">
                <% for (Curso curso : programa.getCursos().values()) { %>
                    <article class="curso-programa">
                        <div><h3><%= escapar(curso.getNombre()) %></h3><p><%= escapar(curso.getDescripcion()) %></p></div>
                        <a class="enlace-detalle" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(curso.getNombre()) %>" aria-label="Ver curso <%= escapar(curso.getNombre()) %>">Ver curso →</a>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>
       
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
