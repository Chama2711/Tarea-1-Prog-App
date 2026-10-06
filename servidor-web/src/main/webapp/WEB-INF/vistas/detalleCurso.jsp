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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<%@ include file="/WEB-INF/vistas/fragmentos/bienvenida.jspf" %>

<main id="contenido" class="contenedor contenido">
    <a class="volver" href="${pageContext.request.contextPath}/cursos">← Volver a cursos</a>
    <section class="panel" aria-label="Información del curso">
        <h2>Información del curso</h2>
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
    </section>
    <section class="panel" aria-labelledby="titulo-ediciones">
        <h2 id="titulo-ediciones">Ediciones del curso</h2>
        <% if (ediciones == null || ediciones.isEmpty()) { %>
            <p class="texto-secundario">No hay ediciones disponibles para este curso.</p>
        <% } else { %>
            <div class="ediciones">
                <% for (EdicionCurso edicion : ediciones) { %>
                    <article class="edicion">
                        <h3><%= escapar(edicion.getNombre()) %></h3>
                        <div class="edicion-datos">
                            <span><strong>Inicio:</strong> <%= fecha(edicion.getFechaInicio()) %></span>
                            <span><strong>Fin:</strong> <%= fecha(edicion.getFechaFin()) %></span>
                            <span><strong>Cupo:</strong> <%= edicion.getCupo() == -1 ? "Sin límite" : edicion.getCupo() %></span>
                        </div>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
