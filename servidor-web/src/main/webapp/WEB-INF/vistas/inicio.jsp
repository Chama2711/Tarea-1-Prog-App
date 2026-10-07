<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%

    String tituloPagina = "Un espacio para seguir aprendiendo.";
    String bajadaPagina = "Cursos y programas de formación para compartir conocimientos y descubrir nuevas oportunidades.";
    String paginaActiva = "inicio";
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
    <section class="oferta-lista" aria-label="Oferta de formación">
    <% for (Curso oferta : (List<Curso>) request.getAttribute("cursos")) { %>
        <article class="oferta-fila"><img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Curso sin imagen"><div><span class="categoria-chip">Curso</span><h2><%= escapar(oferta.getNombre()) %></h2><p><%= escapar(oferta.getDescripcion()) %></p><a class="boton boton-pequeno" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(oferta.getNombre()) %>">Ver detalle</a></div></article>
    <% } %>
    <% for (ProgramaFormacion oferta : (List<ProgramaFormacion>) request.getAttribute("programas")) { %>
        <article class="oferta-fila"><img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Programa sin imagen"><div><span class="categoria-chip">Programa</span><h2><%= escapar(oferta.getNombre()) %></h2><p><%= escapar(oferta.getDescripcion()) %></p><a class="boton boton-pequeno" href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(oferta.getNombre()) %>">Ver detalle</a></div></article>
    <% } %>
    </section>
    <% if (usuarioSesion == null) { %>
        <p class="invitacion">¿Todavía no tenés cuenta? <a href="${pageContext.request.contextPath}/registro">Creá tu cuenta</a></p>
    <% } %>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
