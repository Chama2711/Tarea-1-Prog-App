<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<ProgramaFormacion> programas = (List<ProgramaFormacion>) request.getAttribute("programas");
    String tituloPagina = "Un recorrido para seguir creciendo.";
    String bajadaPagina = "Conocé los programas y los cursos que forman cada propuesta.";
    String paginaActiva = "programas";
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
    <h2>Programas de formación</h2>
    <% if (programas == null || programas.isEmpty()) { %>
        <div class="aviso">No hay programas de formación disponibles.</div>
    <% } else { %>
        <section class="grilla-programas" aria-label="Programas de formación">
            <% for (ProgramaFormacion programa : programas) { %>
                <article class="programa-tarjeta">
                    <h2><%= escapar(programa.getNombre()) %></h2>
                    <p><%= escapar(programa.getDescripcion()) %></p>
                    <div class="programa-datos">
                        <span><strong>Inicio</strong><%= fecha(programa.getFechaInicio()) %></span>
                        <span><strong>Fin</strong><%= fecha(programa.getFechaFin()) %></span>
                        <a class="enlace-detalle" href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(programa.getNombre()) %>" aria-label="Ver detalle de <%= escapar(programa.getNombre()) %>">Ver detalle →</a>
                    </div>
                </article>
            <% } %>
        </section>
    <% } %>
    <a class="volver volver-final" href="${pageContext.request.contextPath}/inicio">← Volver al inicio</a>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
