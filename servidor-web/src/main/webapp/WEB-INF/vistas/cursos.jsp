<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    String tituloPagina = "Encontrá tu próximo curso.";
    String bajadaPagina = "Explorá propuestas para ampliar tus conocimientos.";
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
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
    <h2>Cursos disponibles</h2>
    <% if (cursos == null || cursos.isEmpty()) { %>
        <div class="aviso">No hay cursos disponibles actualmente.</div>
    <% } else { %>
        <section class="grilla-cursos" aria-label="Cursos disponibles">
            <% for (Curso curso : cursos) { %>
                <article class="curso-tarjeta">
                    <img class="imagen-oferta" src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Curso sin imagen">
                    <h2><%= escapar(curso.getNombre()) %></h2>
                    <p class="curso-descripcion"><%= escapar(curso.getDescripcion()) %></p>
                    <div class="curso-datos">
                        <span><strong>Duración</strong><%= escapar(curso.getDuracion()) %></span>
                        <span><strong>Créditos</strong><%= curso.getCreditos() %></span>
                    </div>
                    <a class="enlace-detalle" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(curso.getNombre()) %>" aria-label="Ver detalle de <%= escapar(curso.getNombre()) %>">Ver detalle →</a>
                </article>
            <% } %>
        </section>
    <% } %>
    <a class="volver volver-final" href="${pageContext.request.contextPath}/inicio">← Volver al inicio</a>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
