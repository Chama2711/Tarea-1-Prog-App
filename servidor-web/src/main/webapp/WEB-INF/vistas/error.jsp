<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%

    String tituloPagina = "Algo no salió como esperábamos.";
    String bajadaPagina = "Podés volver al inicio e intentarlo nuevamente.";
    String paginaActiva = "";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>


<main id="contenido" class="contenedor contenido error-contenido">
    <section class="panel error-tarjeta">
        <svg aria-hidden="true" viewBox="0 0 64 64" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="m32 7 27 47H5L32 7Z"/><path d="M32 24v13"/><circle cx="32" cy="45" r="1"/></svg>
        <h2>No pudimos cargar la página.</h2>
        <p>Intentá de nuevo en unos momentos.</p>
        <a class="boton" href="${pageContext.request.contextPath}/inicio">Volver al inicio</a>
    </section>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
