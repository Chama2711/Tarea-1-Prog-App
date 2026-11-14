<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String tituloPagina = "Creá un curso.";
    String bajadaPagina = "Registrá una propuesta de formación para tu instituto.";
    String paginaActiva = "cursos";
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    List<String> categorias = (List<String>) request.getAttribute("categorias");
    Map<String, List<String>> previasPorInstituto = (Map<String, List<String>>) request.getAttribute("previasPorInstituto");
    List<String> categoriasSeleccionadas = (List<String>) request.getAttribute("categoriasSeleccionadas");
    List<String> previasSeleccionadas = (List<String>) request.getAttribute("previasSeleccionadas");
    String institutoElegido = (String) request.getAttribute("instituto");
    if (institutoElegido.isEmpty() && institutos.size() == 1) institutoElegido = institutos.get(0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=6">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
<section class="panel" aria-labelledby="datos-curso">
    <h2 id="datos-curso">Datos del curso</h2>
    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <p class="ayuda">Si habías elegido una imagen, volvé a seleccionarla antes de enviar.</p>
    <% } %>
    <% if (institutos.isEmpty()) { %>
        <p>No tenés institutos asociados para registrar un curso.</p>
    <% } else { %>
    <form method="post" action="${pageContext.request.contextPath}/alta-curso" enctype="multipart/form-data">
        <div class="campo"><label for="instituto">Instituto</label>
            <select id="instituto" name="instituto" required>
                <option value="">Seleccioná un instituto</option>
                <% for (String instituto : institutos) { %>
                    <option value="<%= escapar(instituto) %>" <%= instituto.equals(institutoElegido) ? "selected" : "" %>><%= escapar(instituto) %></option>
                <% } %>
            </select>
        </div>
        <div class="campo"><label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" value="<%= escapar(request.getAttribute("nombre")) %>" required></div>
        <div class="campo"><label for="descripcion">Descripción</label>
            <textarea id="descripcion" name="descripcion" rows="5" required><%= escapar(request.getAttribute("descripcion")) %></textarea></div>
        <div class="campo"><label for="duracion">Duración</label>
            <input id="duracion" name="duracion" value="<%= escapar(request.getAttribute("duracion")) %>" placeholder="Por ejemplo, 8 semanas" required></div>
        <div class="campo"><label for="horas">Cantidad de horas</label>
            <input type="number" id="horas" name="horas" min="0" step="1" value="<%= escapar(request.getAttribute("horas")) %>" required></div>
        <div class="campo"><label for="creditos">Créditos</label>
            <input type="number" id="creditos" name="creditos" min="0" step="1" value="<%= escapar(request.getAttribute("creditos")) %>" required></div>
        <div class="campo"><label for="url">URL del curso</label>
            <input id="url" name="url" inputmode="url" value="<%= escapar(request.getAttribute("url")) %>" required></div>
        <div class="campo">
            <label for="imagen">Imagen del curso (opcional)</label>
            <input type="file" id="imagen" name="imagen" accept="image/jpeg,image/png" aria-describedby="ayuda-imagen">
            <p class="ayuda" id="ayuda-imagen">JPG o PNG, hasta 5 MB. Si no elegís una, se muestra la imagen por defecto.</p>
        </div>
        <fieldset class="opciones-formulario"><legend>Categorías</legend>
            <p class="ayuda">Seleccioná al menos una categoría.</p>
            <% for (String categoria : categorias) { %>
                <label class="check"><input type="checkbox" name="categorias" value="<%= escapar(categoria) %>" <%= categoriasSeleccionadas.contains(categoria) ? "checked" : "" %>><%= escapar(categoria) %></label>
            <% } %>
        </fieldset>
        <fieldset class="opciones-formulario"><legend>Cursos previos</legend>
            <p class="ayuda">Opcional. Solo se muestran cursos del instituto seleccionado.</p>
            <p id="previas-sin-instituto" <%= institutoElegido.isEmpty() ? "" : "hidden" %>>Seleccioná un instituto para ver sus cursos.</p>
            <% for (Map.Entry<String, List<String>> grupo : previasPorInstituto.entrySet()) {
                boolean activo = grupo.getKey().equals(institutoElegido); %>
                <div data-previas-instituto="<%= escapar(grupo.getKey()) %>" <%= activo ? "" : "hidden" %>>
                    <% for (String previa : grupo.getValue()) { %>
                        <label class="check"><input type="checkbox" name="previas" value="<%= escapar(previa) %>" <%= activo ? "" : "disabled" %> <%= activo && previasSeleccionadas.contains(previa) ? "checked" : "" %>><%= escapar(previa) %></label>
                    <% } %>
                    <% if (grupo.getValue().isEmpty()) { %><p>No hay cursos disponibles en este instituto.</p><% } %>
                </div>
            <% } %>
            <noscript>
                <p>Elegí el instituto antes de completar los datos:</p>
                <% for (String instituto : institutos) { %>
                    <a href="${pageContext.request.contextPath}/alta-curso?instituto=<%= parametro(instituto) %>"><%= escapar(instituto) %></a>
                <% } %>
            </noscript>
        </fieldset>
        <div class="acciones-registro">
            <button class="boton" type="submit">Crear curso</button>
            <a class="boton boton-secundario" href="${pageContext.request.contextPath}/cursos">Cancelar</a>
        </div>
    </form>
    <% } %>
</section>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
<script src="${pageContext.request.contextPath}/js/alta-curso.js" defer></script>
</body>
</html>
