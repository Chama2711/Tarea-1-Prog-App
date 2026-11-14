<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    boolean docente = "docente".equals(request.getAttribute("tipo"));
    String tituloPagina = "Empezá tu camino en edEXT";
    String bajadaPagina = "Cursos, personas y nuevas oportunidades para aprender.";
    String paginaActiva = "registro";
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


<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
    <section class="formulario-tarjeta registro-tarjeta" aria-labelledby="titulo-registro">
        <h2 id="titulo-registro">Creá tu cuenta</h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %>
                <p class="ayuda">Volvé a ingresar las contraseñas y a elegir la imagen, si habías seleccionado una.</p>
            </div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/registro" enctype="multipart/form-data" id="formulario-registro">
            <div class="registro-distribucion">
                <div>
                    <fieldset class="selector-cuenta">
                        <legend>Tipo de cuenta</legend>
                        <div class="selector-opciones">
                            <label><input type="radio" name="tipo" value="estudiante" <%= docente ? "" : "checked" %> required><span>Estudiante</span></label>
                            <label><input type="radio" name="tipo" value="docente" <%= docente ? "checked" : "" %> required><span>Docente</span></label>
                        </div>
                        <p class="ayuda" id="ayuda-tipo"><%= docente ? "Como docente, podés crear cursos y acompañar estudiantes." : "Como estudiante, podés inscribirte a cursos." %></p>
                    </fieldset>
                    <div class="campo" id="campoInstituto" <%= docente ? "" : "hidden" %>>
                        <label for="instituto">Instituto del docente</label>
                        <select id="instituto" name="instituto" <%= docente ? "required" : "disabled" %>>
                            <option value="">Seleccioná un instituto</option>
                            <% for (String instituto : institutos) { %>
                                <option value="<%= escapar(instituto) %>" <%= instituto.equals(request.getAttribute("instituto")) ? "selected" : "" %>><%= escapar(instituto) %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="campo"><label for="nombre">Nombre</label>
                        <input id="nombre" name="nombre" value="<%= escapar(request.getAttribute("nombre")) %>" autocomplete="given-name" required></div>
                    <div class="campo"><label for="apellido">Apellido</label>
                        <input id="apellido" name="apellido" value="<%= escapar(request.getAttribute("apellido")) %>" autocomplete="family-name" required></div>
                    <div class="campo"><label for="nick">Nickname</label>
                        <input id="nick" name="nick" value="<%= escapar(request.getAttribute("nick")) %>" autocomplete="username" required></div>
                    <div class="campo"><label for="correo">Correo electrónico</label>
                        <input type="email" id="correo" name="correo" value="<%= escapar(request.getAttribute("correo")) %>" autocomplete="email" required></div>
                    <div class="campo"><label for="fechaNacimiento">Fecha de nacimiento</label>
                        <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="<%= escapar(request.getAttribute("fechaNacimiento")) %>" max="<%= LocalDate.now() %>" autocomplete="bday" required></div>
                    <div class="campo"><label for="clave">Contraseña</label>
                        <input type="password" id="clave" name="clave" autocomplete="new-password" required></div>
                    <div class="campo"><label for="confirmacion">Confirmar contraseña</label>
                        <input type="password" id="confirmacion" name="confirmacion" autocomplete="new-password" required></div>
                    <label class="check"><input type="checkbox" id="mostrarClave">Mostrar contraseñas</label>
                </div>
                <aside class="foto-perfil" aria-labelledby="titulo-foto">
                    <h3 id="titulo-foto">Foto de perfil</h3><p class="ayuda">Opcional</p>
                    <img id="foto-preview" class="foto-preview" src="${pageContext.request.contextPath}/imagenes/avatar.svg" alt="Vista previa de la foto de perfil" width="150" height="150">
                    <div class="foto-control">
                        <input type="file" id="imagen" name="imagen" class="archivo-input" accept="image/jpeg,image/png" aria-describedby="ayuda-imagen nombre-archivo">
                        <label for="imagen" class="boton boton-secundario boton-pequeno">Elegir imagen</label>
                        <p class="ayuda" id="ayuda-imagen">JPG o PNG · Hasta 5 MB</p>
                        <p class="nombre-archivo" id="nombre-archivo" role="status"></p>
                    </div>
                </aside>
            </div>
            <div class="acciones-registro">
                <button type="submit" class="boton">Crear cuenta</button>
                <a class="boton boton-secundario" href="${pageContext.request.contextPath}/inicio">Cancelar</a>
            </div>
        </form>
        <p class="formulario-enlaces">¿Ya tenés cuenta? <a href="${pageContext.request.contextPath}/login">Iniciá sesión</a></p>
    </section>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
<script src="${pageContext.request.contextPath}/js/registro.js" defer></script>
</body>
</html>
