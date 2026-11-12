<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.soap.generado.DtEdicionConsulta,web.soap.generado.DtDocenteResumen,logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DtEdicionConsulta edicion =
            (DtEdicionConsulta) request.getAttribute("edicion");

    String tituloPagina = edicion.getNombre();
    String bajadaPagina = "Una edición de " + edicion.getNombreCurso() + ".";
    String paginaActiva = "cursos";

    DTAutenticacion usuarioInscripcion =
            (DTAutenticacion) session.getAttribute("usuario");

  boolean puedeInscribirse =
    usuarioInscripcion != null && usuarioInscripcion.getRol() == DTAutenticacion.Rol.ESTUDIANTE && edicion.isVigente();

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


    <a class="volver" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>">← Ver el curso</a>
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

        <% if (edicion.isTieneImagen()) { %>

            <img class="imagen-detalle"
                 src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(edicion.getNombre()) %>"
                 alt="Imagen de la edición <%= escapar(edicion.getNombre()) %>">

        <% } else { %>

            <img class="imagen-detalle"
                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                 alt="Edición sin imagen">

        <% } %>

    <div class="titulo-edicion">
        <h1><%= escapar(edicion.getNombre()) %></h1>

        <% if (!edicion.isVigente()) { %>
            <span class="estado-no-vigente">NO VIGENTE</span>
        <% } %>
    </div>

    <div class="detalle-datos datos-columna">
        <div class="dato">
            <span>Inicio</span>
            <strong><%= fecha(edicion.getInicio()) %></strong>
        </div>

        <div class="dato">
            <span>Fin</span>
            <strong><%= fecha(edicion.getFin()) %></strong>
        </div>

        <div class="dato">
            <span>Publicación</span>
            <strong><%= fecha(edicion.getPublicacion()) %></strong>
        </div>

        <div class="dato">
            <span>Cupo</span>
            <strong><%= edicion.getCupo() == -1 ? "Sin límite" : edicion.getCupo() %></strong>
        </div>
    </div>

    <p>
        <strong>Curso:</strong>
        <a href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>">
            <%= escapar(edicion.getNombreCurso()) %>
        </a>
    </p>

    <% if (puedeInscribirse) { %>

        <form method="post"
              action="${pageContext.request.contextPath}/inscripcion-edicion">

            <input type="hidden"
                   name="edicion"
                   value="<%= escapar(edicion.getNombre()) %>">

            <button type="submit"
                    class="boton boton-inscripcion">
                Inscribirme a esta edición
            </button>

        </form>

    <% } %>

</section>
    <section class="panel" aria-labelledby="titulo-docentes">
        <h2 id="titulo-docentes">Docentes de la edición</h2>    
        <% if (edicion.getDocente().isEmpty()) { %><p class="texto-secundario">No hay docentes asignados.</p><% } %>
        <ul class="lista-docentes">
        <% for (DtDocenteResumen docente : edicion.getDocente()) { %>
            <li><%= escapar(docente.getNombreCompleto()) %> — <a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(docente.getNick()) %>">@<%= escapar(docente.getNick()) %></a></li>
        <% } %>
        </ul>
    </section>

</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>

