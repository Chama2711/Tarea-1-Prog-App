<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>No pudimos cargar la página | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <main class="contenedor bienvenida">
        <p class="marca">ed<span>EXT</span></p>
        <h1>No pudimos cargar la página.</h1>
        <p class="introduccion">Inténtalo de nuevo en unos momentos.</p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </main>
</body>
</html>
