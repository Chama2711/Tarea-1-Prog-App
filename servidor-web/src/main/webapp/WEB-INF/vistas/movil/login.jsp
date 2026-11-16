<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=1">
</head>
<body class="movil-body">
<header class="movil-topbar">
    <div class="movil-topbar-fila">
        <span class="movil-marca">edEXT <span class="movil-badge">Móvil</span></span>
    </div>
</header>

<main class="movil-login-caja">
    <section class="formulario-tarjeta" aria-labelledby="titulo-login-movil">
        <h1 id="titulo-login-movil" style="font-size: 1.4rem; margin-top: 0;">Acceso Estudiantes</h1>
        <p class="texto-secundario">Ingresá con tu nickname o correo electrónico.</p>

        <% if (request.getParameter("salir") != null) { %>
            <div class="aviso aviso-exito" role="status">Sesión cerrada correctamente.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/movil/login" class="login-formulario">
            <div class="campo">
                <label for="identificador">Nickname o correo</label>
                <input id="identificador" name="identificador"
                       value="<%= escapar(request.getAttribute("identificador")) %>"
                       autocomplete="username" required>
            </div>
            <div class="campo">
                <label for="clave">Contraseña</label>
                <input type="password" id="clave" name="clave"
                       autocomplete="current-password" required>
            </div>
            <button type="submit" class="boton movil-boton-ancho">Ingresar</button>
        </form>

        <div class="formulario-enlaces" style="margin-top: 1rem;">
            <a href="${pageContext.request.contextPath}/inicio">Ir a la versión de escritorio</a>
        </div>
    </section>
</main>
</body>
</html>