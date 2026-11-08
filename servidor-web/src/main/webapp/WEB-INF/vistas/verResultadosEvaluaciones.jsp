<%--
    Document   : verResultadosEvaluaciones
    Created on : 7 nov 2026, 19:34:34
    Author     : elizeth
--%>
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.*,logica.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>

<%
    String tituloPagina = "Ver resultados de evaluaciones";
    String bajadaPagina =
            "Consultá las calificaciones definitivas de tus ediciones.";
    String paginaActiva = "cursos";

    List<String> ediciones =
            (List<String>) request.getAttribute("ediciones");

    String edicion =
            (String) request.getAttribute("edicion");

    List<DTResultadoEvaluacion> resultados =
            (List<DTResultadoEvaluacion>)
                    request.getAttribute("resultados");

    String avisoResultados =
        (String) request.getAttribute("avisoResultados");

    if (ediciones == null) ediciones = List.of();
    if (edicion == null) edicion = "";
    if (resultados == null) resultados = List.of();
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Ver resultados | edEXT</title>

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

            <p class="texto-secundario">
                Los resultados definitivos se encuentran
                disponibles cuando la edición está cerrada.
            </p>

            <form method="get"
                  action="${pageContext.request.contextPath}/ver-resultados-evaluaciones">

                <div class="campo">

                    <label for="edicion">
                        Edición de curso
                    </label>

                    <select id="edicion"
                            name="edicion"
                            required>

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
                    Consultar resultados
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
                    Resultados:
                    <%= escapar(edicion) %>
                </h2>

                <% if (avisoResultados != null) { %>

                    <p class="aviso" role="status">
                        <strong>Edición todavía abierta.</strong>
                        <%= escapar(avisoResultados) %>
                    </p>

                <% } else { %>

                <p>
                    Estudiantes aceptados:
                    <strong><%= resultados.size() %></strong>
                </p>

                <% if (resultados.isEmpty()) { %>

                    <p class="aviso">
                        Esta edición no tiene resultados
                        de estudiantes aceptados.
                    </p>

                <% } else { %>

                    <div class="ediciones">

                        <% for (DTResultadoEvaluacion resultado
                                : resultados) { %>

                            <article class="edicion">

                                <h3>
                                    <%= escapar(
                                            resultado.getNombreEstudiante()
                                    ) %>
                                </h3>

                                <p class="texto-secundario">
                                    @<%= escapar(
                                            resultado.getNicknameEstudiante()
                                    ) %>
                                </p>

                                <div class="edicion-datos datos-columna">

                                    <span>
                                        <strong>Calificación:</strong>

                                        <%= resultado.getCalificacion()
                                                == null
                                                ? "Sin calificación"
                                                : resultado.getCalificacion() %>
                                    </span>

                                    <span>
                                        <strong>Fecha de calificación:</strong>

                                        <%= resultado.getFechaCalificacion()
                                                == null
                                                ? "Sin fecha"
                                                : fecha(
                                                    resultado.getFechaCalificacion()
                                                ) %>
                                    </span>

                                </div>

                            </article>

                        <% } %>

                    </div>

                <% } %>

                <p class="texto-secundario">
                    Estos resultados son de solo lectura.
                    Las calificaciones de una edición
                    cerrada no se pueden modificar.
                </p>

                <% } %>

            </section>

        <% } %>

    </div>

</main>

</body>
</html>
