<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String tituloPagina = "Certificado de programa de formación";
    String bajadaPagina = "Descargá el certificado de los programas que completaste.";
    String paginaActiva = "usuarios";
    List<String> programasCertificado = (List<String>) request.getAttribute("programasCertificado");
%>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Certificado | edEXT</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=6"></head><body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %><div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
<section class="panel">
    <p>Para obtenerlo, tenés que aprobar todos los cursos del programa con una nota de 6 o más, en ediciones cerradas.</p>
    <% if (request.getAttribute("errorCertificado") != null) { %>
    <p class="aviso" role="alert"><%= escapar((String) request.getAttribute("errorCertificado")) %></p>
    <% } %>
    <% if (programasCertificado.isEmpty()) { %><p>No hay programas de formación disponibles.</p><% } else { %>
    <form method="get" action="${pageContext.request.contextPath}/certificado-programa">
        <input type="hidden" name="descargar" value="1">
        <div class="campo"><label for="programa-certificado">Programa de formación</label>
        <select id="programa-certificado" name="programa" required><option value="">Seleccioná un programa</option>
        <% for (String programaCertificado : programasCertificado) { %>
        <option value="<%= escapar(programaCertificado) %>" <%= programaCertificado.equals(request.getAttribute("programaSeleccionado")) ? "selected" : "" %>><%= escapar(programaCertificado) %></option>
        <% } %></select></div><button class="boton" type="submit">Descargar certificado PDF</button>
    </form><% } %>
</section></div></main><%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %></body></html>
