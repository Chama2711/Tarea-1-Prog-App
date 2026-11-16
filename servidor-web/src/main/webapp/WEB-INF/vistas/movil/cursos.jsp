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
    <title>Cursos | edEXT Móvil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movil.css?v=1">
</head>
<body class="movil-body">
<%@ include file="/WEB-INF/vistas/movil/cabeceraMovil.jspf" %>

<main class="movil-contenedor">
    <h1 style="font-size: 1.35rem; margin: 0 0 0.85rem;">Consulta de Cursos</h1>

    <form method="get" action="${pageContext.request.contextPath}/movil/cursos" class="movil-filtros">
        <div class="campo" style="margin: 0;">
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

        <div class="campo" style="margin: 0;">
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

        <button type="submit" class="boton movil-boton-ancho">Filtrar</button>
    </form>

    <% if (request.getAttribute("error") != null) { %>
        <div class="aviso aviso-error"><%= escapar(request.getAttribute("error")) %></div>
    <% } %>

    <% if (cursos == null || cursos.isEmpty()) { %>
        <div class="aviso">No se encontraron cursos con los filtros seleccionados.</div>
    <% } else { %>
        <section class="movil-lista" aria-label="Listado de cursos">
            <% for (DtCursoConsulta curso : cursos) { %>
                <article class="movil-tarjeta">
                    <% if (curso.isTieneImagen()) { %>
                        <img class="movil-tarjeta-img"
                             src="${pageContext.request.contextPath}/imagen-curso?nombre=<%= parametro(curso.getNombre()) %>"
                             alt="Imagen de <%= escapar(curso.getNombre()) %>">
                    <% } else { %>
                        <img class="movil-tarjeta-img"
                             src="${pageContext.request.contextPath}/imagenes/formacion.svg"
                             alt="Curso sin imagen">
                    <% } %>

                    <div class="movil-tarjeta-cuerpo">
                        <% if (curso.getNombreInstituto() != null) { %>
                            <span class="instituto" style="align-self: flex-start;"><%= escapar(curso.getNombreInstituto()) %></span>
                        <% } %>
                        <h2><%= escapar(curso.getNombre()) %></h2>
                        <p class="curso-descripcion" style="margin: 0;"><%= escapar(curso.getDescripcion()) %></p>

                        <div class="movil-datos-grid">
                            <div class="movil-dato-item">
                                <span>Duración</span>
                                <strong><%= escapar(curso.getDuracion()) %></strong>
                            </div>
                            <div class="movil-dato-item">
                                <span>Créditos</span>
                                <strong><%= curso.getCreditos() %></strong>
                            </div>
                        </div>

                        <a class="boton movil-boton-ancho"
                           style="display: inline-flex; justify-content: center; align-items: center; text-decoration: none;"
                           href="${pageContext.request.contextPath}/movil/curso?nombre=<%= parametro(curso.getNombre()) %>">
                            Ver detalle del curso
                        </a>
                    </div>
                </article>
            <% } %>
        </section>
    <% } %>
</main>
</body>
</html>