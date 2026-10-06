<%-- 
    Document   : programas
    Created on : 4 oct 2026, 18:47:45
    Author     : elizeth
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="logica.ProgramaFormacion" %>

<%
    List<ProgramaFormacion> programas =
            (List<ProgramaFormacion>) request.getAttribute("programas");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Programas de Formación - edEXT</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

<header class="cabecera">
    <div class="contenedor cabecera-interior">

        <div class="identidad">
            <a class="marca"
               href="${pageContext.request.contextPath}/inicio"
               aria-label="edEXT, inicio">
                ed<span>EXT</span>
            </a>

            <span class="descripcion-marca">
                Formación y extensión
            </span>
        </div>

               <nav class="navegacion-principal"
                    aria-label="Navegación principal">

                   <a href="${pageContext.request.contextPath}/inicio">
                       Inicio
                   </a>

                   <a href="${pageContext.request.contextPath}/cursos">
                       Cursos
                   </a>

                   <a class="nav-activo"
                      href="${pageContext.request.contextPath}/programas">
                       Programas
                   </a>

               </nav>

        <div class="sesion">

            <% if (session.getAttribute("usuario") != null) { %>

                <div class="usuario-sesion">
                    <span>
                        Hola, ${sessionScope.usuario.nombre}
                    </span>

                    <span class="rol-sesion">
                        ${sessionScope.usuario.rol}
                    </span>
                </div>

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


<main class="contenedor programas-contenedor">

    <section class="encabezado-programas">

        <p class="etiqueta">
            FORMACIÓN
        </p>

        <h1>
            Programas de Formación
        </h1>

        <p class="introduccion">
            Conocé los programas disponibles y los cursos
            que forman parte de cada propuesta.
        </p>

    </section>


    <% if (programas == null || programas.isEmpty()) { %>

        <p class="texto-secundario">
            No hay programas de formación disponibles.
        </p>

    <% } else { %>

        <section class="grilla-programas">

            <% for (ProgramaFormacion programa : programas) { %>

                <article class="programa-tarjeta">

                    <h2>
                        <%= programa.getNombre() %>
                    </h2>

                    <p class="programa-descripcion">
                        <%= programa.getDescripcion() %>
                    </p>

                    <div class="programa-datos">

                        <span>
                            <strong>Inicio:</strong>
                            <%= programa.getFechaInicio() %>
                        </span>

                        <span>
                            <strong>Fin:</strong>
                            <%= programa.getFechaFin() %>
                        </span>

                    </div>
                        
                        
                        <a class="enlace-detalle"
                            href="${pageContext.request.contextPath}/programa?nombre=<%= java.net.URLEncoder.encode(programa.getNombre(), "UTF-8") %>">
                             Ver detalle →
                        </a>

                </article>

            <% } %>

        </section>

    <% } %>


    <a class="volver-inicio"
       href="${pageContext.request.contextPath}/inicio">
        ← Volver al inicio
    </a>

</main>


<footer class="pie">
    <div class="contenedor">
        <p>edEXT · Plataforma de formación y extensión</p>
    </div>
</footer>

</body>
</html>
