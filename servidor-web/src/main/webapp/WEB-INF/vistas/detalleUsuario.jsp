<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DTPerfilUsuario perfil = (DTPerfilUsuario) request.getAttribute("perfil");
    DTUsuarioConsulta usuario = perfil.usuario();
    boolean docente = usuario.rol() == DTAutenticacion.Rol.DOCENTE;
    String tituloPagina = perfil.propio() ? "Tu espacio en edEXT." : "Conocé su recorrido.";
    String bajadaPagina = "Datos personales y participación en la comunidad.";
    String paginaActiva = "usuarios";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <script src="${pageContext.request.contextPath}/js/perfil.js?v=3" defer></script>
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>

<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>

    <a class="volver" href="${pageContext.request.contextPath}/usuarios?tipo=<%= docente ? "docentes" : "estudiantes" %>">← Volver a usuarios</a>
    <section class="panel perfil-encabezado" aria-label="Perfil de usuario">
        <img class="avatar-usuario avatar-grande foto-ampliable" src="${pageContext.request.contextPath}/<%= usuario.tieneImagen() ? "imagen-usuario?nick=" + parametro(usuario.nick()) : "imagenes/avatar.svg" %>" alt="Foto de perfil de <%= escapar(usuario.nombreCompleto()) %>" width="120" height="120" role="button" tabindex="0" aria-label="Ampliar foto de perfil" aria-haspopup="dialog" aria-controls="visor-foto" title="Doble clic para ampliar la foto">
        <div class="perfil-identidad">
            <span class="categoria-chip"><%= docente ? "Docente" : "Estudiante" %></span>
            <h2><%= escapar(usuario.nombreCompleto()) %></h2>
            <p class="texto-secundario">@<%= escapar(usuario.nick()) %><%= perfil.propio() ? " · Mi perfil" : "" %></p>
        </div>
    </section>
    <section class="panel perfil-contenido" aria-label="Información del usuario">

        <nav class="perfil-pestanas" aria-label="Secciones del perfil">
            <a id="tab-general" href="#perfil-general">General</a>
            <a id="tab-ediciones" href="#perfil-ediciones">Ediciones</a>
            <% if (!docente) { %><a id="tab-programas" href="#perfil-programas">Programas</a><% } %>
            <% if (docente && perfil.propio()) { %><a id="tab-aceptados" href="#perfil-aceptados">Estudiantes aceptados</a><% } %>
        </nav>
        <section id="perfil-general" class="perfil-panel" aria-labelledby="tab-general">
        <div class="perfil-datos-fijos">
            <dl class="perfil-datos">
                <div><dt>Nombre</dt><dd><%= escapar(usuario.nombre()) %></dd></div>
                <div><dt>Apellido</dt><dd><%= escapar(usuario.apellido()) %></dd></div>
                <div><dt>Correo electrónico</dt><dd><%= escapar(usuario.correo()) %></dd></div>
                <div><dt>Fecha de nacimiento</dt><dd><%= fecha(usuario.fechaNacimiento()) %></dd></div>
                <% if (docente) { %>
                    <div><dt>Instituto</dt><dd><%= usuario.institutos().isEmpty() ? "No indicado" : escapar(String.join(", ", usuario.institutos())) %></dd></div>
                <% } %>
            </dl>
        </div>
        </section>
        <section id="perfil-ediciones" class="perfil-panel" aria-labelledby="titulo-participacion">
            <h3 id="titulo-participacion"><%= docente ? "Ediciones en las que participa" : "Inscripciones a ediciones de cursos" %></h3>
            <% if (docente) { %>
                <% if (perfil.ediciones().isEmpty()) { %><p class="texto-secundario">Este docente no participa en ediciones de cursos.</p><% } %>
                <div class="ediciones">
                <% for (DTEdicionConsulta edicion : perfil.ediciones()) { %>
                    <article class="edicion">
                        <h4><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(edicion.nombre()) %>"><%= escapar(edicion.nombre()) %></a></h4>
                        <p><%= escapar(edicion.nombreCurso()) %></p>
                        <div class="edicion-datos"><span><strong>Inicio:</strong> <%= fecha(edicion.inicio()) %></span><span><strong>Fin:</strong> <%= fecha(edicion.fin()) %></span></div>
                    </article>
                <% } %>
                </div>
            <% } else { %>
                <% if (perfil.propio()) { %><p class="texto-secundario">Aquí también podés ver tus inscripciones rechazadas.</p><% } %>
                <% if (perfil.inscripciones().isEmpty()) { %><p class="texto-secundario">No hay inscripciones para mostrar.</p><% } %>
                <div class="ediciones">
                <% for (DTInscripcion inscripcion : perfil.inscripciones()) { %>
                    <article class="edicion">
                        <h4><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(inscripcion.getNombreEdicion()) %>"><%= escapar(inscripcion.getNombreEdicion()) %></a></h4>
                        <p><%= escapar(inscripcion.getNombreCurso()) %></p>
                        <div class="edicion-datos">
                            <span><strong>Fecha de inscripción:</strong> <%= fecha(inscripcion.getFechaInscripcion()) %></span>
                            <span class="estado-inscripcion"><%= inscripcion.getEstado() == EstadoInscripcion.ACEPTADA ? "Aceptada" : inscripcion.getEstado() == EstadoInscripcion.RECHAZADA ? "Rechazada" : "Inscripto" %></span>
                        </div>
                    </article>
                <% } %>
                </div>
            <% } %>
        </section>
        <% if (!docente) { %>
        <section id="perfil-programas" class="perfil-panel" aria-labelledby="titulo-programas-usuario">
            <h3 id="titulo-programas-usuario">Programas en los que está inscripto</h3>
            <% if (perfil.programas().isEmpty()) { %><p class="texto-secundario">No hay inscripciones a programas para mostrar.</p><% } %>
            <div class="ediciones">
            <% for (String programa : perfil.programas()) { %>
                <article class="edicion"><h4><a href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(programa) %>"><%= escapar(programa) %></a></h4></article>
            <% } %>
            </div>
        </section>
        <% } %>
        <% if (docente && perfil.propio()) { %>
        <section id="perfil-aceptados" class="perfil-panel" aria-labelledby="titulo-aceptados">
            <h3 id="titulo-aceptados">Estudiantes aceptados en tus ediciones</h3>
            <% if (perfil.aceptados().isEmpty()) { %><p class="texto-secundario">Todavía no hay estudiantes aceptados para mostrar.</p><% } %>
            <div class="ediciones">
            <% for (DTInscripcion aceptado : perfil.aceptados()) { %>
                <article class="edicion">
                    <h4><a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(aceptado.getNicknameEstudiante()) %>"><%= escapar(aceptado.getNombreEstudiante()) %></a></h4>
                    <p><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(aceptado.getNombreEdicion()) %>"><%= escapar(aceptado.getNombreEdicion()) %></a></p>
                    <div class="edicion-datos"><span><strong>Fecha de inscripción:</strong> <%= fecha(aceptado.getFechaInscripcion()) %></span><span class="estado-inscripcion">Aceptada</span></div>
                </article>
            <% } %>
            </div>
        </section>
        <% } %>
    </section>

</div>
</main>
<dialog id="visor-foto" class="visor-foto" aria-labelledby="titulo-visor-foto">
    <div class="visor-foto-cabecera">
        <h2 id="titulo-visor-foto">Foto de perfil</h2>
        <form method="dialog"><button class="boton boton-secundario boton-pequeno" autofocus>Cerrar</button></form>
    </div>
    <img id="foto-ampliada" alt="Foto de perfil de <%= escapar(usuario.nombreCompleto()) %>">
</dialog>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
