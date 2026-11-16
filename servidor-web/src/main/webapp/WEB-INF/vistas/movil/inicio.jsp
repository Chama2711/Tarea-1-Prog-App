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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=1">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <section class="movil-hero">
        <h1>Hola, <%= escapar(usuarioMovil != null ? usuarioMovil.getNombre() : "") %></h1>
        <p class="texto-secundario" style="margin-bottom: 1rem;">
            Consultá los cursos disponibles y sus ediciones desde tu dispositivo móvil.
        </p>
        <a class="boton movil-boton-ancho"
           style="display: inline-flex; justify-content: center; align-items: center; text-decoration: none;"
           href="${pageContext.request.contextPath}/movil/cursos">
            Explorar todos los cursos
        </a>
    </section>

    <% if (institutos != null && !institutos.isEmpty()) { %>
        <section class="movil-hero">
            <h2 style="font-size: 1.1rem; margin-top: 0;">Filtrar por Instituto</h2>
            <div class="movil-lista" style="gap: 0.5rem;">
                <% for (String inst : institutos) { %>
                    <a class="volver"
                       style="display: block; padding: 0.65rem 0.85rem; background: #f8fafc; border-radius: 8px; text-decoration: none;"
                       href="${pageContext.request.contextPath}/movil/cursos?instituto=<%= parametro(inst) %>">
                        <%= escapar(inst) %> &rarr;
                    </a>
                <% } %>
            </div>
        </section>
    <% } %>

    <% if (categorias != null && !categorias.isEmpty()) { %>
        <section class="movil-hero">
            <h2 style="font-size: 1.1rem; margin-top: 0;">Filtrar por Categoría</h2>
            <div class="categorias">
                <% for (String cat : categorias) { %>
                    <a class="categoria-chip" style="text-decoration: none;"
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