<%-- 
    Document   : cursos
    Created on : 3 oct 2026, 22:01:31
    Author     : elizeth
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="logica.Curso" %>

<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Cursos | edEXT</title>

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

               <a class="nav-activo"
                  href="${pageContext.request.contextPath}/cursos">
                   Cursos
               </a>

               <a href="${pageContext.request.contextPath}/programas">
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


<main class="contenedor cursos-contenedor">

    <section class="encabezado-cursos">

        <p class="etiqueta">FORMACIÓN</p>

        <h1>Cursos disponibles</h1>

        <p class="introduccion">
            Explorá los cursos disponibles en edEXT y encontrá
            nuevas oportunidades de aprendizaje.
        </p>

    </section>


    <section class="grilla-cursos">

        <% if (cursos == null || cursos.isEmpty()) { %>

            <div class="aviso">
                <span class="indicador"></span>
                <p>No hay cursos disponibles actualmente.</p>
            </div>

        <% } else { %>

            <% for (Curso curso : cursos) { %>
                     
                <article class="curso-tarjeta">

                    <h2>
                        <%= curso.getNombre() %>
                    </h2>

                    <p class="curso-descripcion">
                        <%= curso.getDescripcion() %>
                    </p>

                    <div class="curso-datos">

                        <span>
                            <strong>Duración:</strong>
                            <%= curso.getDuracion() %>
                        </span>

                        <span>
                            <strong>Créditos:</strong>
                            <%= curso.getCreditos() %>
                        </span>

                    </div>
                        
                    <a class="enlace-detalle"
                     href="${pageContext.request.contextPath}/curso?nombre=<%= java.net.URLEncoder.encode(curso.getNombre(), "UTF-8") %>">
                     Ver detalle →
                    </a>

                </article>

            <% } %>

        <% } %>

    </section>

    <a class="volver-inicio"
       href="${pageContext.request.contextPath}/inicio">
        ← Volver al inicio
    </a>

</main>


<footer class="contenedor pie">
    edEXT
    <span>Aprendizaje que conecta.</span>
</footer>

</body>
</html>