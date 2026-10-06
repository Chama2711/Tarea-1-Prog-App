<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%

    String tituloPagina = "Qué bueno verte de nuevo.";
    String bajadaPagina = "Ingresá a edEXT para continuar.";
    String paginaActiva = "login";
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
    <section class="formulario-tarjeta login-tarjeta" aria-labelledby="titulo-login">
        <h2 id="titulo-login">Iniciar sesión</h2>
        <% if ("correcto".equals(request.getParameter("registro"))) { %>
            <div class="aviso aviso-exito" role="status">Tu cuenta fue creada. Ya podés iniciar sesión.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/login" class="login-formulario">
            <div class="campo"><label for="identificador">Nickname o correo electrónico</label>
                <input id="identificador" name="identificador" value="<%= escapar(request.getAttribute("identificador")) %>" autocomplete="username" required></div>
            <div class="campo"><label for="clave">Contraseña</label>
                <input type="password" id="clave" name="clave" autocomplete="current-password" required></div>
            <button type="submit" class="boton">Iniciar sesión</button>
        </form>
        <div class="formulario-enlaces">
            <p>¿No tenés cuenta? <a href="${pageContext.request.contextPath}/registro">Registrarse</a></p>
            <a href="${pageContext.request.contextPath}/inicio">Volver al inicio</a>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
