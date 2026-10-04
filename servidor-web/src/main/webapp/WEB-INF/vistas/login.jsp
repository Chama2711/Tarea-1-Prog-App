<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Iniciar sesión | edEXT</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>

    <header class="cabecera">
        <div class="contenedor cabecera-interior">

            <div>
                <a class="marca"
                   href="${pageContext.request.contextPath}/inicio"
                   aria-label="edEXT, inicio">
                    ed<span>EXT</span>
                </a>

                <span class="descripcion-marca">
                    Formación y extensión
                </span>
            </div>

        </div>
    </header>


    <main class="contenedor login-contenedor">

        <section class="login-presentacion">

            <p class="etiqueta">BIENVENIDO DE NUEVO</p>

            <h1>Iniciar sesión</h1>

            <p class="introduccion">
                Ingresá a edEXT para acceder a las funcionalidades
                de la plataforma.
            </p>


            <div class="login-tarjeta">

                <% if (request.getAttribute("error") != null) { %>

                    <div class="login-error">
                        <%= request.getAttribute("error") %>
                    </div>

                <% } %>


                <form method="post"
                      action="${pageContext.request.contextPath}/login"
                      class="login-formulario">

                    <div class="campo-login">

                        <label for="identificador">
                            Nickname o correo electrónico
                        </label>

                        <input
                            type="text"
                            id="identificador"
                            name="identificador"
                            value="${identificador}"
                            placeholder="Nickname o correo"
                            required
                            autofocus>

                    </div>


                    <div class="campo-login">

                        <label for="clave">
                            Contraseña
                        </label>

                        <input
                            type="password"
                            id="clave"
                            name="clave"
                            placeholder="Contraseña"
                            required>

                    </div>


                    <button type="submit"
                            class="boton-login">
                        Iniciar sesión
                    </button>

                </form>

            </div>


            <a class="volver-inicio"
               href="${pageContext.request.contextPath}/inicio">
                ← Volver al inicio
            </a>

        </section>

    </main>


    <footer class="contenedor pie">
        edEXT
        <span>Aprendizaje que conecta.</span>
    </footer>

</body>
</html>