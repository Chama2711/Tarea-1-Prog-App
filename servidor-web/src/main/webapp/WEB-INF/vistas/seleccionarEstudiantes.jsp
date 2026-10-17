<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,logica.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String tituloPagina = "Elegí quiénes participan.";
    String bajadaPagina = "Revisá las inscripciones y guardá la selección de estudiantes.";
    String paginaActiva = "cursos";
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    List<String> cursos = (List<String>) request.getAttribute("cursos");
    List<DTEdicionConsulta> ediciones = (List<DTEdicionConsulta>) request.getAttribute("ediciones");
    List<DTRankingInscripcion> ranking = (List<DTRankingInscripcion>) request.getAttribute("ranking");
    String instituto = (String) request.getAttribute("instituto"), curso = (String) request.getAttribute("curso");
    String nombreEdicion = (String) request.getAttribute("edicion");
    OrdenInscripciones orden = (OrdenInscripciones) request.getAttribute("orden");
    DTEdicionConsulta detalle = (DTEdicionConsulta) request.getAttribute("detalleEdicion");
    Map<Long, EstadoInscripcion> decisiones = (Map<Long, EstadoInscripcion>) request.getAttribute("decisiones");
    if (decisiones == null) decisiones = Map.of();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Selección de estudiantes | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=7">
    <script src="${pageContext.request.contextPath}/js/seleccion-estudiantes.js" defer></script>
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
    <section class="panel" aria-labelledby="titulo-seleccion">
        <h2 id="titulo-seleccion">Seleccionar estudiantes</h2>
       <form id="filtro-estudiantes"
      class="filtro-seleccion"
      method="get"
      action="${pageContext.request.contextPath}/seleccion-estudiantes">

    <div class="campo">
        <label for="instituto">Instituto</label>
        <select id="instituto" name="instituto" required>
            <option value="">Seleccioná un instituto</option>
            <% for (String item : institutos) { %>
                <option value="<%= escapar(item) %>"
                    <%= item.equals(instituto) ? "selected" : "" %>>
                    <%= escapar(item) %>
                </option>
            <% } %>
        </select>
    </div>

    <div class="campo">
        <label for="curso">Curso</label>
        <select id="curso" name="curso"
                <%= instituto.isEmpty() ? "disabled" : "" %>
                required>
            <option value="">Seleccioná un curso</option>
            <% for (String item : cursos) { %>
                <option value="<%= escapar(item) %>"
                    <%= item.equals(curso) ? "selected" : "" %>>
                    <%= escapar(item) %>
                </option>
            <% } %>
        </select>
    </div>

    <div class="campo">
        <label for="edicion">Edición vigente</label>
        <select id="edicion" name="edicion"
                <%= curso.isEmpty() ? "disabled" : "" %>
                required>
            <option value="">Seleccioná una edición</option>
            <% for (DTEdicionConsulta item : ediciones) { %>
                <option value="<%= escapar(item.nombre()) %>"
                    <%= item.nombre().equals(nombreEdicion) ? "selected" : "" %>>
                    <%= escapar(item.nombre()) %>
                </option>
            <% } %>
        </select>
    </div>

    <fieldset class="selector-cuenta">
        <legend>Ordenar por</legend>

        <div class="selector-opciones">
            <label>
                <input type="radio" name="orden" value="FECHA"
                    <%= orden == OrdenInscripciones.FECHA ? "checked" : "" %>>
                <span>Fecha de inscripción</span>
            </label>

            <label>
                <input type="radio" name="orden" value="PRIORIDAD"
                    <%= orden == OrdenInscripciones.PRIORIDAD ? "checked" : "" %>>
                <span>Prioridad</span>
            </label>
        </div>
    </fieldset>

    <button type="submit" class="boton">
        Ver inscripciones
    </button>
</form>

