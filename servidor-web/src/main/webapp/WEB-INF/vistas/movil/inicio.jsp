<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "inicio";
    List<String> institutos = (List<String>) request.getAttribute("navInstitutos");
    List<String> categorias = (List<String>) request.getAttribute("navCategorias");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <section class="movil-banner">
        <h1>Bienvenido, <%= escapar(usuarioMovil != null ? usuarioMovil.getNombre() : "") %></h1>
        <p>Explorá la oferta académica de edEXT desde tu dispositivo móvil.</p>
    </section>

    <section class="movil-panel">
        <h2 style="font-size: 1.15rem; margin: 0 0 0.75rem;">Accesos rápidos</h2>
        <div class="movil-grilla">
            <a class="movil-btn-primario movil-btn-ancho"
               href="${pageContext.request.contextPath}/movil/cursos">
                Ver Cursos
            </a>
            <a class="movil-btn-primario movil-btn-ancho"
               style="background-color: var(--edext-nav);"
               href="${pageContext.request.contextPath}/movil/ediciones">
                Ver Ediciones
            </a>
        </div>
    </section>

    <% if (institutos != null && !institutos.isEmpty()) { %>
        <section class="movil-panel">
            <h2 style="font-size: 1.1rem; margin: 0 0 0.7rem;">Institutos</h2>
            <div class="movil-lista-links">
                <% for (String inst : institutos) { %>
                    <a href="${pageContext.request.contextPath}/movil/cursos?instituto=<%= parametro(inst) %>">
                        <span><%= escapar(inst) %></span>
                        <span style="color: var(--edext-cyan-hover);">&rarr;</span>
                    </a>
                <% } %>
            </div>
        </section>
    <% } %>

    <% if (categorias != null && !categorias.isEmpty()) { %>
        <section class="movil-panel">
            <h2 style="font-size: 1.1rem; margin: 0 0 0.7rem;">Categorías</h2>
            <div style="display: flex; flex-wrap: wrap; gap: 0.45rem;">
                <% for (String cat : categorias) { %>
                    <a class="movil-chip"
                       href="${pageContext.request.contextPath}/movil/cursos?categoria=<%= parametro(cat) %>">
                        <%= escapar(cat) %>
                    </a>
                <% } %>
            </div>
        </section>
    <% } %>
</main>
</body>
</html>