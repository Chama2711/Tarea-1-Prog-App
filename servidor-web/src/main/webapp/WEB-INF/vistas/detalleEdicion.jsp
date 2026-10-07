<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DTEdicionConsulta edicion =
            (DTEdicionConsulta) request.getAttribute("edicion");

    String tituloPagina = edicion.nombre();
    String bajadaPagina = "Una edición de " + edicion.nombreCurso() + ".";
    String paginaActiva = "cursos";

    DTAutenticacion usuarioInscripcion =
            (DTAutenticacion) session.getAttribute("usuario");

  boolean puedeInscribirse =
    usuarioInscripcion != null && usuarioInscripcion.getRol() == DTAutenticacion.Rol.ESTUDIANTE && edicion.vigente();

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


    <a class="volver" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(edicion.nombreCurso()) %>">← Ver el curso</a>
        <% if ("ok".equals(request.getParameter("inscripcion"))) { %>
            <div class="aviso aviso-exito" role="status">
                Inscripción realizada correctamente.
            </div>
        <% } %>

        <% if (request.getParameter("errorInscripcion") != null) { %>
            <div class="aviso aviso-error" role="alert">
                <%= escapar(request.getParameter("errorInscripcion")) %>
            </div>
        <% } %>
<section class="panel" aria-label="Información de la edición">

    <img class="imagen-detalle" src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Edición sin imagen">
    <div class="titulo-edicion">
        <h1><%= escapar(edicion.nombre()) %></h1>

        <% if (!edicion.vigente()) { %>
            <span class="estado-no-vigente">NO VIGENTE</span>
        <% } %>
    </div>

    <div class="detalle-datos datos-columna">
        <div class="dato">
            <span>Inicio</span>
            <strong><%= fecha(edicion.inicio()) %></strong>
        </div>

        <div class="dato">
            <span>Fin</span>
            <strong><%= fecha(edicion.fin()) %></strong>
        </div>

        <div class="dato">
            <span>Publicación</span>
            <strong><%= fecha(edicion.publicacion()) %></strong>
        </div>

        <div class="dato">
            <span>Cupo</span>
            <strong><%= edicion.cupo() == -1 ? "Sin límite" : edicion.cupo() %></strong>
        </div>
    </div>

    <p>
        <strong>Curso:</strong>
        <a href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(edicion.nombreCurso()) %>">
            <%= escapar(edicion.nombreCurso()) %>
        </a>
    </p>

    <% if (puedeInscribirse) { %>

        <form method="post"
              action="${pageContext.request.contextPath}/inscripcion-edicion">

            <input type="hidden"
                   name="edicion"
                   value="<%= escapar(edicion.nombre()) %>">

            <button type="submit"
                    class="boton boton-inscripcion">
                Inscribirme a esta edición
            </button>

        </form>

    <% } %>

</section>
    <section class="panel" aria-labelledby="titulo-docentes">
        <h2 id="titulo-docentes">Docentes de la edición</h2>    
        <% if (edicion.docentes().isEmpty()) { %><p class="texto-secundario">No hay docentes asignados.</p><% } %>
        <ul class="lista-docentes">
        <% for (DTUsuarioConsulta docente : edicion.docentes()) { %>
            <li><%= escapar(docente.nombreCompleto()) %> — <a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(docente.nick()) %>">@<%= escapar(docente.nick()) %></a></li>
        <% } %>
        </ul>
    </section>

</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>

