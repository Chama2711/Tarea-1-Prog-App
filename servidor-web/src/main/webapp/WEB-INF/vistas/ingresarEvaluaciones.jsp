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
                           
                                <form method="post"
                                      action="${pageContext.request.contextPath}/ingresar-evaluaciones">

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

                            </article>

                        <% } %>

                    </div>

                <% } %>

            </section>

            <section class="panel">

                <h2>Cierre de edición y acta</h2>

                <p>
                    Edición seleccionada:
                    <strong><%= escapar(edicion) %></strong>
                </p>

               
                <p class="texto-secundario">
                    Podés cerrar la edición cuando todos los
                    estudiantes aceptados tengan calificación.
                    Una vez cerrada, las notas no podrán modificarse.
                </p>

                <% if ("1".equals(request.getParameter("cerrada"))) { %>
                    <p class="aviso" role="status">
                        Se procesó correctamente la solicitud de cierre.
                    </p>
                <% } %>

                <form method="post"
                      action="${pageContext.request.contextPath}/ingresar-evaluaciones"
                      onsubmit="return confirm('¿Confirmás el cierre de esta edición? No podrás modificar las calificaciones después.');">

                    <input type="hidden"
                           name="accion"
                           value="cerrar">

                    <input type="hidden"
                           name="edicion"
                           value="<%= escapar(edicion) %>">

                    <button type="submit" class="boton">
                        Cerrar edición
                    </button>
                </form>


                <p class="ayuda">
                    Para cerrar una edición será necesario
                    verificar que todos los estudiantes
                    aceptados tengan su calificación.
                </p>

                <div class="campo">
                    <a class="boton"
                        href="${pageContext.request.contextPath}/ver-resultados-evaluaciones">
                         Ver resultados definitivos
                    </a>
                </div>

                <p class="ayuda">
                    El acta podrá consultarse una vez
                    que la edición esté cerrada.
                </p>

            </section>

        <% } %>

    </div>

</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>

