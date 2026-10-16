<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="logica.*,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    DTPerfilUsuario perfil = (DTPerfilUsuario) request.getAttribute("perfil");
    DTUsuarioConsulta usuario = perfil.usuario();
    boolean docente = usuario.rol() == DTAutenticacion.Rol.DOCENTE;
    String tituloPagina = perfil.propio() ? "Tu espacio en edEXT." : "Conocé su recorrido.";
    String bajadaPagina = "Datos personales y participación en la comunidad.";
    String paginaActiva = "usuarios";
    

    // Obtención de datos para la funcionalidad Seguir/Seguidos
    Boolean esSeguidorObj = (Boolean) request.getAttribute("esSeguidor");
    boolean esSeguidor = esSeguidorObj != null && esSeguidorObj;
    List<Usuario> listaSeguidos = (List<Usuario>) request.getAttribute("listaSeguidos");
    if (listaSeguidos == null) {
        listaSeguidos = new ArrayList<>();
    }
    DTAutenticacion autenticado = (DTAutenticacion) session.getAttribute("usuario");
    
    // Mensajes de feedback tras modificar datos
    String mensajeExito = (String) session.getAttribute("mensajeExito");
    String mensajeError = (String) session.getAttribute("mensajeError");
    if (mensajeExito != null) session.removeAttribute("mensajeExito");
    if (mensajeError != null) session.removeAttribute("mensajeError");
%>



<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= escapar(tituloPagina) %> | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <script src="${pageContext.request.contextPath}/js/perfil.js?v=3" defer></script>
</head>
<body>
<%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>

