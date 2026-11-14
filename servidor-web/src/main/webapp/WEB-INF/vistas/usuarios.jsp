<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    List<DTUsuarioConsulta> usuarios = (List<DTUsuarioConsulta>) request.getAttribute("usuarios");
    boolean mostrarEstudiantes = "estudiantes".equals(request.getParameter("tipo"));
    DTAutenticacion.Rol rolListado = mostrarEstudiantes
            ? DTAutenticacion.Rol.ESTUDIANTE : DTAutenticacion.Rol.DOCENTE;
    usuarios = usuarios.stream().filter(u -> u.rol() == rolListado).toList();
    String tituloPagina = "Conocé a nuestra comunidad.";
    String bajadaPagina = "Estudiantes y docentes que comparten este espacio de aprendizaje.";
    String paginaActiva = "usuarios";
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

    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
    <% } %>
    <div class="titulo-seccion usuarios-introduccion"><h2>Usuarios de edEXT</h2><p>Elegí una persona para consultar su perfil.</p></div>
    <nav class="selector-usuarios" aria-label="Tipo de usuario">
        <a href="${pageContext.request.contextPath}/usuarios?tipo=docentes" <%= !mostrarEstudiantes ? "aria-current=\"page\"" : "" %>>Docentes</a>
        <a href="${pageContext.request.contextPath}/usuarios?tipo=estudiantes" <%= mostrarEstudiantes ? "aria-current=\"page\"" : "" %>>Estudiantes</a>
    </nav>
    <h2 class="titulo-grupo-usuarios"><%= mostrarEstudiantes ? "Estudiantes" : "Docentes" %></h2>
    <% if (usuarios.isEmpty()) { %>
        <div class="panel"><p class="texto-secundario">Todavía no hay <%= mostrarEstudiantes ? "estudiantes" : "docentes" %> registrados.</p></div>
    <% } else { %>
        <section class="grilla-usuarios directorio-usuarios" aria-label="<%= mostrarEstudiantes ? "Estudiantes" : "Docentes" %> de edEXT">
        <% for (DTUsuarioConsulta usuario : usuarios) { %>
            <article class="usuario-tarjeta">
                <img class="avatar-usuario" src="${pageContext.request.contextPath}/<%= usuario.tieneImagen() ? "imagen-usuario?nick=" + parametro(usuario.nick()) : "imagenes/avatar.svg" %>" alt="" loading="lazy" width="72" height="72">
                <div class="usuario-tarjeta-datos">
                    <h2><%= escapar(usuario.nombreCompleto()) %></h2>
                    <p class="texto-secundario">@<%= escapar(usuario.nick()) %></p>
                </div>
                <div class="usuario-tarjeta-acciones">
                    <span class="categoria-chip"><%= usuario.rol() == DTAutenticacion.Rol.DOCENTE ? "Docente" : "Estudiante" %></span>
                    <a class="enlace-detalle" href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(usuario.nick()) %>" aria-label="Ver perfil de <%= escapar(usuario.nombreCompleto()) %>">Ver perfil →</a>
                </div>
            </article>
        <% } %>
        </section>
    <% } %>

</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
