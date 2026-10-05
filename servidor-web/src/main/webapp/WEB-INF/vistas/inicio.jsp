<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
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

            <a class="nav-activo"
               href="${pageContext.request.contextPath}/inicio">
                Inicio
            </a>

            <a href="${pageContext.request.contextPath}/cursos">
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
