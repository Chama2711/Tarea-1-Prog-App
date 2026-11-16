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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=1">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <a class="volver" style="display: inline-block; margin-bottom: 0.85rem;"
       href="${pageContext.request.contextPath}/movil/cursos">&larr; Volver a cursos</a>

    <article class="movil-tarjeta" style="margin-bottom: 1rem;">
        <% if (curso.isTieneImagen()) { %>
            <img class="movil-tarjeta-img" style="height: 200px;"
                 src="${pageContext.request.contextPath}/imagen-curso?nombre=<%= parametro(curso.getNombre()) %>"
                 alt="Imagen de <%= escapar(curso.getNombre()) %>">
        <% } else { %>
            <img class="movil-tarjeta-img" style="height: 200px;"
                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                 alt="Curso sin imagen">
        <% } %>

        <div class="movil-tarjeta-cuerpo">
            <% if (curso.getNombreInstituto() != null) { %>
                <span class="instituto" style="align-self: flex-start;"><%= escapar(curso.getNombreInstituto()) %></span>
            <% } %>
            <h1 style="font-size: 1.35rem; margin: 0;"><%= escapar(curso.getNombre()) %></h1>
            <%@ include file="/WEB-INF/vistas/fragmentos/estrellas.jspf" %>
            <p style="margin: 0.25rem 0;"><%= escapar(curso.getDescripcion()) %></p>

            <% String urlCurso = curso.getUrl();
               if (urlCurso != null && (urlCurso.startsWith("https://") || urlCurso.startsWith("http://"))) { %>
                <p style="margin: 0.25rem 0;">
                    <a href="<%= escapar(urlCurso) %>" target="_blank" rel="noopener">Acceder al sitio del curso</a>
                </p>
            <% } %>

            <div class="movil-datos-grid">
                <div class="movil-dato-item">
                    <span>Duración</span>
                    <strong><%= escapar(curso.getDuracion()) %></strong>
                </div>
                <div class="movil-dato-item">
                    <span>Carga horaria</span>
                    <strong><%= curso.getCantidadHoras() %> h</strong>
                </div>
                <div class="movil-dato-item">
                    <span>Créditos</span>
                    <strong><%= curso.getCreditos() %></strong>
                </div>
                <div class="movil-dato-item">
                    <span>Fecha de alta</span>
                    <strong><%= fecha(curso.getFechaRegistro()) %></strong>
                </div>
            </div>

            <h2 style="font-size: 1.05rem; margin: 0.5rem 0 0.2rem;">Categorías</h2>
            <% if (categorias == null || categorias.isEmpty()) { %>
                <p class="texto-secundario" style="margin: 0;">Sin categorías asociadas.</p>
            <% } else { %>
                <div class="categorias">
                    <% for (String cat : categorias) { %>
                        <span class="categoria-chip"><%= escapar(cat) %></span>
                    <% } %>
                </div>
            <% } %>

            <h2 style="font-size: 1.05rem; margin: 0.75rem 0 0.2rem;">Previas</h2>
            <% if (curso.getPrevia().isEmpty()) { %>
                <p class="texto-secundario" style="margin: 0;">Este curso no tiene previas.</p>
            <% } else { %>
                <ul style="margin: 0; padding-left: 1.2rem;">
                    <% for (String previa : curso.getPrevia()) { %>
                        <li>
                            <a href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(previa) %>">
                                <%= escapar(previa) %>
                            </a>
                        </li>
                    <% } %>
                </ul>
            <% } %>

            <% if (!curso.getPrograma().isEmpty()) { %>
                <h2 style="font-size: 1.05rem; margin: 0.75rem 0 0.2rem;">Programas que integra</h2>
                <ul style="margin: 0; padding-left: 1.2rem;">
                    <% for (String prog : curso.getPrograma()) { %>
                        <li><%= escapar(prog) %></li>
                    <% } %>
                </ul>
            <% } %>
        </div>
    </article>

    <section class="movil-hero" aria-labelledby="titulo-ediciones-movil">
        <h2 id="titulo-ediciones-movil" style="font-size: 1.15rem; margin-top: 0;">Ediciones del curso</h2>
        <% if (ediciones == null || ediciones.isEmpty()) { %>
            <p class="texto-secundario" style="margin-bottom: 0;">No hay ediciones registradas para este curso.</p>
        <% } else { %>
            <div class="movil-lista" style="gap: 0.75rem;">
                <% for (DtEdicionResumen ed : ediciones) { %>
                    <article class="movil-edicion-item">
                        <div class="movil-edicion-cabecera">
                            <img src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(ed.getNombre()) %>"
                                 alt="Edición <%= escapar(ed.getNombre()) %>">
                            <div>
                                <h3 style="font-size: 1rem; margin: 0 0 0.2rem;">
                                    <a href="${pageContext.request.contextPath}/movil/edicion?nombre=<%= parametro(ed.getNombre()) %>">
                                        <%= escapar(ed.getNombre()) %>
                                    </a>
                                </h3>
                                <span class="texto-secundario" style="font-size: 0.85rem;">
                                    Cupo: <%= ed.getCupo() == -1 ? "Sin límite" : ed.getCupo() %>
                                </span>
                            </div>
                        </div>
                        <div class="texto-secundario" style="font-size: 0.85rem;">
                            <strong>Inicio:</strong> <%= fecha(ed.getFechaInicio()) %> &bull;
                            <strong>Fin:</strong> <%= fecha(ed.getFechaFin()) %>
                        </div>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>
</main>
</body>
</html>