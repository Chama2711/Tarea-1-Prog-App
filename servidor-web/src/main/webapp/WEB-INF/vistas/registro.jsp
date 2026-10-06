<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%!
    private String escapar(Object valor) {
        if (valor == null) return "";
        return valor.toString().replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    boolean docente = "docente".equals(request.getAttribute("tipo"));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear una cuenta | edEXT</title>
</head>
<body>
    <nav><a href="${pageContext.request.contextPath}/inicio">Inicio</a> |
        <a href="${pageContext.request.contextPath}/login">Iniciar sesión</a></nav>
    <main>
        <h1>Crear una cuenta</h1>
        <% if (request.getAttribute("error") != null) { %>
            <p role="alert"><%= escapar(request.getAttribute("error")) %></p>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/registro" enctype="multipart/form-data">
            <p><label for="tipo">Tipo de usuario</label>
                <select id="tipo" name="tipo" required>
                    <option value="estudiante" <%= docente ? "" : "selected" %>>Estudiante</option>
                    <option value="docente" <%= docente ? "selected" : "" %>>Docente</option>
                </select></p>
            <p id="campoInstituto"><label for="instituto">Instituto del docente</label>
                <select id="instituto" name="instituto">
                    <option value="">Seleccioná un instituto</option>
                    <% for (String instituto : institutos) { %>
                        <option value="<%= escapar(instituto) %>" <%= instituto.equals(request.getAttribute("instituto")) ? "selected" : "" %>><%= escapar(instituto) %></option>
                    <% } %>
                </select></p>
            <p><label for="nick">Nickname</label>
                <input id="nick" name="nick" value="<%= escapar(request.getAttribute("nick")) %>" autocomplete="username" required></p>
            <p><label for="correo">Correo electrónico</label>
                <input type="email" id="correo" name="correo" value="<%= escapar(request.getAttribute("correo")) %>" autocomplete="email" required></p>
            <p><label for="nombre">Nombre</label>
                <input id="nombre" name="nombre" value="<%= escapar(request.getAttribute("nombre")) %>" autocomplete="given-name" required></p>
            <p><label for="apellido">Apellido</label>
                <input id="apellido" name="apellido" value="<%= escapar(request.getAttribute("apellido")) %>" autocomplete="family-name" required></p>
            <p><label for="fechaNacimiento">Fecha de nacimiento</label>
                <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="<%= escapar(request.getAttribute("fechaNacimiento")) %>" max="<%= java.time.LocalDate.now() %>" autocomplete="bday" required></p>
            <p><label for="clave">Contraseña</label>
                <input type="password" id="clave" name="clave" autocomplete="new-password" required></p>
            <p><label for="confirmacion">Confirmar contraseña</label>
                <input type="password" id="confirmacion" name="confirmacion" autocomplete="new-password" required></p>
            <p><label><input type="checkbox" id="mostrarClave"> Mostrar contraseñas</label></p>
            <p><label for="imagen">Imagen opcional (JPG o PNG, hasta 5 MB)</label>
                <input type="file" id="imagen" name="imagen" accept="image/jpeg,image/png"></p>
            <button type="submit">Crear cuenta</button>
            <a href="${pageContext.request.contextPath}/inicio">Cancelar</a>
        </form>
    </main>
    <script>
        const tipo = document.getElementById('tipo');
        const instituto = document.getElementById('instituto');
        function actualizarTipo() {
            const docente = tipo.value === 'docente';
            document.getElementById('campoInstituto').hidden = !docente;
            instituto.disabled = !docente;
            instituto.required = docente;
        }
        tipo.addEventListener('change', actualizarTipo);
        actualizarTipo();
        document.getElementById('mostrarClave').addEventListener('change', function () {
            for (const id of ['clave', 'confirmacion']) {
                document.getElementById(id).type = this.checked ? 'text' : 'password';
            }
        });
    </script>
</body>
</html>
