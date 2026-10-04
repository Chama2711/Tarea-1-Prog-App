<%-- 
    Document   : detallePrograma
    Created on : 4 oct 2026, 19:05:32
    Author     : elizeth
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.ProgramaFormacion" %>
<%@ page import="logica.Curso" %>
<%@ page import="java.util.Map" %>

<%
    ProgramaFormacion programa =
            (ProgramaFormacion) request.getAttribute("programa");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><%= programa.getNombre() %> - edEXT</title>

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

                <span>Hola, ${sessionScope.usuario.nombre}</span>
                <span>(${sessionScope.usuario.rol})</span>

                <a href="${pageContext.request.contextPath}/logout">
                    Cerrar sesión
                </a>

            <% } else { %>

                <a href="${pageContext.request.contextPath}/login">
                    Iniciar sesión
                </a>

            <% } %>

        </div>

    </div>
</header>


<main class="contenedor detalle-curso">

    <a class="volver-inicio"
       href="${pageContext.request.contextPath}/programas">
        ← Volver a programas
    </a>


    <section class="detalle-encabezado">

        <p class="etiqueta">
            PROGRAMA DE FORMACIÓN
        </p>

        <h1>
            <%= programa.getNombre() %>
        </h1>

        <p class="detalle-descripcion">
            <%= programa.getDescripcion() %>
        </p>

    </section>


    <section class="detalle-datos">

        <div class="dato-curso">
            <span>Fecha de inicio</span>
            <strong>
                <%= programa.getFechaInicio() %>
            </strong>
        </div>

        <div class="dato-curso">
            <span>Fecha de finalización</span>
            <strong>
                <%= programa.getFechaFin() %>
            </strong>
        </div>

        <div class="dato-curso">
            <span>Fecha de alta</span>
            <strong>
                <%= programa.getFechaAlta() %>
            </strong>
        </div>

        <div class="dato-curso">
            <span>Cursos</span>
            <strong>
                <%= programa.getCursos().size() %>
            </strong>
        </div>

    </section>


    <section class="detalle-seccion">

        <p class="etiqueta">
            CURSOS
        </p>

        <h2>
            Cursos del programa
        </h2>

        <% if (programa.getCursos() == null
                || programa.getCursos().isEmpty()) { %>

            <p class="texto-secundario">
                Este programa todavía no tiene cursos asociados.
            </p>

        <% } else { %>

            <div class="cursos-programa">

                <% for (Map.Entry<String, Curso> entrada
                        : programa.getCursos().entrySet()) {

                    Curso curso = entrada.getValue();
                %>

                    <article class="curso-programa-tarjeta">

                        <div>
                            <h3>
                                <%= curso.getNombre() %>
                            </h3>

                            <p>
                                <%= curso.getDescripcion() %>
                            </p>
                        </div>

                        <a class="enlace-detalle"
                           href="${pageContext.request.contextPath}/curso?nombre=<%= java.net.URLEncoder.encode(curso.getNombre(), "UTF-8") %>">
                            Ver curso →
                        </a>

                    </article>

                <% } %>

            </div>

        <% } %>

    </section>

</main>


<footer class="pie">
    <div class="contenedor">
        <p>edEXT · Plataforma de formación y extensión</p>
    </div>
</footer>

</body>
</html>