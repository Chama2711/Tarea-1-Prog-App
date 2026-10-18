<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="logica.EdicionCurso"%>
<%@page import="logica.DTInscripcion"%>

<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>

<%
    List<String> institutos = (List<String>) request.getAttribute("institutos");

    List<String> cursos = (List<String>) request.getAttribute("cursos");

    List<EdicionCurso> ediciones = (List<EdicionCurso>) request.getAttribute("ediciones");

    List<DTInscripcion> aceptados = (List<DTInscripcion>) request.getAttribute("aceptados");
    
    String institutoSeleccionado = (String) request.getAttribute("institutoSeleccionado");

    String cursoSeleccionado = (String) request.getAttribute("cursoSeleccionado");

    String edicionSeleccionada = (String) request.getAttribute("edicionSeleccionada");

    EdicionCurso edicionElegida = (EdicionCurso) request.getAttribute("edicionElegida");
    
    
    String tituloPagina = "Estudiantes aceptados";
    String bajadaPagina = "Consultá los estudiantes aceptados en las ediciones de tus cursos.";
    String paginaActiva = "cursos";
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Estudiantes aceptados | edEXT</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/estilos.css?v=7">
    </head>

    <body>
        
        <%@ include file="/WEB-INF/vistas/fragmentos/cabecera.jspf" %>
        
        <main id="contenido" class="contenedor contenido disposicion-web">

            <%@ include file="/WEB-INF/vistas/fragmentos/lateral.jspf" %>

            <div class="contenido-pagina">
                
                <%@ include file="/WEB-INF/vistas/fragmentos/intro.jspf" %>
                
                <section class="panel">

                    <h2>Seleccionar instituto y curso</h2>

                    <form action="${pageContext.request.contextPath}/listar-aceptados"
                          method="GET">
                        
                        <div class="campo">
                            <label for="instituto">Instituto:</label>

                            <select name="instituto" id="instituto" required>
                                <option value="">Seleccione un instituto</option>

                                <% for (String instituto : institutos) { %>

                                    <option value="<%= escapar(instituto) %>"
                                        <%= instituto.equals(institutoSeleccionado) ? "selected" : "" %>>
                                        <%= escapar(instituto) %>
                                    </option>

                                <% } %>

                            </select>
                        </div>
                                
                        <% if (institutoSeleccionado != null && !institutoSeleccionado.isBlank()) { %>

                            <div class="campo">
                                <label for="curso">Curso:</label>

                                <select name="curso" id="curso" required>
                                    <option value="">Seleccione un curso</option>

                                    <% for (String curso : cursos) { %>

                                        <option value="<%= escapar(curso) %>"
                                            <%= curso.equals(cursoSeleccionado) ? "selected" : "" %>>
                                            <%= escapar(curso) %>
                                        </option>

                                    <% } %>

                                </select>
                            </div>

                        <% } %>        

                        <button type="submit" class="boton boton-secundario boton-pequeno">
                            Continuar
                        </button>       
                                
                    </form>
                        
                    <% if (cursoSeleccionado != null && !cursoSeleccionado.isBlank()) { %>

                        <h2>Ediciones del curso</h2>

                        <% if (ediciones.isEmpty()) { %>

                            <p>No hay ediciones de este curso en las que participes.</p>
                        
                        <%} else { %>

                            <div class="tabla-contenedor">
                                <table class="tabla">
                                    <thead>
                                        <tr>
                                            <th>Edición</th>
                                            <th>Fecha de inicio</th>
                                            <th>Fecha de finalización</th>
                                            <th>Cupo</th>
                                        </tr>
                                    </thead>

                                    <tbody>

                                        <% for (EdicionCurso edicion : ediciones) { %>

                                            <tr>
                                                <td><%= escapar(edicion.getNombre()) %></td>
                                                <td><%= edicion.getFechaInicio() %></td>
                                                <td><%= edicion.getFechaFin() %></td>
                                                <td><%= edicion.getCupo() == -1 ? "Sin limite" : edicion.getCupo() %></td>
                                            </tr>

                                        <% } %>

                                    </tbody>
                                </table>
                            </div>

                            <h2>Consultar estudiantes aceptados</h2>

                            <form action="${pageContext.request.contextPath}/listar-aceptados"
                                  method="GET">

                                <input type="hidden"
                                       name="instituto"
                                       value="<%= escapar(institutoSeleccionado) %>">

                                <input type="hidden"
                                       name="curso"
                                       value="<%= escapar(cursoSeleccionado) %>">


                                <div class="campo">
                                    <label for="edicion">Edición:</label>

                                    <select name="edicion" id="edicion" required>
                                        <option value="">Seleccione una edición</option>

                                        <% for (EdicionCurso edicion : ediciones) { %>

                                            <option value="<%= escapar(edicion.getNombre()) %>"
                                                <%= edicion.getNombre().equals(edicionSeleccionada) ? "selected" : "" %>>
                                                <%= escapar(edicion.getNombre()) %>
                                            </option>

                                        <% } %>

                                    </select>

                                </div>

                                <button type="submit" class="boton boton-secundario boton-pequeno">
                                    Consultar
                                </button>              

                            </form>

                        <% } %>
                    <% } %>
                    
                    <% if (edicionElegida != null) { %>

                        <h2>Estudiantes aceptados en <%= escapar(edicionElegida.getNombre()) %></h2>
                        
                        <% if (aceptados.isEmpty()) { %>

                            <p>No hay estudiantes aceptados en esta edición.</p>

                        <% } else { %>

                            <p>Se encontraron <%= aceptados.size() %> estudiantes aceptados.</p>

                            <div class="tabla-contenedor">
                                <table class="tabla">
                                    <thead>
                                        <tr>
                                            <th>Nickname</th>
                                            <th>Nombre del estudiante</th>
                                            <th>Fecha de inscripción</th>
                                            <th>Estado</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        
                                        <% for (DTInscripcion inscripcion : aceptados) { %>

                                            <tr>
                                                <td><%= escapar(inscripcion.getNicknameEstudiante()) %></td>
                                                <td><%= escapar(inscripcion.getNombreEstudiante()) %></td>
                                                <td><%= inscripcion.getFechaInscripcion() %></td>
                                                <td><%= inscripcion.getEstado() %></td>
                                            </tr>

                                        <% } %>

                                    </tbody>
                                </table>
                            </div>
                            
                        <% } %>


                    <% } %>
                    

                </section>  
                
            </div>

        </main>
        
        <%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>            
                    
    </body>
</html>