<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,web.BusquedaServlet.Resultado" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<Resultado> resultados = (List<Resultado>) request.getAttribute("resultados");
    String tituloPagina = "Resultados de búsqueda";
    String bajadaPagina = resultados.size() + " resultados";
    String paginaActiva = "busqueda";
%>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Búsqueda | edEXT</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5"></head><body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
<form class="filtros-busqueda panel" method="get" action="${pageContext.request.contextPath}/busqueda">
    <input type="hidden" name="q" value="<%= escapar(request.getParameter("q")) %>">
    <input type="hidden" name="instituto" value="<%= escapar(request.getParameter("instituto")) %>">
    <input type="hidden" name="categoria" value="<%= escapar(request.getParameter("categoria")) %>">
    <fieldset class="selector-cuenta"><legend>Tipo</legend><div class="selector-opciones">
        <label><input type="radio" name="tipo" value="" <%= !"cursos".equals(request.getParameter("tipo")) && !"programas".equals(request.getParameter("tipo")) ? "checked" : "" %>><span>Todos</span></label>
        <label><input type="radio" name="tipo" value="cursos" <%= "cursos".equals(request.getParameter("tipo")) ? "checked" : "" %>><span>Cursos</span></label>
        <label><input type="radio" name="tipo" value="programas" <%= "programas".equals(request.getParameter("tipo")) ? "checked" : "" %>><span>Programas</span></label>
    </div></fieldset>
    <div class="campo"><label for="orden">Ordenar por</label><select id="orden" name="orden">
        <option value="nombre">Nombre (A–Z)</option><option value="fecha" <%= "fecha".equals(request.getParameter("orden")) ? "selected" : "" %>>Publicación (más reciente)</option>
    </select></div><button class="boton" type="submit">Aplicar</button>
    <% if (request.getParameter("instituto") != null && !request.getParameter("instituto").isBlank()) { %><p>Instituto: <strong><%= escapar(request.getParameter("instituto")) %></strong></p><% } %>
    <% if (request.getParameter("categoria") != null && !request.getParameter("categoria").isBlank()) { %><p>Categoría: <strong><%= escapar(request.getParameter("categoria")) %></strong></p><% } %>
    <a href="${pageContext.request.contextPath}/busqueda">Limpiar filtros</a>
</form>
<% if (resultados.isEmpty()) { %><div class="aviso">No hay resultados para esta búsqueda.</div><% } %>
<section class="oferta-lista" aria-label="Resultados">
<% for (Resultado resultado : resultados) { %>
    <article class="oferta-fila"><img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Sin imagen"><div>
        <span class="categoria-chip"><%= resultado.tipo().equals("curso") ? "Curso" : "Programa" %></span>
        <h2><%= escapar(resultado.nombre()) %></h2><p><%= escapar(resultado.descripcion()) %></p>
        <p class="texto-secundario">Publicado: <%= fecha(resultado.publicacion()) %></p>
        <a class="boton boton-pequeno" href="${pageContext.request.contextPath}/<%= resultado.tipo() %>?nombre=<%= parametro(resultado.nombre()) %>">Ver detalle</a>
    </div></article>
<% } %>
</section></div></main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body></html>
