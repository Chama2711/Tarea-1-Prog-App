<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.MovilEdicionesServlet.EdicionConCurso,web.soap.generado.DtCursoConsulta,java.util.*" %>
<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>
<%
    String paginaActiva = "ediciones";
    List<EdicionConCurso> ediciones = (List<EdicionConCurso>) request.getAttribute("ediciones");
    List<DtCursoConsulta> cursosDisponibles = (List<DtCursoConsulta>) request.getAttribute("cursosDisponibles");
    List<String> institutos = (List<String>) request.getAttribute("navInstitutos");
    List<String> categorias = (List<String>) request.getAttribute("navCategorias");
    String instSel = (String) request.getAttribute("institutoSeleccionado");
    String catSel = (String) request.getAttribute("categoriaSeleccionada");
    String cursoSel = (String) request.getAttribute("cursoSeleccionado");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ver Ediciones | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=2">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <section class="movil-banner">
        <h1>Ver Ediciones</h1>
        <p><%= ediciones != null ? ediciones.size() : 0 %> ediciones encontradas</p>
    </section>

    <section class="movil-panel">
        <form method="get" action="${pageContext.request.contextPath}/movil/ediciones" class="movil-filtros">
            <div>
                <label for="ed-instituto">Instituto</label>
                <select id="ed-instituto" name="instituto">
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
                <label for="ed-categoria">Categoría</label>
                <select id="ed-categoria" name="categoria">
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

            <div>
                <label for="ed-curso">Curso</label>
                <select id="ed-curso" name="curso">
                    <option value="">Todos los cursos</option>
                    <% if (cursosDisponibles != null) {
                        for (DtCursoConsulta c : cursosDisponibles) { %>
                            <option value="<%= escapar(c.getNombre()) %>" <%= c.getNombre().equals(cursoSel) ? "selected" : "" %>>
                                <%= escapar(c.getNombre()) %>
                            </option>
                    <%  }
                       } %>
                </select>
            </div>

            <div style="display: flex; gap: 0.5rem; align-items: center;">
                <button type="submit" class="movil-btn-primario" style="flex: 1;">Aplicar</button>
                <% if ((instSel != null && !instSel.isBlank()) || (catSel != null && !catSel.isBlank()) || (cursoSel != null && !cursoSel.isBlank())) { %>
                    <a href="${pageContext.request.contextPath}/movil/ediciones"
                       style="padding: 0 0.75rem; color: var(--edext-cyan-hover); font-weight: 600;">Limpiar</a>
                <% } %>
            </div>
        </form>
    </section>

    <% if (ediciones == null || ediciones.isEmpty()) { %>
        <div class="aviso">No se encontraron ediciones con los filtros seleccionados.</div>
    <% } else { %>
        <section class="movil-grilla" aria-label="Listado de ediciones">
            <% for (EdicionConCurso item : ediciones) {
                var ed = item.edicion();
                String urlEd = request.getContextPath() + "/movil/edicion?nombre=" + parametro(ed.getNombre());
            %>
                <article class="movil-tarjeta">
                    <a class="movil-tarjeta-enlace-foto" href="<%= urlEd %>">
                        <img class="movil-tarjeta-img"
                             src="${pageContext.request.contextPath}/imagen-edicion?nombre=<%= parametro(ed.getNombre()) %>"
                             alt="Imagen de <%= escapar(ed.getNombre()) %>">
                    </a>

                    <div class="movil-tarjeta-cuerpo">
                        <div style="display: flex; flex-wrap: wrap; gap: 0.4rem; align-items: center;">
                            <span class="movil-chip">Edición</span>
                            <span class="texto-secundario" style="font-size: 0.82rem; font-weight: 600;">
                                <%= escapar(item.nombreCurso()) %>
                            </span>
                        </div>

                        <h2 class="movil-tarjeta-titulo">
                            <a href="<%= urlEd %>"><%= escapar(ed.getNombre()) %></a>
                        </h2>

                        <p class="texto-secundario" style="margin: 0; font-size: 0.88rem;">
                            <strong>Inicio:</strong> <%= fecha(ed.getFechaInicio()) %> &bull;
                            <strong>Fin:</strong> <%= fecha(ed.getFechaFin()) %>
                        </p>

                        <div style="margin-top: auto; padding-top: 0.5rem;">
                            <a class="movil-btn-primario movil-btn-ancho" href="<%= urlEd %>">
                                Ver edición
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