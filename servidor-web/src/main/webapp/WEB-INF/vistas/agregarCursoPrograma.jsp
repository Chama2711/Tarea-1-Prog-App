<%-- 
    Document   : agregarCursoPrograma
    Created on : 13 oct 2026, 14:55:41
    Author     : elizeth
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>

<%
    String tituloPagina = "Agregar curso a programa";
    String bajadaPagina = "Seleccioná un programa de formación y un curso para asociar.";
    String paginaActiva = "programas";

    List<String> programas =
            (List<String>) request.getAttribute("programas");

    List<String> cursos =
            (List<String>) request.getAttribute("cursos");

    String programaSeleccionado =
            (String) request.getAttribute("programaSeleccionado");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><%= escapar(tituloPagina) %> | edEXT</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css?v=6">
</head>

<body>

<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>

<main id="contenido" class="contenedor contenido disposicion-web">

    <%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>

    <div class="contenido-pagina">

        <%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>

        <section class="panel" aria-labelledby="titulo-agregar-curso">

            <h2 id="titulo-agregar-curso">Agregar curso a programa</h2>

            <% if (request.getAttribute("error") != null) { %>
                <div class="aviso aviso-error" role="alert">
                    <%= escapar(request.getAttribute("error")) %>
                </div>
            <% } %>

            <% if (programas.isEmpty() || cursos.isEmpty()) { %>

                <div class="aviso">
                    No hay programas o cursos disponibles para realizar esta operación.
                </div>

            <% } else { %>

                <form method="post"
                      action="${pageContext.request.contextPath}/agregar-curso-programa">

                    <div class="campo">
                        <label for="programa">Programa de formación</label>

                        <select id="programa" name="programa" required>

                            <option value="">Seleccioná un programa</option>

                            <% for (String programa : programas) { %>

                                <option value="<%= escapar(programa) %>"
                                    <%= programa.equals(programaSeleccionado)
                                            ? "selected" : "" %>>

                                    <%= escapar(programa) %>

                                </option>

                            <% } %>

                        </select>
                    </div>

                    <div class="campo">
                        <label for="curso">Curso</label>

                        <select id="curso" name="curso" required>

                            <option value="">Seleccioná un curso</option>

                            <% for (String curso : cursos) { %>

                                <option value="<%= escapar(curso) %>">
                                    <%= escapar(curso) %>
                                </option>

                            <% } %>

                        </select>
                    </div>

                    <div class="acciones-registro">

                        <button type="submit" class="boton">
                            Agregar curso
                        </button>

                        <a class="boton boton-secundario"
                           href="${pageContext.request.contextPath}/programas">
                            Cancelar
                        </a>

                    </div>

                </form>

            <% } %>

        </section>

    </div>

</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

</body>
</html>
