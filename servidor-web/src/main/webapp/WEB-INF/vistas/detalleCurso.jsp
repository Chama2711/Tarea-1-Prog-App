<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    Curso curso = (Curso) request.getAttribute("curso");
    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
    List<EdicionCurso> ediciones = (List<EdicionCurso>) request.getAttribute("ediciones");
    String tituloPagina = curso.getNombre();
    String bajadaPagina = curso.getDescripcion();
    String paginaActiva = "cursos";
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

    <a class="volver" href="${pageContext.request.contextPath}/cursos">← Volver a cursos</a>
    <div class="consulta-curso">
    <section class="panel" aria-label="Información del curso">
        <img class="imagen-detalle" src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Curso sin imagen">
        <h1><%= escapar(curso.getNombre()) %></h1>
        <p><%= escapar(curso.getDescripcion()) %></p>
        <% String urlCurso = curso.getUrl(); if (urlCurso != null && (urlCurso.startsWith("https://") || urlCurso.startsWith("http://"))) { %>
        <p><a href="<%= escapar(urlCurso) %>" target="_blank" rel="noopener">Acceder al sitio del curso</a></p><% } %>
        <div class="detalle-datos">
            <div class="dato"><span>Duración</span><strong><%= escapar(curso.getDuracion()) %></strong></div>
            <div class="dato"><span>Carga horaria</span><strong><%= curso.getCantidadHoras() %> horas</strong></div>
            <div class="dato"><span>Créditos</span><strong><%= curso.getCreditos() %></strong></div>
            <div class="dato"><span>Fecha de alta</span><strong><%= fecha(curso.getFechaRegistro()) %></strong></div>
        </div>
        <% if (curso.getInstituto() != null) { %>
            <div class="instituto"><%= escapar(curso.getInstituto().getNombre()) %></div>
        <% } %>
        <% if (categorias == null || categorias.isEmpty()) { %>
            <p class="texto-secundario">Este curso no tiene categorías asociadas.</p>
        <% } else { %>
            <div class="categorias" aria-label="Categorías del curso">
                <% for (Categoria categoria : categorias) { %>
                    <span class="categoria-chip"><%= escapar(categoria.getNombre()) %></span>
                <% } %>
            </div>
        <% } %>
        <h3>Previas</h3>
        <% if (curso.getPrevias().isEmpty()) { %><p class="texto-secundario">Este curso no tiene previas.</p><% } %>
        <ul><% for (Curso previa : curso.getPrevias()) { %><li><a href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(previa.getNombre()) %>"><%= escapar(previa.getNombre()) %></a></li><% } %></ul>
    </section>
    <aside class="relaciones-curso">
    <section class="panel" aria-labelledby="titulo-ediciones">
        <h2 id="titulo-ediciones">Ediciones del curso</h2>
        <% if (ediciones == null || ediciones.isEmpty()) { %>
            <p class="texto-secundario">No hay ediciones disponibles para este curso.</p>
        <% } else { %>
            <div class="ediciones">
                <% for (EdicionCurso edicion : ediciones) { %>
                    <article class="edicion edicion-miniatura">
                        <img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="" width="100" height="76">
                        <div>
                        <h3><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(edicion.getNombre()) %>"><%= escapar(edicion.getNombre()) %></a></h3>
                        <div class="edicion-datos">
                            <span><strong>Inicio:</strong> <%= fecha(edicion.getFechaInicio()) %></span>
                            <span><strong>Fin:</strong> <%= fecha(edicion.getFechaFin()) %></span>
                            <span><strong>Cupo:</strong> <%= edicion.getCupo() == -1 ? "Sin límite" : edicion.getCupo() %></span>
                        </div>
                        </div>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>
    <section class="panel"><h2>Programas de formación</h2>
        <% if (curso.getProgramasFormacion().isEmpty()) { %><p class="texto-secundario">Este curso no integra programas.</p><% } %>
        <ul class="lista-docentes"><% for (ProgramaFormacion programaCurso : curso.getProgramasFormacion()) { %><li><a href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(programaCurso.getNombre()) %>"><%= escapar(programaCurso.getNombre()) %></a></li><% } %></ul>
    </section></aside></div>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
