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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<%@ include file="/WEB-INF/vistas/fragmentos/bienvenida.jspf" %>

<main id="contenido" class="contenedor contenido">
    <div class="aviso">
        <% if (usuarioSesion != null) { %>
            <p>Sesión iniciada como <strong><%= escapar(usuarioSesion.getNick()) %></strong>.</p>
        <% } else { %>
            <p><a href="${pageContext.request.contextPath}/login">Iniciá sesión</a> para acceder a las funcionalidades de la plataforma.</p>
        <% } %>
    </div>
    <h2>Nuestra oferta de formación</h2>
    <section class="tarjetas" aria-label="Oferta de formación">
        <article class="tarjeta">
            <span class="icono-oferta" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7c5-2 8-1 12 1 4-2 7-3 12-1v20c-5-2-8-1-12 1-4-2-7-3-12-1V7Z"/><path d="M16 8v20"/></svg></span>
            <span class="numero">${cantidadCursos}</span><h3>Cursos</h3>
            <p>Propuestas para ampliar tus conocimientos.</p>
            <a class="boton" href="${pageContext.request.contextPath}/cursos">Ver cursos →</a>
        </article>
        <article class="tarjeta">
            <span class="icono-oferta" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 12 13-7 13 7-13 7-13-7Z"/><path d="M8 15v9c5 4 11 4 16 0v-9M29 12v12"/></svg></span>
            <span class="numero">${cantidadProgramas}</span><h3>Programas de formación</h3>
            <p>Recorridos formativos para seguir creciendo.</p>
            <a class="boton" href="${pageContext.request.contextPath}/programas">Ver programas →</a>
        </article>
        <article class="tarjeta">
            <span class="icono-oferta" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 10V6h10l4 4h12v17H3V10Z"/><path d="M3 11h26"/></svg></span>
            <span class="numero">${cantidadCategorias}</span><h3>Categorías</h3>
            <p>Áreas para explorar según tus intereses.</p>
        </article>
    </section>
    <% if (usuarioSesion == null) { %>
        <p class="invitacion">¿Todavía no tenés cuenta? <a href="${pageContext.request.contextPath}/registro">Creá tu cuenta</a></p>
    <% } %>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
