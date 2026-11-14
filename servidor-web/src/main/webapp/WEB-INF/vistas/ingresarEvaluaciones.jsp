<%--
    Document   : ingresarEvaluaciones
    Created on : 31 oct 2026, 19:43:44
    Author     : elizeth
--%>


<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,logica.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>

<%
    String tituloPagina = "Ingresar evaluaciones";
    String bajadaPagina =
            "Consultá los estudiantes aceptados de tus ediciones.";
    String paginaActiva = "cursos";

    String rutaEvaluaciones = "/ingresar-evaluaciones";
    String mensajeSinEdiciones = "No participás en una edición vigente de este curso.";

    String edicion =
            (String) request.getAttribute("edicion");

    List<DTInscripcion> aceptados =
            (List<DTInscripcion>) request.getAttribute("aceptados");

    if (edicion == null) edicion = "";
    if (aceptados == null) aceptados = List.of();
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Ingresar evaluaciones | edEXT</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=7">
</head>

<body>

<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>

<main id="contenido"
      class="contenedor contenido disposicion-web">

    <%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>

    <div class="contenido-pagina">

        <%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>

        <%@ include file="/WEB-INF/vistas/fragmentos/seleccionEvaluaciones.jspf" %>

        <% if (!edicion.isEmpty()) {
            boolean edicionCerrada = detalleEdicion != null && detalleEdicion.cerrada();
        %>

            <section class="panel">

                <h2>
                    Estudiantes aceptados
                </h2>

                <% if ("1".equals(request.getParameter("guardada"))) { %>
                    <p class="aviso" role="status">Calificación guardada.</p>
                <% } %>

                <% if (aceptados.isEmpty()) { %>

                    <p class="aviso">
                        No hay estudiantes aceptados
                        en esta edición.
                    </p>

                <% } else { %>

                    <p>
                        Estudiantes aceptados:
                        <strong><%= aceptados.size() %></strong>
                    </p>

                    <div class="ediciones">

                        <% for (DTInscripcion inscripcion : aceptados) { %>

                            <article class="edicion">

                                <h3>
                                    <%= escapar(
                                        inscripcion.getNombreEstudiante()
                                    ) %>
                                </h3>

                                <p class="texto-secundario">
                                    @<%= escapar(
                                        inscripcion.getNicknameEstudiante()
                                    ) %>
                                </p>

                                <div class="edicion-datos datos-columna">

                                    <span>
                                        <strong>Curso:</strong>
                                        <%= escapar(
                                            inscripcion.getNombreCurso()
                                        ) %>
                                    </span>

                                    <span>
                                        <strong>Fecha de inscripción:</strong>
                                        <%= fecha(
                                            inscripcion.getFechaInscripcion()
                                        ) %>
                                    </span>

                                    <span>
                                        <strong>Estado:</strong>
                                        <%= escapar(
                                            inscripcion.getEstado()
                                        ) %>
                                    </span>

                                </div>
                           
                                <% if (!edicionCerrada) { %>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/ingresar-evaluaciones">

                                    <input type="hidden" name="instituto" value="<%= escapar(filtroInstituto) %>">
                                    <input type="hidden" name="curso" value="<%= escapar(filtroCurso) %>">
                                    <input type="hidden"
                                           name="edicion"
                                           value="<%= escapar(edicion) %>">

                                    <input type="hidden"
                                           name="idInscripcion"
                                           value="<%= inscripcion.getId() %>">

                                    <div class="campo">

                                        <label for="nota-<%= inscripcion.getId() %>">
                                            Calificación del estudiante
                                        </label>

                                        <select
                                            id="nota-<%= inscripcion.getId() %>"
                                            name="nota"
                                            required>

                                            <option value="" selected disabled>
                                                Seleccioná una calificación
                                            </option>

                                            <% for (int nota = 1; nota <= 12; nota++) { %>

                                                <option value="<%= nota %>">
                                                    <%= nota %>
                                                </option>

                                            <% } %>

                                        </select>

                                        <p class="ayuda">
                                            Las calificaciones válidas van de 1 a 12.
                                        </p>

                                    </div>

                                    <button type="submit" class="boton">
                                        Guardar nota
                                    </button>

                                </form>
                                <% } %>

                            </article>

                        <% } %>

                    </div>

                <% } %>

            </section>

            <section class="panel">

                <h2>Cierre de edición y acta</h2>

                <% if (edicionCerrada) { %>
                    <% if ("1".equals(request.getParameter("cerrada"))) { %>
                    <p class="aviso" role="status">
                        Edición cerrada.
                    </p>
                    <% } %>
                    <a class="boton"
                        href="${pageContext.request.contextPath}/ver-resultados-evaluaciones?edicion=<%= parametro(edicion) %><%= filtroCurso.isEmpty() ? "" : "&amp;instituto=" + parametro(filtroInstituto) + "&amp;curso=" + parametro(filtroCurso) %>">
                         Ver resultados definitivos
                    </a>
                <% } else { %>
                    <p class="texto-secundario">
                        Podés cerrar la edición cuando todos los estudiantes aceptados tengan calificación.
                        Una vez cerrada, las notas no podrán modificarse.
                    </p>
                    <form method="post"
                          action="${pageContext.request.contextPath}/ingresar-evaluaciones"
                          onsubmit="return confirm('¿Confirmás el cierre de esta edición? No podrás modificar las calificaciones después.');">
                        <input type="hidden" name="accion" value="cerrar">
                        <input type="hidden" name="instituto" value="<%= escapar(filtroInstituto) %>">
                        <input type="hidden" name="curso" value="<%= escapar(filtroCurso) %>">
                        <input type="hidden" name="edicion" value="<%= escapar(edicion) %>">
                        <button type="submit" class="boton">Cerrar edición</button>
                    </form>
                <% } %>

            </section>

        <% } %>

    </div>

</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>