<main id="contenido" class="contenedor contenido disposicion-web">
<%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>
<div class="contenido-pagina">
<%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>

    <a class="volver" href="${pageContext.request.contextPath}/usuarios?tipo=<%= docente ? "docentes" : "estudiantes" %>">← Volver a usuarios</a>
    
    <%-- Avisos de éxito o error --%>
    <% if (mensajeExito != null) { %>
        <div class="aviso aviso-exito" role="alert"><%= escapar(mensajeExito) %></div>
    <% } %>
    <% if (mensajeError != null) { %>
        <div class="aviso aviso-error" role="alert"><%= escapar(mensajeError) %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error" role="alert"><%= escapar(request.getAttribute("error")) %></div>
    <% } %>
    
    <section class="panel perfil-encabezado" aria-label="Perfil de usuario">
        <img class="avatar-usuario avatar-grande foto-ampliable" src="${pageContext.request.contextPath}/<%= usuario.tieneImagen() ? "imagen-usuario?nick=" + parametro(usuario.nick()) : "imagenes/avatar.svg" %>" alt="Foto de perfil de <%= escapar(usuario.nombreCompleto()) %>" width="120" height="120" role="button" tabindex="0" aria-label="Ampliar foto de perfil" aria-haspopup="dialog" aria-controls="visor-foto" title="Doble clic para ampliar la foto">
        <div class="perfil-identidad">
            <span class="categoria-chip"><%= docente ? "Docente" : "Estudiante" %></span>
            <h2><%= escapar(usuario.nombreCompleto()) %></h2>
            <p class="texto-secundario">@<%= escapar(usuario.nick()) %><%= perfil.propio() ? " · Mi perfil" : "" %></p>
            
            <%-- Botón Seguir / Dejar de seguir --%>
            <% if (autenticado != null && !perfil.propio()) { %>
                <form action="${pageContext.request.contextPath}/Seguir" method="POST" style="margin-top: 10px;">
                    <input type="hidden" name="nickDestino" value="<%= parametro(usuario.nick()) %>">
                    <% if (esSeguidor) { %>
                        <input type="hidden" name="accion" value="dejar_seguir">
                        <button type="submit" class="boton boton-secundario">Dejar de seguir</button>
                    <% } else { %>
                        <input type="hidden" name="accion" value="seguir">
                        <button type="submit" class="boton boton-principal">Seguir</button>
                    <% } %>
                </form>
            <% } %>
        </div>
    </section>
    <section class="panel perfil-contenido" aria-label="Información del usuario">

        <nav class="perfil-pestanas" aria-label="Secciones del perfil">
            <a id="tab-general" href="#perfil-general">General</a>
            <a id="tab-ediciones" href="#perfil-ediciones">Ediciones</a>
            <% if (!docente) { %><a id="tab-programas" href="#perfil-programas">Programas</a><% } %>
            <% if (docente && perfil.propio()) { %><a id="tab-aceptados" href="#perfil-aceptados">Estudiantes aceptados</a><% } %>
        
            <a id="tab-seguidos" href="#perfil-seguidos">Seguidos</a>
        </nav>
        
        <section id="perfil-general" class="perfil-panel" aria-labelledby="tab-general">
            <% if (perfil.propio()) { %>
                <%-- Formulario editable si es mi propio perfil --%>
                <form action="${pageContext.request.contextPath}/modificarUsuario" method="POST" id="formModificarUsuario" class="perfil-form-editable">
                    
                    <%-- Nickname (Fijo / No editable) --%>
                    <div class="fila-dato deshabilitado">
                        <span class="label">Nickname</span>
                        <span class="valor-fijo">@<%= escapar(usuario.nick()) %></span>
                    </div>

                    <%-- Nombre (Editable con lápiz) --%>
                    <div class="fila-dato">
                        <label for="nombre" class="label">Nombre</label>
                        <div class="input-contenedor">
                            <input type="text" id="nombre" name="nombre" value="<%= escapar(usuario.nombre()) %>" class="input-lectura" readonly required />
                            <button type="button" class="btn-lapiz" onclick="habilitarCampo('nombre')" title="Editar Nombre">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            </button>
                        </div>
                    </div>

                    <%-- Apellido (Editable con lápiz) --%>
                    <div class="fila-dato">
                        <label for="apellido" class="label">Apellido</label>
                        <div class="input-contenedor">
                            <input type="text" id="apellido" name="apellido" value="<%= escapar(usuario.apellido()) %>" class="input-lectura" readonly required />
                            <button type="button" class="btn-lapiz" onclick="habilitarCampo('apellido')" title="Editar Apellido">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            </button>
                        </div>
                    </div>

                    <%-- Correo electrónico (Fijo / No editable) --%>
                    <div class="fila-dato deshabilitado">
                        <span class="label">Correo electrónico</span>
                        <span class="valor-fijo"><%= escapar(usuario.correo()) %></span>
                    </div>

                    <%-- Fecha de nacimiento (Editable con lápiz) --%>
                    <div class="fila-dato">
                        <label for="fechaNacimiento" class="label">Fecha de nacimiento</label>
                        <div class="input-contenedor">
                            <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="<%= usuario.fechaNacimiento() != null ? usuario.fechaNacimiento().toString() : "" %>" class="input-lectura" readonly required />
                            <button type="button" class="btn-lapiz" onclick="habilitarCampo('fechaNacimiento')" title="Editar Fecha">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            </button>
                        </div>
                    </div>

                    <% if (docente) { %>
                        <div class="fila-dato deshabilitado">
                            <span class="label">Instituto</span>
                            <span class="valor-fijo"><%= usuario.institutos().isEmpty() ? "No indicado" : escapar(String.join(", ", usuario.institutos())) %></span>
                        </div>
                    <% } %>

                    <div id="contenedor-guardar" class="acciones-guardar" style="display: none; margin-top: 20px;">
                        <button type="submit" class="boton boton-principal">Guardar Cambios</button>
                    </div>
                </form>
            <% } else { %>
                <%-- Vista fija estándar para usuarios ajenos --%>
                <div class="perfil-datos-fijos">
                    <dl class="perfil-datos">
                        <div><dt>Nombre</dt><dd><%= escapar(usuario.nombre()) %></dd></div>
                        <div><dt>Apellido</dt><dd><%= escapar(usuario.apellido()) %></dd></div>
                        <div><dt>Correo electrónico</dt><dd><%= escapar(usuario.correo()) %></dd></div>
                        <div><dt>Fecha de nacimiento</dt><dd><%= fecha(usuario.fechaNacimiento()) %></dd></div>
                        <% if (docente) { %>
                            <div><dt>Instituto</dt><dd><%= usuario.institutos().isEmpty() ? "No indicado" : escapar(String.join(", ", usuario.institutos())) %></dd></div>
                        <% } %>
                    </dl>
                </div>
            <% } %>
        </section>
        <section id="perfil-ediciones" class="perfil-panel" aria-labelledby="titulo-participacion">
            <h3 id="titulo-participacion"><%= docente ? "Ediciones en las que participa" : "Inscripciones a ediciones de cursos" %></h3>
            <% if (docente) { %>
                <% if (perfil.ediciones().isEmpty()) { %><p class="texto-secundario">Este docente no participa en ediciones de cursos.</p><% } %>
                <div class="ediciones">
                <% for (DTEdicionConsulta edicion : perfil.ediciones()) { %>
                    <article class="edicion">
                        <h4><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(edicion.nombre()) %>"><%= escapar(edicion.nombre()) %></a></h4>
                        <p><%= escapar(edicion.nombreCurso()) %></p>
                        <div class="edicion-datos"><span><strong>Inicio:</strong> <%= fecha(edicion.inicio()) %></span><span><strong>Fin:</strong> <%= fecha(edicion.fin()) %></span></div>
                    </article>
                <% } %>
                </div>
            <% } else { %>
                <% if (perfil.propio()) { %><p class="texto-secundario">Aquí también podés ver tus inscripciones rechazadas.</p><% } %>
                <% if (perfil.inscripciones().isEmpty()) { %><p class="texto-secundario">No hay inscripciones para mostrar.</p><% } %>
                <div class="ediciones">
                <% for (DTInscripcion inscripcion : perfil.inscripciones()) { %>
                    <article class="edicion">
                        <h4><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(inscripcion.getNombreEdicion()) %>"><%= escapar(inscripcion.getNombreEdicion()) %></a></h4>
                        <p><%= escapar(inscripcion.getNombreCurso()) %></p>
                        <div class="edicion-datos">
                            <span><strong>Fecha de inscripción:</strong> <%= fecha(inscripcion.getFechaInscripcion()) %></span>
                            <span class="estado-inscripcion"><%= inscripcion.getEstado() == EstadoInscripcion.ACEPTADA ? "Aceptada" : inscripcion.getEstado() == EstadoInscripcion.RECHAZADA ? "Rechazada" : "Inscripto" %></span>
                        </div>
                    </article>
                <% } %>
                </div>
            <% } %>
        </section>
        <% if (!docente) { %>
        <section id="perfil-programas" class="perfil-panel" aria-labelledby="titulo-programas-usuario">
            <h3 id="titulo-programas-usuario">Programas en los que está inscripto</h3>
            <% if (perfil.programas().isEmpty()) { %><p class="texto-secundario">No hay inscripciones a programas para mostrar.</p><% } %>
            <div class="ediciones">
            <% for (String programa : perfil.programas()) { %>
                <article class="edicion"><h4><a href="${pageContext.request.contextPath}/programa?nombre=<%= parametro(programa) %>"><%= escapar(programa) %></a></h4></article>
            <% } %>
            </div>
        </section>
        <% } %>
        <% if (docente && perfil.propio()) { %>
        <section id="perfil-aceptados" class="perfil-panel" aria-labelledby="titulo-aceptados">
            <h3 id="titulo-aceptados">Estudiantes aceptados en tus ediciones</h3>
            <% if (perfil.aceptados().isEmpty()) { %><p class="texto-secundario">Todavía no hay estudiantes aceptados para mostrar.</p><% } %>
            <div class="ediciones">
            <% for (DTInscripcion aceptado : perfil.aceptados()) { %>
                <article class="edicion">
                    <h4><a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(aceptado.getNicknameEstudiante()) %>"><%= escapar(aceptado.getNombreEstudiante()) %></a></h4>
                    <p><a href="${pageContext.request.contextPath}/edicion?nombre=<%= parametro(aceptado.getNombreEdicion()) %>"><%= escapar(aceptado.getNombreEdicion()) %></a></p>
                    <div class="edicion-datos"><span><strong>Fecha de inscripción:</strong> <%= fecha(aceptado.getFechaInscripcion()) %></span><span class="estado-inscripcion">Aceptada</span></div>
                </article>
            <% } %>
            </div>
        </section>
        <% } %>
        
        <%-- Panel con la lista de usuarios seguidos --%>
        <section id="perfil-seguidos" class="perfil-panel" aria-labelledby="tab-seguidos">
            <h3 id="titulo-seguidos">Usuarios que sigue</h3>
            <% if (listaSeguidos.isEmpty()) { %>
                <p class="texto-secundario">Este usuario no sigue a nadie actualmente.</p>
            <% } else { %>
                <div class="ediciones">
                <% for (Usuario uSeguido : listaSeguidos) { %>
                    <article class="edicion">
                        <h4>
                            <a href="${pageContext.request.contextPath}/usuario?nick=<%= parametro(uSeguido.getNick()) %>">
                                <%= escapar(uSeguido.getNombre()) %> <%= escapar(uSeguido.getApellido()) %>
                            </a>
                        </h4>
                        <p class="texto-secundario">@<%= escapar(uSeguido.getNick()) %></p>
                    </article>
                <% } %>
                </div>
            <% } %>
        </section>
    </section>

</div>
</main>
<dialog id="visor-foto" class="visor-foto" aria-labelledby="titulo-visor-foto">
    <div class="visor-foto-cabecera">
        <h2 id="titulo-visor-foto">Foto de perfil</h2>
        <form method="dialog"><button class="boton boton-secundario boton-pequeno" autofocus>Cerrar</button></form>
    </div>
    <img id="foto-ampliada" alt="Foto de perfil de <%= escapar(usuario.nombreCompleto()) %>">
</dialog>
    
<script>
function habilitarCampo(idCampo) {
    const input = document.getElementById(idCampo);
    if (input) {
        input.removeAttribute('readonly');
        input.classList.add('en-edicion');
        input.focus();

        const contenedorGuardar = document.getElementById('contenedor-guardar');
        if (contenedorGuardar) {
            contenedorGuardar.style.display = 'block';
        }
    }
}
</script>
    
<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
