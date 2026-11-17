<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.soap.generado.DtCursoConsulta,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "cursos";
    List<DtCursoConsulta> cursos = (List<DtCursoConsulta>) request.getAttribute("cursos");
    List<String> institutos = (List<String>) request.getAttribute("navInstitutos");
    List<String> categorias = (List<String>) request.getAttribute("navCategorias");
    String instSel = (String) request.getAttribute("institutoSeleccionado");
    String catSel = (String) request.getAttribute("categoriaSeleccionada");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ver Cursos | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <section class="movil-banner">
        <h1>Ver Cursos</h1>
        <p><%= cursos != null ? cursos.size() : 0 %> cursos disponibles</p>
    </section>

    <section class="movil-panel">
        <form method="get" action="${pageContext.request.contextPath}/movil/cursos" class="movil-filtros">
            <div>
                <label for="filtro-instituto">Instituto</label>
                <select id="filtro-instituto" name="instituto">
                    <option value="">Todos los institutos</option>
                    <% if (institutos != null) {
                        for (String inst : institutos) { %>
                            <option value="<%= escapar(inst) %>" <%= inst.equals(instSel) ? "selected" : "" %>>
                                <%= escapar(inst) %>
                            </option>
                    <%  }
                       } %>
                </select>
            </div>

            <div>
                <label for="filtro-categoria">Categoría</label>
                <select id="filtro-categoria" name="categoria">
                    <option value="">Todas las categorías</option>
                    <% if (categorias != null) {
                        for (String cat : categorias) { %>
                            <option value="<%= escapar(cat) %>" <%= cat.equals(catSel) ? "selected" : "" %>>
                                <%= escapar(cat) %>
                            </option>
                    <%  }
                       } %>
                </select>
            </div>

            <div style="display: flex; gap: 0.5rem; align-items: center;">
                <button type="submit" class="movil-btn-primario" style="flex: 1;">Aplicar</button>
                <% if ((instSel != null && !instSel.isBlank()) || (catSel != null && !catSel.isBlank())) { %>
                    <a href="${pageContext.request.contextPath}/movil/cursos"
                       style="padding: 0 0.75rem; color: var(--edext-cyan-hover); font-weight: 600;">Limpiar</a>
                <% } %>
            </div>
        </form>
    </section>

    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error"><%= escapar(request.getAttribute("error")) %></div>
    <% } %>

    <% if (cursos == null || cursos.isEmpty()) { %>
        <div class="aviso">No se encontraron cursos con los filtros seleccionados.</div>
    <% } else { %>
        <section class="movil-grilla" aria-label="Listado de cursos">
            <% for (DtCursoConsulta curso : cursos) {
                String urlDetalle = request.getContextPath() + "/movil/curso?nombre=" + parametro(curso.getNombre());
            %>
                <article class="movil-tarjeta">
                    <a class="movil-tarjeta-enlace-foto" href="<%= urlDetalle %>">
                        <% if (curso.isTieneImagen()) { %>
                            <img class="movil-tarjeta-img"
                                 src="${pageContext.request.contextPath}/imagen-curso?nombre=<%= parametro(curso.getNombre()) %>"
                                 alt="Imagen de <%= escapar(curso.getNombre()) %>">
                        <% } else { %>
                            <img class="movil-tarjeta-img"
                                 src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                                 alt="Curso sin imagen">
                        <% } %>
                    </a>

                    <div class="movil-tarjeta-cuerpo">
                        <div style="display: flex; flex-wrap: wrap; gap: 0.4rem; align-items: center;">
                            <span class="movil-chip">Curso</span>
                            <% if (curso.getNombreInstituto() != null) { %>
                                <span class="texto-secundario" style="font-size: 0.82rem; font-weight: 600;">
                                    <%= escapar(curso.getNombreInstituto()) %>
                                </span>
                            <% } %>
                        </div>

                        <h2 class="movil-tarjeta-titulo">
                            <a href="<%= urlDetalle %>"><%= escapar(curso.getNombre()) %></a>
                        </h2>

                        <p class="curso-descripcion" style="margin: 0;"><%= escapar(curso.getDescripcion()) %></p>

                        <div style="margin-top: auto; padding-top: 0.5rem;">
                            <a class="movil-btn-primario movil-btn-ancho" href="<%= urlDetalle %>">
                                Ver detalle
                            </a>
                        </div>
                    </div>
                </article>
            <% } %>
        </section>
    <% } %>
</main>
</body>
</html>