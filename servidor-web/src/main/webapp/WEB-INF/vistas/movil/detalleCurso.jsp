<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,web.soap.generado.DtCursoConsulta,web.soap.generado.DtEdicionResumen" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "cursos";
    DtCursoConsulta curso = (DtCursoConsulta) request.getAttribute("curso");
    List<String> categorias = (List<String>) request.getAttribute("categorias");
    List<DtEdicionResumen> ediciones = (List<DtEdicionResumen>) request.getAttribute("ediciones");
    double promedioValoraciones = curso.getPromedioValoraciones();
    long totalValoraciones = curso.getTotalValoraciones();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(curso.getNombre()) %> | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <a class="volver" style="display: inline-block; margin-bottom: 0.85rem;"
       href="${pageContext.request.contextPath}/movil/cursos">&larr; Volver a cursos</a>

    <article class="movil-tarjeta" style="margin-bottom: 1rem;">
        <% if (curso.isTieneImagen()) { %>
            <img class="movil-tarjeta-img" style="height: 210px;"
                 src="${pageContext.request.contextPath}/imagen-curso?nombre=<%= parametro(curso.getNombre()) %>"
                 alt="Imagen de <%= escapar(curso.getNombre()) %>">
        <% } else { %>
            <img class="movil-tarjeta-img" style="height: 210px;"
                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                 alt="Curso sin imagen">
        <% } %>

        <div class="movil-tarjeta-cuerpo">
            <div style="display: flex; flex-wrap: wrap; gap: 0.4rem; align-items: center;">
                <span class="movil-chip">Curso</span>
                <% if (curso.getNombreInstituto() != null) { %>
                    <span class="texto-secundario" style="font-size: 0.86rem; font-weight: 600;">
                        <%= escapar(curso.getNombreInstituto()) %>
                    </span>
                <% } %>
            </div>

            <h1 style="font-size: 1.4rem; margin: 0;"><%= escapar(curso.getNombre()) %></h1>
            <%@ include file="/WEB-INF/vistas/fragmentos/estrellas.jspf" %>
            <p style="margin: 0.25rem 0;"><%= escapar(curso.getDescripcion()) %></p>

            <% String urlCurso = curso.getUrl();
               if (urlCurso != null && (urlCurso.startsWith("https://") || urlCurso.startsWith("http://"))) { %>
                <p style="margin: 0.2rem 0;">
                    <a href="<%= escapar(urlCurso) %>" target="_blank" rel="noopener"
                       style="color: var(--edext-cyan-hover); font-weight: 600;">Ir al sitio del curso</a>
                </p>
            <% } %>

            <div class="movil-tabla-wrap" style="margin-top: 0.5rem;">
                <table class="movil-tabla">
                    <tbody>
                        <tr>
                            <th>Duración</th>
                            <td><%= escapar(curso.getDuracion()) %></td>
                        </tr>
                        <tr>
                            <th>Horas</th>
                            <td><%= curso.getCantidadHoras() %> horas</td>
                        </tr>
                        <tr>
                            <th>Créditos</th>
                            <td><%= curso.getCreditos() %></td>
                        </tr>
                        <tr>
                            <th>Fecha de registro</th>
                            <td><%= fecha(curso.getFechaRegistro()) %></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <h2 style="font-size: 1.05rem; margin: 0.7rem 0 0.2rem;">Categorías</h2>
            <% if (categorias == null || categorias.isEmpty()) { %>
                <p class="texto-secundario" style="margin: 0;">Sin categorías asociadas.</p>
            <% } else { %>
                <div style="display: flex; flex-wrap: wrap; gap: 0.4rem;">
                    <% for (String cat : categorias) { %>
                        <span class="movil-chip"><%= escapar(cat) %></span>
                    <% } %>
                </div>
            <% } %>

            <h2 style="font-size: 1.05rem; margin: 0.7rem 0 0.2rem;">Previas</h2>
            <% if (curso.getPrevia().isEmpty()) { %>
                <p class="texto-secundario" style="margin: 0;">Este curso no tiene previas.</p>
            <% } else { %>
                <ul style="margin: 0; padding-left: 1.2rem;">
                    <% for (String previa : curso.getPrevia()) { %>
                        <li>
                            <a href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(previa) %>"
                               style="color: var(--edext-cyan-hover); font-weight: 600;">
                                <%= escapar(previa) %>
                            </a>
                        </li>
                    <% } %>
                </ul>
            <% } %>
        </div>
    </article>

    <section class="movil-panel" aria-labelledby="titulo-ediciones-movil">
        <h2 id="titulo-ediciones-movil" style="font-size: 1.15rem; margin: 0 0 0.75rem;">Ediciones del curso</h2>
        <% if (ediciones == null || ediciones.isEmpty()) { %>
            <p class="texto-secundario" style="margin: 0;">No hay ediciones registradas para este curso.</p>
        <% } else { %>
            <div class="movil-tabla-wrap">
                <table class="movil-tabla">
                    <thead>
                        <tr>
                            <th>Edición</th>
                            <th>Inicio</th>
                            <th>Fin</th>
                            <th>Cupo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (DtEdicionResumen ed : ediciones) { %>
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/movil/edicion?nombre=<%= parametro(ed.getNombre()) %>">
                                        <%= escapar(ed.getNombre()) %>
                                    </a>
                                </td>
                                <td><%= fecha(ed.getFechaInicio()) %></td>
                                <td><%= fecha(ed.getFechaFin()) %></td>
                                <td><%= ed.getCupo() == -1 ? "Sin límite" : ed.getCupo() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </section>

    <section class="movil-panel" aria-labelledby="titulo-programas-movil">
        <h2 id="titulo-programas-movil" style="font-size: 1.15rem; margin: 0 0 0.6rem;">Programas de formación</h2>
        <% if (curso.getPrograma().isEmpty()) { %>
            <p class="texto-secundario" style="margin: 0;">Este curso no integra programas de formación.</p>
        <% } else { %>
            <ul style="margin: 0; padding-left: 1.2rem;">
                <% for (String prog : curso.getPrograma()) { %>
                    <li><%= escapar(prog) %></li>
                <% } %>
            </ul>
        <% } %>
    </section>
</main>
</body>
</html>