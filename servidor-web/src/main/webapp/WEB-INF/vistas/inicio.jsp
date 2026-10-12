<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%

    String tituloPagina = "Un espacio para seguir aprendiendo.";
    String bajadaPagina = "Cursos y programas de formación para compartir conocimientos y descubrir nuevas oportunidades.";
    String paginaActiva = "inicio";
%>
<%@ page import="logica.DTAutenticacion" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>


<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
    <section class="oferta-lista" aria-label="Oferta de formación">
    <% for (Curso oferta : (List<Curso>) request.getAttribute("cursos")) { %>
        <article class="oferta-fila"><img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Curso sin imagen"><div><span class="categoria-chip">Curso</span><h2><%= escapar(oferta.getNombre()) %></h2><p><%= escapar(oferta.getDescripcion()) %></p><a class="boton boton-pequeno" href="${pageContext.request.contextPath}/curso?nombre=<%= parametro(oferta.getNombre()) %>">Ver detalle</a></div></article>
    <% } %>
    <% for (ProgramaFormacion oferta : (List<ProgramaFormacion>) request.getAttribute("programas")) { %>
        <article class="oferta-fila"><img src="${pageContext.request.contextPath}/imagenes/formacion.svg" alt="Programa sin imagen"><div><span class="categoria-chip">Programa</span><h2><%= escapar(oferta.getNombre()) %></h2><p><%= escapar(oferta.getDescripcion()) %></p><a class="boton boton-pequeno" href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(oferta.getNombre()) %>">Ver detalle</a></div></article>
    <% } %>
    </section>
    <% if (usuarioSesion == null) { %>
        <p class="invitacion">¿Todavía no tenés cuenta? <a href="${pageContext.request.contextPath}/registro">Creá tu cuenta</a></p>
    <% } %>
</div>
</main>
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>

        <div class="sesion">

            <% if (session.getAttribute("usuario") != null) { %>

                <span>
                    Hola, ${sessionScope.usuario.nombre}
                </span>

                <span>
                    (${sessionScope.usuario.rol})
                </span>

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
    <main class="contenedor">
        <section class="bienvenida" aria-labelledby="titulo">
            <p class="etiqueta">BIENVENIDO A edEXT</p>
            <h1 id="titulo">Un espacio para<br>seguir aprendiendo.</h1>
            <p class="introduccion">Cursos y programas de formación para compartir conocimientos y descubrir nuevas oportunidades.</p>
            <% if (session.getAttribute("usuario") != null) { %>

            <div class="aviso">
                <span class="indicador" aria-hidden="true"></span>
                <p>
                    Sesión iniciada como
                    <strong>${sessionScope.usuario.nick}</strong>.
                </p>
            </div>

            <% } else { %>

            <div class="aviso">
                <span class="indicador" aria-hidden="true"></span>
                <p>
                    Iniciá sesión para acceder a las funcionalidades
                    de la plataforma.
                </p>
            </div>

            <% } %>
            
        </section>
        <section class="oferta" aria-labelledby="titulo-oferta">
            <div class="titulo-seccion">
                <h2 id="titulo-oferta">Nuestra oferta de formación</h2>
                <p>Un primer vistazo a edEXT.</p>
            </div>
            <div class="tarjetas">
                <article class="tarjeta">
                       <span class="numero">${cantidadCursos}</span>

                        <h3>Cursos</h3>

                        <p>
                            Propuestas para ampliar tus conocimientos.
                        </p>

                        <a class="boton-tarjeta"
                           href="${pageContext.request.contextPath}/cursos">
                            Ver cursos →
                        </a>
			<%
    			DTAutenticacion usuario = (DTAutenticacion) session.getAttribute("usuario");

    			if (usuario != null && usuario.getRol() == DTAutenticacion.Rol.DOCENTE) {
			%>

    			<a class="boton-tarjeta"
       			href="${pageContext.request.contextPath}/alta-curso">
       			 Crear curso +
    			</a>

    			<a class="boton-tarjeta"
       			href="${pageContext.request.contextPath}/alta-edicion-curso">
        		Crear edición +
    			</a>

			<% } %>
			
                </article>
                <article class="tarjeta">
                    <span class="numero">${cantidadProgramas}</span>
                    
                    <h3>Programas de formación</h3>
                    
                    <p>Recorridos formativos.</p>
                    
                    <a class="boton-tarjeta"
                        href="${pageContext.request.contextPath}/programas">
                         Ver programas →
                    </a>
                    
                </article>
                <article class="tarjeta">
                    <span class="numero">${cantidadCategorias}</span>
                    <h3>Categorías</h3>
                    <p>Áreas para explorar según tus intereses.</p>
                </article>
            </div>
        </section>
    </main>
    <footer class="contenedor pie">edEXT <span>Aprendizaje que conecta.</span></footer>
</body>
</html>
