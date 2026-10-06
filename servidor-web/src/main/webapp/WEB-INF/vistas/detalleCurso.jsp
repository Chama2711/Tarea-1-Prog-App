<%-- 
    Document   : detalleCurso
    Created on : 4 oct 2026, 0:10:35
    Author     : elizeth
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.Curso" %>
<%@ page import="logica.Categoria" %>
<%@ page import="logica.EdicionCurso" %>
<%@ page import="java.util.List" %>

<%
    Curso curso = (Curso) request.getAttribute("curso");
    List<Categoria> categorias =
            (List<Categoria>) request.getAttribute("categorias");
    List<EdicionCurso> ediciones =
            (List<EdicionCurso>) request.getAttribute("ediciones");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><%= curso.getNombre() %> | edEXT</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

<header class="cabecera">
    <div class="contenedor cabecera-interior">

        <div>
            <a class="marca"
               href="${pageContext.request.contextPath}/inicio">
                ed<span>EXT</span>
            </a>

            <span class="descripcion-marca">
                Formación y extensión
            </span>
        </div>

        <div class="sesion">

            <% if (session.getAttribute("usuario") != null) { %>

                <span>
                    Hola, ${sessionScope.usuario.nombre}
                    (${sessionScope.usuario.rol})
                </span>

                <a href="${pageContext.request.contextPath}/logout">
                    Cerrar sesión
                </a>

            <% } else { %>

                <a href="${pageContext.request.contextPath}/login">
                    Iniciar sesión
                </a>
                <a href="${pageContext.request.contextPath}/registro">Registrarse</a>

            <% } %>

        </div>

    </div>
</header>


<main class="contenedor detalle-curso">

    <a class="volver-inicio"
       href="${pageContext.request.contextPath}/cursos">
        ← Volver a cursos
    </a>


    <section class="detalle-encabezado">

        <p class="etiqueta">CURSO</p>

        <h1><%= curso.getNombre() %></h1>

        <p class="detalle-descripcion">
            <%= curso.getDescripcion() %>
        </p>

    </section>


    <section class="detalle-datos">

        <div class="dato-curso">
            <span>Duración</span>
            <strong><%= curso.getDuracion() %></strong>
        </div>

        <div class="dato-curso">
            <span>Carga horaria</span>
            <strong><%= curso.getCantidadHoras() %> horas</strong>
        </div>

        <div class="dato-curso">
            <span>Créditos</span>
            <strong><%= curso.getCreditos() %></strong>
        </div>

        <div class="dato-curso">
            <span>Fecha de registro</span>
            <strong><%= curso.getFechaRegistro() %></strong>
        </div>

    </section>


    <% if (curso.getInstituto() != null) { %>

        <section class="detalle-seccion">

            <p class="etiqueta">INSTITUTO</p>

            <h2>
                <%= curso.getInstituto().getNombre() %>
            </h2>

        </section>

    <% } %>


    <section class="detalle-seccion">

        <p class="etiqueta">CATEGORÍAS</p>

        <h2>Áreas del curso</h2>

        <% if (categorias == null || categorias.isEmpty()) { %>

            <p class="texto-secundario">
                Este curso no tiene categorías asociadas.
            </p>

        <% } else { %>

            <div class="categorias-curso">

                <% for (Categoria categoria : categorias) { %>

                    <span class="categoria-chip">
                        <%= categoria.getNombre() %>
                    </span>

                <% } %>

            </div>

        <% } %>

    </section>


    <section class="detalle-seccion">

        <p class="etiqueta">EDICIONES</p>

        <h2>Ediciones del curso</h2>

        <% if (ediciones == null || ediciones.isEmpty()) { %>

            <p class="texto-secundario">
                No hay ediciones disponibles para este curso.
            </p>

        <% } else { %>

            <div class="ediciones-curso">

                <% for (EdicionCurso edicion : ediciones) { %>

                    <article class="edicion-tarjeta">

                        <h3><%= edicion.getNombre() %></h3>

                        <div class="edicion-datos">

                            <span>
                                <strong>Inicio:</strong>
                                <%= edicion.getFechaInicio() %>
                            </span>

                            <span>
                                <strong>Fin:</strong>
                                <%= edicion.getFechaFin() %>
                            </span>

                            <span>
                                <strong>Cupo:</strong>
                                <%= edicion.getCupo() == -1
                                        ? "Sin límite"
                                        : edicion.getCupo() %>
                            </span>

                        </div>

                    </article>

                <% } %>

            </div>

        <% } %>

    </section>

        <%-- Pueda que sirva mas adelante pero por ahora esta recomendacion no es relevante
        si tocas mas informacion te lleva a la url del curso --%>
        
    <%-- if (curso.getUrl() != null && !curso.getUrl().isBlank()) { %>

        <section class="detalle-seccion">

            <p class="etiqueta">MÁS INFORMACIÓN</p>

            <a class="enlace-curso"
               href="<%= curso.getUrl() %>"
               target="_blank"
               rel="noopener noreferrer">
                Visitar sitio del curso →
            </a>

        </section>

    <% } --%>
    

</main>


<footer class="contenedor pie">
    edEXT
    <span>Aprendizaje que conecta.</span>
</footer>

</body>
</html>
