<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.soap.generado.DtEdicionConsulta,web.soap.generado.DtDocenteResumen,logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DtEdicionConsulta edicion =
            (DtEdicionConsulta) request.getAttribute("edicion");

    String tituloPagina = edicion.getNombre();
    String bajadaPagina = "Una edición de " + edicion.getNombreCurso() + ".";
    String paginaActiva = "cursos";

    DTAutenticacion usuarioInscripcion = (DTAutenticacion) session.getAttribute("usuario");

    DTInscripcion inscripcionActual = (DTInscripcion) request.getAttribute("inscripcionActual");

    boolean esEstudiante = usuarioInscripcion != null && usuarioInscripcion.getRol() == DTAutenticacion.Rol.ESTUDIANTE;

    boolean puedeInscribirse = esEstudiante && edicion.isVigente() && inscripcionActual == null;

    boolean puedeModificarVideo = esEstudiante && edicion.isVigente() && inscripcionActual != null && inscripcionActual.getEstado() == EstadoInscripcion.INSCRIPTO;

%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=6">
    
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>

<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">


    <a class="volver" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(edicion.getNombreCurso()) %>">← Ver el curso</a>
        
        <% if ("ok".equals(request.getParameter("cancelacion"))) { %>
            <div class="aviso aviso-exito" role="status">
                Tu inscripción fue cancelada correctamente.
            </div>
        <% } %>
        
        <% if ("ok".equals(request.getParameter("inscripcion"))) { %>
            <div class="aviso aviso-exito" role="status">
                Los datos de la inscripción se guardaron correctamente.
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
            
            <div class="campo-formulario">
                <label for="videoMotivacionUrl">
                    Video de motivación (opcional)
                </label>

                <input type="url"
                       id="videoMotivacionUrl"
                       name="videoMotivacionUrl"
                       maxlength="2048"
                       placeholder="https://www.youtube.com/watch?v=..."
                       autocomplete="off">

                <small>
                    Ingresá el enlace de tu video de YouTube.
                    Solo se aceptan enlaces de youtube.com.
                </small>
            </div>

            <button type="submit"
                    class="boton boton-inscripcion">
                Inscribirme a esta edición
            </button>

        </form>

    <% } else if (puedeModificarVideo) { %>

       
        
        <div class="aviso aviso-exito">
            Ya estás inscripto a esta edición.
        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/inscripcion-edicion">

            <input type="hidden"
                   name="edicion"
                   value="<%= escapar(edicion.getNombre()) %>">

            <div class="campo-formulario">
                <label for="videoMotivacionUrl">
                    Modificar video de motivación
                </label>

                <input type="url"
                       id="videoMotivacionUrl"
                       name="videoMotivacionUrl"
                       maxlength="2048"
                       value="<%= escapar(inscripcionActual.getVideoMotivacionUrl() == null ? "" : inscripcionActual.getVideoMotivacionUrl()) %>"
                       placeholder="https://www.youtube.com/watch?v=..."
                       autocomplete="off">

                <small>
                    Podés cambiar el enlace por otro video de YouTube.
                    Para eliminar tu video, dejá el campo vacío y guardá.
                </small>
            </div>

            <button type="submit" class="boton boton-inscripcion">
                Guardar cambios del video
            </button>

        </form>
                       
        <button type="button"
               class="boton-cancelar-inscripcion"
               onclick="document.getElementById('confirmarCancelacion').showModal()">
            Cancelar inscripción
        </button>

        <dialog id="confirmarCancelacion"
                style="border: none; border-radius: 16px;
                       padding: 30px; max-width: 420px; width: 90%;
                       box-shadow: 0 12px 40px rgba(0,0,0,0.2);">

            <h2 style="margin-top: 0;">¿Cancelar inscripción?</h2>

            <p>
                ¿Estás seguro de que querés cancelar tu inscripción
                a esta edición?
            </p>

            <p style="color: #666;">
                Esta acción no se puede deshacer.
            </p>

            <div style="display: flex; gap: 12px;
                        justify-content: flex-end; margin-top: 25px;">

                <button type="button"
                        onclick="document.getElementById('confirmarCancelacion').close()"
                        style="padding: 12px 18px; border-radius: 8px;
                               border: 1px solid #ccc; background: white;
                               cursor: pointer;">
                    Volver
                </button>

                <form method="post"
                      action="<%= request.getContextPath() %>/cancelar-inscripcion-edicion?edicion=<%= java.net.URLEncoder.encode(edicion.getNombre(), java.nio.charset.StandardCharsets.UTF_8) %>">

                    <button type="submit"
                            style="padding: 12px 18px; border: none;
                                   border-radius: 8px; background: #dc3545;
                                   color: white; font-weight: bold;
                                   cursor: pointer;">
                        Sí, cancelar inscripción
                    </button>

                </form>
            </div>
        </dialog>               

    <% } else if (inscripcionActual != null) { %>

        <p class="aviso">
            Estado de tu inscripción:
            <strong><%= escapar(inscripcionActual.getEstado()) %></strong>.
            Ya no es posible modificar el video de motivación.
        </p>

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

