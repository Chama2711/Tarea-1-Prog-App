<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    boolean recordarme = Boolean.TRUE.equals(request.getAttribute("recordarme"));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
</head>
<body class="movil-body">
<header class="movil-topbar">
    <div class="movil-topbar-fila">
        <span class="movil-marca">ed<span>EXT</span></span>
        <span class="movil-saludo-top">Dispositivo Móvil</span>
    </div>
</header>

<main class="movil-login-caja">
    <section class="movil-panel" aria-labelledby="titulo-login-movil">
        <h1 id="titulo-login-movil" style="font-size: 1.5rem; margin: 0 0 0.3rem;">Iniciar sesión</h1>
        <p class="texto-secundario" style="margin-top: 0;">Acceso exclusivo para estudiantes de edEXT.</p>

        <% if (request.getParameter("salir") != null) { %>
            <div class="aviso aviso-exito" role="status">Sesión cerrada correctamente.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/movil/login" class="movil-filtros">
            <div>
                <label for="identificador">Nickname o dirección de correo</label>
                <input id="identificador" name="identificador" class="movil-input"
                       placeholder="Ingresá tu usuario o correo"
                       value="<%= escapar(request.getAttribute("identificador")) %>"
                       autocomplete="username" required>
            </div>
            <div>
                <label for="clave">Contraseña</label>
                <input type="password" id="clave" name="clave" class="movil-input"
                       placeholder="Contraseña"
                       autocomplete="current-password" required>
            </div>
            <label class="movil-check-fila" for="recordarme">
                <input type="checkbox" id="recordarme" name="recordarme" <%= recordarme ? "checked" : "" %>>
                Recordarme
            </label>
            <button type="submit" class="movil-btn-primario movil-btn-ancho">Login</button>
        </form>

        <div class="formulario-enlaces" style="margin-top: 1rem; text-align: center;">
            <a href="${pageContext.request.contextPath}/inicio">Ir a la versión de escritorio</a>
        </div>
    </section>
</main>
</body>
</html>