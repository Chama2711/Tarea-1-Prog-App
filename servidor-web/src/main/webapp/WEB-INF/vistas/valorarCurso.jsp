<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<String> cursosValorables = (List<String>) request.getAttribute("cursosValorables");
    String cursoSeleccionado = (String) request.getAttribute("cursoSeleccionado");
    Integer valoracionPropia = (Integer) request.getAttribute("valoracionPropia");
    Double promedio = (Double) request.getAttribute("promedioValoraciones");
    String tituloPagina = "Valorar un curso";
    String bajadaPagina = "Compartí tu experiencia en los cursos en los que participás.";
    String paginaActiva = "cursos";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Valorar un curso | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
    <%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
    <div class="contenido-pagina">
        <%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
        <% if (request.getAttribute("errorValoracion") != null) { %>
            <div class="aviso" role="alert"><%= escapar(request.getAttribute("errorValoracion")) %></div>
        <% } else if ("1".equals(request.getParameter("guardada")) && valoracionPropia != null) { %>
            <div class="aviso" role="status">Tu valoración se guardó correctamente.</div>
        <% } %>
        <section class="panel" aria-labelledby="titulo-cursos-valorables">
            <h2 id="titulo-cursos-valorables">Tus cursos</h2>
            <% if (cursosValorables.isEmpty()) { %>
                <p class="aviso">Todavía no tenés inscripciones aceptadas en cursos para valorar.</p>
            <% } else { %>
                <form method="get" action="${pageContext.request.contextPath}/valorar-curso">
                    <div class="campo">
                        <label for="cursoValoracion">Curso</label>
                        <select id="cursoValoracion" name="curso" required>
                            <option value="">Seleccioná un curso</option>
                            <% for (String nombre : cursosValorables) { %>
                                <option value="<%= escapar(nombre) %>" <%= nombre.equals(cursoSeleccionado) ? "selected" : "" %>><%= escapar(nombre) %></option>
                            <% } %>
                        </select>
                    </div>
                    <button type="submit" class="boton">Consultar curso</button>
                </form>
            <% } %>
        </section>
        <% if (!cursoSeleccionado.isEmpty()) { %>
            <section class="panel" aria-labelledby="titulo-puntuar-curso">
                <h2 id="titulo-puntuar-curso"><%= escapar(cursoSeleccionado) %></h2>
                <p><strong>Promedio del curso:</strong>
                    <%= promedio == null || promedio == 0 ? "Sin valoraciones" : escapar(String.format(Locale.forLanguageTag("es-UY"), "%.2f de 5", promedio)) %>
                </p>
                <% if (valoracionPropia != null) { %>
                    <p><strong>Tu valoración:</strong> <%= valoracionPropia %> de 5.</p>
                    <p class="texto-secundario">Ya valoraste este curso. Cada estudiante puede valorarlo una única vez.</p>
                <% } else { %>
                    <form method="post" action="${pageContext.request.contextPath}/valorar-curso">
                        <input type="hidden" name="curso" value="<%= escapar(cursoSeleccionado) %>">
                        <div class="campo">
                            <label for="puntuacion">Tu puntuación</label>
                            <select id="puntuacion" name="puntuacion" required>
                                <option value="">Seleccioná una puntuación</option>
                                <% for (int punto = 1; punto <= 5; punto++) { %>
                                    <option value="<%= punto %>"><%= punto %></option>
                                <% } %>
                            </select>
                            <p class="ayuda">Podés valorar el curso una sola vez, aunque participes en distintas ediciones.</p>
                        </div>
                        <button type="submit" class="boton">Guardar valoración</button>
                    </form>
                <% } %>
                <a class="enlace-detalle" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(cursoSeleccionado) %>">Ver detalle del curso</a>
            </section>
        <% } %>
    </div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