<% if (!instituto.isEmpty() && cursos.isEmpty()) { %>
    <p class="aviso">Este instituto no tiene cursos.</p>
<% } else if (!curso.isEmpty() && ediciones.isEmpty()) { %>
    <p class="aviso">
        No hay ediciones vigentes de este curso en las que participes.
    </p>
<% } %>
    </section>
    <% if (detalle != null) { %>
    <section class="panel seleccion-estudiantes" aria-labelledby="titulo-inscripciones">
        <h2 id="titulo-inscripciones"><%= escapar(detalle.nombre()) %></h2>
        <div class="detalle-datos datos-columna">
            <div class="dato"><span>Inicio</span><strong><%= fecha(detalle.inicio()) %></strong></div>
            <div class="dato"><span>Fin</span><strong><%= fecha(detalle.fin()) %></strong></div>
            <div class="dato"><span>Cupo</span><strong><%= detalle.cupo() == -1 ? "Sin límite" : detalle.cupo() %></strong></div>
        </div>
        <% if (request.getAttribute("error") != null) { %><div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
        <% } else if ("1".equals(request.getParameter("guardado"))) { %><div class="aviso aviso-exito" role="status">La selección se guardó correctamente.</div><% } %>
        <% if (ranking.isEmpty()) { %><p>No hay inscripciones para esta edición.</p><% } else { %>
        <form id="form-seleccion" method="post" action="${pageContext.request.contextPath}/seleccion-estudiantes">
            <input type="hidden" name="instituto" value="<%= escapar(instituto) %>">
            <input type="hidden" name="curso" value="<%= escapar(curso) %>">
            <input type="hidden" name="edicion" value="<%= escapar(nombreEdicion) %>">
            <input type="hidden" name="orden" value="<%= orden.name() %>">
            <div class="ediciones">
            <% boolean hayPendientes = false;
               for (DTRankingInscripcion fila : ranking) {
                   DTInscripcion inscripcion = fila.getInscripcion();
                   EstadoInscripcion decision = decisiones.get(inscripcion.getId()); %>
                <article class="edicion">
                    <h3><a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(inscripcion.getNicknameEstudiante()) %>"><%= escapar(inscripcion.getNombreEstudiante()) %></a></h3>
                    <p class="texto-secundario">@<%= escapar(inscripcion.getNicknameEstudiante()) %></p>
                    <div class="edicion-datos datos-columna">
                        <span><strong>Inscripción:</strong> <%= fecha(inscripcion.getFechaInscripcion()) %></span>
                        <span><strong>Estado actual:</strong> <%= escapar(inscripcion.getEstado()) %></span>
                        <span><strong>Rechazos anteriores:</strong> <%= fila.getRechazosAnteriores() %></span>
                        <span><strong>Prioridad:</strong> <%= String.format(java.util.Locale.forLanguageTag("es-UY"), "%.1f", fila.getPrioridad()) %></span>
                    </div>
                    <% if (inscripcion.getEstado() == EstadoInscripcion.INSCRIPTO) { hayPendientes = true; %>
                    <div class="campo"><label for="decision-<%= inscripcion.getId() %>">Decisión para <%= escapar(inscripcion.getNombreEstudiante()) %></label>
                        <select id="decision-<%= inscripcion.getId() %>" name="decision_<%= inscripcion.getId() %>">
                            <option value="">Sin decidir</option>
                            <option value="ACEPTADA" <%= decision == EstadoInscripcion.ACEPTADA ? "selected" : "" %>>Aceptar</option>
                            <option value="RECHAZADA" <%= decision == EstadoInscripcion.RECHAZADA ? "selected" : "" %>>Rechazar</option>
                        </select>
                    </div>
                    <% } else { %><p class="ayuda">Esta inscripción ya fue resuelta.</p><% } %>
                </article>
            <% } %>
            </div>
            <% if (hayPendientes) { %>
            <div class="acciones-registro"><button class="boton" type="submit">Guardar selección</button>
                <button class="boton boton-secundario" type="reset">Descartar cambios</button></div>
            <% } else { %><p class="aviso">Todas las inscripciones de esta edición están resueltas.</p><% } %>
        </form>
        <% } %>
    </section>
    <% } %>
</div></main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body></html>
