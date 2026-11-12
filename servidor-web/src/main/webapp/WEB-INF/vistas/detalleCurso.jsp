<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,web.soap.generado.DtCursoConsulta,web.soap.generado.DtEdicionResumen" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DtCursoConsulta curso = (DtCursoConsulta) request.getAttribute("curso");
    List<String> categorias = (List<String>) request.getAttribute("categorias");
    List<DtEdicionResumen> ediciones = (List<DtEdicionResumen>) request.getAttribute("ediciones");
    String tituloPagina = curso.getNombre();
    String bajadaPagina = curso.getDescripcion();
    String paginaActiva = "cursos";
    double promedioValoraciones = curso.getPromedioValoraciones();
    long totalValoraciones = curso.getTotalValoraciones();
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
       <% if (curso.isTieneImagen()) { %>

           <img class="imagen-detalle"
                src="${pageContext.request.contextPath}/imagen-curso?nombre=<%= parametro(curso.getNombre()) %>"
                alt="Imagen del curso <%= escapar (curso.getNombre()) %>">

       <% } else { %>

          <img class="imagen-detalle"
               src="${pageContext.request.contextPath}/imagenes/formacion.svg"
               alt="Curso sin imagen">
       <% } %>


        <h1><%= escapar(curso.getNombre()) %></h1>
        <%@ include file="/WEB-INF/vistas/fragmentos/estrellas.jspf" %>
        <p><%= escapar(curso.getDescripcion()) %></p>
        <% String urlCurso = curso.getUrl(); if (urlCurso != null && (urlCurso.startsWith("https://") || urlCurso.startsWith("http://"))) { %>
        <p><a href="<%= escapar(urlCurso) %>" target="_blank" rel="noopener">Acceder al sitio del curso</a></p><% } %>
        <div class="detalle-datos">
            <div class="dato"><span>Duración</span><strong><%= escapar(curso.getDuracion()) %></strong></div>
            <div class="dato"><span>Carga horaria</span><strong><%= curso.getCantidadHoras() %> horas</strong></div>
            <div class="dato"><span>Créditos</span><strong><%= curso.getCreditos() %></strong></div>
            <div class="dato"><span>Fecha de alta</span><strong><%= fecha(curso.getFechaRegistro()) %></strong></div>
        </div>
        <% if (curso.getNombreInstituto() != null) { %>
            <div class="instituto"><%= escapar(curso.getNombreInstituto()) %></div>
        <% } %>
        <% if (categorias == null || categorias.isEmpty()) { %>
            <p class="texto-secundario">Este curso no tiene categorías asociadas.</p>
        <% } else { %>
            <div class="categorias" aria-label="Categorías del curso">
                <% for (String categoria : categorias) { %>
                    <span class="categoria-chip"><%= escapar(categoria) %></span>
                <% } %>
            </div>
        <% } %>
        
        <% if (totalValoraciones > 0) { %>
            <section class="panel" aria-labelledby="titulo-puntajes">
                <h2 id="titulo-puntajes">Distribución de puntuaciones</h2>
                <ul class="distribucion-valoraciones">
                    <% for (int puntaje = 5; puntaje >= 1; puntaje--) {
                        long cantidad = curso.getCantidadPorPuntuacion().get(puntaje - 1);
                    %>
                        <li>
                            <span><%= puntaje %> <%= puntaje == 1 ? "estrella" : "estrellas" %></span>
                            <span class="barra-valoracion" aria-hidden="true">
                                <span style="width: <%= cantidad * 100.0 / totalValoraciones %>%"></span>
                            </span>
                            <span><%= cantidad %> <%= cantidad == 1 ? "voto" : "votos" %></span>
                        </li>
                    <% } %>
                </ul>
            </section>
        <% } %>
        <% if (usuarioSesion != null && usuarioSesion.getRol() == DTAutenticacion.Rol.ESTUDIANTE) { %>
            <section class="panel" aria-labelledby="titulo-valoracion">
                <h2 id="titulo-valoracion">Valoración de cursos</h2>
                <p class="texto-secundario">Elegí uno de los cursos en los que participás para consultar su promedio y dejar tu puntuación.</p>
                <a class="boton" href="${pageContext.request.contextPath}/valorar-curso">Ver cursos para valorar</a>
            </section>

        <% } %>

        <h3>Previas</h3>
        <% if (curso.getPrevia().isEmpty()) { %><p class="texto-secundario">Este curso no tiene previas.</p><% } %>
        <ul><% for (String previa : curso.getPrevia()) { %><li><a href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(previa) %>"><%= escapar(previa) %></a></li><% } %></ul>
    </section>
    <aside class="relaciones-curso">
    <section class="panel" aria-labelledby="titulo-ediciones">
        <h2 id="titulo-ediciones">Ediciones del curso</h2>
        <% if (ediciones == null || ediciones.isEmpty()) { %>
            <p class="texto-secundario">No hay ediciones disponibles para este curso.</p>
        <% } else { %>
            <div class="ediciones">
                <% for (DtEdicionResumen edicion : ediciones) { %>
                    <article class="edicion edicion-miniatura">
                        <img src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(edicion.getNombre()) %>" alt="Imagen de la edición <%= escapar(edicion.getNombre()) %>" width="100" height="76">
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
        <% if (curso.getPrograma().isEmpty()) { %><p class="texto-secundario">Este curso no integra programas.</p><% } %>
        <ul class="lista-docentes"><% for (String programaCurso : curso.getPrograma()) { %><li><a href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(programaCurso) %>"><%= escapar(programaCurso) %></a></li><% } %></ul>
    </section></aside></div>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
