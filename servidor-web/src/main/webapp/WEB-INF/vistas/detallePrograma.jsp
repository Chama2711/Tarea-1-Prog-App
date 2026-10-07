<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    ProgramaFormacion programa = (ProgramaFormacion) request.getAttribute("programa");
    String tituloPagina = programa.getNombre();
    String bajadaPagina = programa.getDescripcion();
    String paginaActiva = "programas";
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
    <section class="panel" aria-label="Información del programa">
        <h1><%= escapar(programa.getNombre()) %></h1><p><%= escapar(programa.getDescripcion()) %></p>
        <div class="detalle-datos">
            <div class="dato"><span>Fecha de inicio</span><strong><%= fecha(programa.getFechaInicio()) %></strong></div>
            <div class="dato"><span>Fecha de finalización</span><strong><%= fecha(programa.getFechaFin()) %></strong></div>
            <div class="dato"><span>Fecha de alta</span><strong><%= fecha(programa.getFechaAlta()) %></strong></div>
            <div class="dato"><span>Cursos</span><strong><%= programa.getCursos() == null ? 0 : programa.getCursos().size() %></strong></div>
        </div>
    </section>
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
