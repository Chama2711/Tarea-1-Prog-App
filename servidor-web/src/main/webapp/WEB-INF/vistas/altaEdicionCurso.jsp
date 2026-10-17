<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String tituloPagina = "Creá una edición.";
    String bajadaPagina = "Definí las fechas, el cupo y los docentes de un curso.";
    String paginaActiva = "cursos";
    String hoy = java.time.LocalDate.now().toString();
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    List<String> cursos = (List<String>) request.getAttribute("cursos");
    List<String> docentes = (List<String>) request.getAttribute("docentes");
    List<String> docentesSeleccionados = (List<String>) request.getAttribute("docentesSeleccionados");
    String institutoElegido = (String) request.getAttribute("instituto");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=6">
</head>

<script>
document.addEventListener("DOMContentLoaded", function () {

    const fechaInicio = document.getElementById("fechaInicio");
    const fechaFin = document.getElementById("fechaFin");
    const hoy = "<%= hoy %>";

    // Los campos solo existen cuando se seleccionó un instituto.
    if (!fechaInicio || !fechaFin) {
        return;
    }

    function actualizarFechaFin() {
        fechaFin.min = fechaInicio.value || hoy;

        if (fechaFin.value && fechaFin.value < fechaFin.min) {
            fechaFin.value = "";
        }
    }

    fechaInicio.addEventListener("change", actualizarFechaFin);

    actualizarFechaFin();
});
</script>

<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
<section class="panel" aria-labelledby="datos-edicion">
    <h2 id="datos-edicion">Datos de la edición</h2>
    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <p class="ayuda">Si habías elegido una imagen, volvé a seleccionarla antes de enviar.</p>
    <% } %>
    <% if (institutos.isEmpty()) { %>
        <p>No tenés institutos asociados para registrar una edición.</p>
    <% } else { %>
    <form method="get" action="${pageContext.request.contextPath}/alta-edicion-curso">
        <div class="campo"><label for="instituto">Instituto</label>
            <select id="instituto" name="instituto" required>
                <option value="">Seleccioná un instituto</option>
                <% for (String instituto : institutos) { %>
                    <option value="<%= escapar(instituto) %>" <%= instituto.equals(institutoElegido) ? "selected" : "" %>><%= escapar(instituto) %></option>
                <% } %>
            </select>
        </div>
        <button type="submit" class="boton boton-secundario boton-pequeno">Ver cursos del instituto</button>
    </form>
    <% if (!institutoElegido.isEmpty() && cursos.isEmpty()) { %>
        <p class="aviso">Este instituto no tiene cursos. <a href="${pageContext.request.contextPath}/alta-curso">Creá un curso</a> antes de registrar su edición.</p>
    <% } else if (!institutoElegido.isEmpty()) { %>
    <form class="formulario-edicion" method="post" action="${pageContext.request.contextPath}/alta-edicion-curso" enctype="multipart/form-data">
        <input type="hidden" name="instituto" value="<%= escapar(institutoElegido) %>">
        <div class="campo"><label for="curso">Curso</label>
            <select id="curso" name="curso" required>
                <option value="">Seleccioná un curso</option>
                <% for (String curso : cursos) { %>
                    <option value="<%= escapar(curso) %>" <%= curso.equals(request.getAttribute("curso")) ? "selected" : "" %>><%= escapar(curso) %></option>
                <% } %>
            </select>
        </div>
        <div class="campo"><label for="nombre">Nombre de la edición</label>
            <input id="nombre" name="nombre" value="<%= escapar(request.getAttribute("nombre")) %>" required></div>
        <div class="campo">
            <label for="fechaInicio">Fecha de inicio</label>
            <input type="date"
                   id="fechaInicio"
                   name="fechaInicio"
                   min="<%= hoy %>"
                   value="<%= escapar(request.getAttribute("fechaInicio")) %>"
                   required>
        </div>

        <div class="campo">
            <label for="fechaFin">Fecha de finalización</label>
            <input type="date"
                   id="fechaFin"
                   name="fechaFin"
                   min="<%= hoy %>"
                   value="<%= escapar(request.getAttribute("fechaFin")) %>"
                   required>
        </div>
        <div class="campo"><label for="cupos">Cupo</label>
            <input type="number" id="cupos" name="cupos" min="-1" step="1" value="<%= escapar(request.getAttribute("cupos")) %>" aria-describedby="ayuda-cupo" placeholder="Sin límite">
            <p id="ayuda-cupo" class="ayuda">Dejalo vacío o usá -1 para sin límite. El valor 0 no permite aceptaciones.</p>
        </div>
        <div class="campo">
            <label for="imagen">Imagen de la edición (opcional)</label>
            <input type="file" id="imagen" name="imagen" accept="image/jpeg,image/png" aria-describedby="ayuda-imagen">
            <p class="ayuda" id="ayuda-imagen">JPG o PNG, hasta 5 MB. Si no elegís una, se muestra la imagen por defecto.</p>
        </div>
        <fieldset class="opciones-formulario"><legend>Docentes participantes</legend>
            <% for (String docente : docentes) { %>
                <label class="check"><input type="checkbox" name="docentes" value="<%= escapar(docente) %>" <%= docentesSeleccionados.contains(docente) ? "checked" : "" %>><%= escapar(docente) %></label>
            <% } %>
            <% if (docentes.isEmpty()) { %><p>No hay docentes disponibles en este instituto.</p><% } %>
        </fieldset>
        <div class="acciones-registro">
            <button type="submit" class="boton">Crear edición</button>
            <a class="boton boton-secundario" href="${pageContext.request.contextPath}/cursos">Cancelar</a>
        </div>
    </form>
    <% } %>
    <% } %>
</section>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
