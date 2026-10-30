   <%-- 
    Document   : altaPrograma
    Created on : 16 oct 2026, 0:13:21
    Author     : elizeth
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String tituloPagina = "Creá un programa de formación.";
    String bajadaPagina = "Definí el nombre, la descripción y las fechas del programa.";
    String paginaActiva = "programas";
    String hoy = java.time.LocalDate.now().toString();
    
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
document.addEventListener("DOMContentLoaded", () => {
    const fechaInicio = document.getElementById("fechaInicio");
    const fechaFin = document.getElementById("fechaFin");
    const hoy = "<%= hoy %>";

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

        <section class="panel" aria-labelledby="datos-programa">
            <h2 id="datos-programa">Datos del programa de formación</h2>

            <% if (request.getAttribute("error") != null) { %>
                <div class="aviso aviso-error" role="alert">
                    <%= escapar(request.getAttribute("error")) %>
                </div>
            <% } %>

            <form method="post" enctype="multipart/form-data"
                  action="${pageContext.request.contextPath}/alta-programa">

                <div class="campo">
                    <label for="nombre">Nombre del programa</label>
                    <input type="text"
                           id="nombre"
                           name="nombre"
                           value="<%= escapar(request.getAttribute("nombre")) %>"
                           required>
                </div>

                <div class="campo">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion"
                              name="descripcion"
                              rows="5"
                              required><%= escapar(request.getAttribute("descripcion")) %></textarea>
                </div>

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

                <div class="campo">
                    <label for="imagen">Imagen del programa (opcional)</label>
                    <input type="file" id="imagen" name="imagen"
                           accept="image/jpeg,image/png" aria-describedby="ayuda-imagen">
                    <p id="ayuda-imagen" class="texto-secundario">JPG o PNG, hasta 5 MB. Si hay un error al guardar, volvé a seleccionar la imagen.</p>
                </div>

                <div class="acciones-registro">
                    <button type="submit" class="boton">
                        Crear programa
                    </button>

                    <a class="boton boton-secundario"
                       href="${pageContext.request.contextPath}/programas">
                        Cancelar
                    </a>
                </div>
            </form>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>

