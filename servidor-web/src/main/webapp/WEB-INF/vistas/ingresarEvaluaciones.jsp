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

    List<String> ediciones =
            (List<String>) request.getAttribute("ediciones");

    String edicion =
            (String) request.getAttribute("edicion");

    List<DTInscripcion> aceptados =
            (List<DTInscripcion>) request.getAttribute("aceptados");

    if (ediciones == null) ediciones = List.of();
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

        <section class="panel">

            <h2>Seleccionar edición</h2>

            <form method="get"
                  action="${pageContext.request.contextPath}/ingresar-evaluaciones">

                <div class="campo">

                    <label for="edicion">Edición de curso</label>

                    <select id="edicion" name="edicion" required>

                        <option value="">
                            Seleccioná una edición
                        </option>

                        <% for (String item : ediciones) { %>

                            <option value="<%= escapar(item) %>"
                                <%= item.equals(edicion)
                                        ? "selected" : "" %>>

                                <%= escapar(item) %>

                            </option>

                        <% } %>

                    </select>

                </div>

                <button type="submit" class="boton">
                    Ver estudiantes aceptados
                </button>

            </form>

            <% if (ediciones.isEmpty()) { %>

                <p class="aviso">
                    No tenés ediciones de curso asignadas.
                </p>

            <% } %>

        </section>

        <% if (!edicion.isEmpty()) { %>

            <section class="panel">

                <h2>
                    Evaluaciones:
                    <%= escapar(edicion) %>
                </h2>

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


                                <div class="campo">
                                    <label for="nota-<%= inscripcion.getId() %>">
                                        Calificación del estudiante
                                    </label>

                                    <select
                                        id="nota-<%= inscripcion.getId() %>"
                                        name="nota-<%= inscripcion.getId() %>"
                                        disabled>

                                        <option value="">
                                            Sin calificación
                                        </option>

                                        <% for (int nota = 1; nota <= 12; nota++) { %>
                                            <option value="<%= nota %>">
                                                <%= nota %>
                                            </option>
                                        <% } %>

                                    </select>

                                    <p class="ayuda">
                                        Las calificaciones válidas van de 1 a 12.
                                        El registro de notas estará disponible
                                        cuando se integre el Servidor Central.
                                    </p>
                                </div>


                            </article>

                        <% } %>

                    </div>

                <% } %>

            </section>

        <% } %>

    </div>

</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>

