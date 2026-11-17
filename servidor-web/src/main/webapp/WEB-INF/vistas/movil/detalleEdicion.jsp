<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.soap.generado.DtEdicionConsulta,web.soap.generado.DtDocenteResumen" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "ediciones";
    DtEdicionConsulta edicion = (DtEdicionConsulta) request.getAttribute("edicion");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(edicion.getNombre()) %> | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
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
            <img class="movil-tarjeta-img" style="height: 210px;"
                 src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(edicion.getNombre()) %>"
                 alt="Imagen de <%= escapar(edicion.getNombre()) %>">
        <% } else { %>
            <img class="movil-tarjeta-img" style="height: 210px;"
                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                 alt="Edición sin imagen">
        <% } %>

        <div class="movil-tarjeta-cuerpo">
            <div style="display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem;">
                <span class="movil-chip">Edición</span>
                <% if (!edicion.isVigente()) { %>
                    <span class="estado-no-vigente">NO VIGENTE</span>
                <% } %>
            </div>

            <h1 style="font-size: 1.4rem; margin: 0;"><%= escapar(edicion.getNombre()) %></h1>

            <p style="margin: 0.25rem 0;">
                <strong>Curso:</strong>
                <a href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>"
                   style="color: var(--edext-cyan-hover); font-weight: 600;">
                    <%= escapar(edicion.getNombreCurso()) %>
                </a>
            </p>

            <div class="movil-tabla-wrap" style="margin-top: 0.4rem;">
                <table class="movil-tabla">
                    <tbody>
                        <tr>
                            <th>Inicio</th>
                            <td><%= fecha(edicion.getInicio()) %></td>
                        </tr>
                        <tr>
                            <th>Fin</th>
                            <td><%= fecha(edicion.getFin()) %></td>
                        </tr>
                        <tr>
                            <th>Publicación</th>
                            <td><%= fecha(edicion.getPublicacion()) %></td>
                        </tr>
                        <tr>
                            <th>Cupo</th>
                            <td><%= edicion.getCupo() == -1 ? "Sin límite" : edicion.getCupo() %></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </article>

    <section class="movil-panel" aria-labelledby="titulo-docentes-movil">
        <h2 id="titulo-docentes-movil" style="font-size: 1.15rem; margin: 0 0 0.7rem;">Docentes asignados</h2>
        <% if (edicion.getDocente().isEmpty()) { %>
            <p class="texto-secundario" style="margin: 0;">No hay docentes asignados.</p>
        <% } else { %>
            <div class="movil-tabla-wrap">
                <table class="movil-tabla">
                    <thead>
                        <tr>
                            <th>Docente</th>
                            <th>Nickname</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (DtDocenteResumen docente : edicion.getDocente()) { %>
                            <tr>
                                <td><%= escapar(docente.getNombreCompleto()) %></td>
                                <td>@<%= escapar(docente.getNick()) %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </section>
</main>
</body>
</html>