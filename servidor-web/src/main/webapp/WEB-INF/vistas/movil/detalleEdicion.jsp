<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.soap.generado.DtEdicionConsulta,web.soap.generado.DtDocenteResumen" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "cursos";
    DtEdicionConsulta edicion = (DtEdicionConsulta) request.getAttribute("edicion");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(edicion.getNombre()) %> | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=1">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <a class="volver" style="display: inline-block; margin-bottom: 0.85rem;"
       href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>">
        &larr; Volver al curso
    </a>

    <article class="movil-tarjeta" style="margin-bottom: 1rem;">
        <% if (edicion.isTieneImagen()) { %>
            <img class="movil-tarjeta-img" style="height: 200px;"
                 src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(edicion.getNombre()) %>"
                 alt="Imagen de <%= escapar(edicion.getNombre()) %>">
        <% } else { %>
            <img class="movil-tarjeta-img" style="height: 200px;"
                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                 alt="Edición sin imagen">
        <% } %>

        <div class="movil-tarjeta-cuerpo">
            <div style="display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem;">
                <h1 style="font-size: 1.35rem; margin: 0;"><%= escapar(edicion.getNombre()) %></h1>
                <% if (!edicion.isVigente()) { %>
                    <span class="estado-no-vigente">NO VIGENTE</span>
                <% } %>
            </div>

            <p style="margin: 0.25rem 0;">
                <strong>Curso:</strong>
                <a href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>">
                    <%= escapar(edicion.getNombreCurso()) %>
                </a>
            </p>

            <div class="movil-datos-grid">
                <div class="movil-dato-item">
                    <span>Inicio</span>
                    <strong><%= fecha(edicion.getInicio()) %></strong>
                </div>
                <div class="movil-dato-item">
                    <span>Fin</span>
                    <strong><%= fecha(edicion.getFin()) %></strong>
                </div>
                <div class="movil-dato-item">
                    <span>Publicación</span>
                    <strong><%= fecha(edicion.getPublicacion()) %></strong>
                </div>
                <div class="movil-dato-item">
                    <span>Cupo</span>
                    <strong><%= edicion.getCupo() == -1 ? "Sin límite" : edicion.getCupo() %></strong>
                </div>
            </div>
        </div>
    </article>

    <section class="movil-hero" aria-labelledby="titulo-docentes-movil">
        <h2 id="titulo-docentes-movil" style="font-size: 1.15rem; margin-top: 0;">Docentes de la edición</h2>
        <% if (edicion.getDocente().isEmpty()) { %>
            <p class="texto-secundario" style="margin-bottom: 0;">No hay docentes asignados.</p>
        <% } else { %>
            <ul class="lista-docentes" style="margin-bottom: 0;">
                <% for (DtDocenteResumen docente : edicion.getDocente()) { %>
                    <li><%= escapar(docente.getNombreCompleto()) %> (@<%= escapar(docente.getNick()) %>)</li>
                <% } %>
            </ul>
        <% } %>
    </section>
</main>
</body>
</html>