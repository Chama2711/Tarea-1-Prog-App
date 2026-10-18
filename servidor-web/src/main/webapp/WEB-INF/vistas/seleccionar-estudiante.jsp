<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="logica.EdicionCurso"%>
<%@page import="logica.DTInscripcion"%>
<%@page import="logica.EstadoInscripcion"%>

<%@ include file="/WEB-INF/vistas/fragmentos/base.jspf" %>

<%
    String tituloPagina = "Seleccionar estudiantes.";
    String bajadaPagina = "Gestioná las inscripciones de una edición de curso.";
    String paginaActiva = "cursos";
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Seleccionar estudiantes | edEXT</title>

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

            <form action="${pageContext.request.contextPath}/seleccionar-estudiantes"
                  method="GET">
            
                <div class="campo">
                    <label for="instituto">Instituto:</label>

                    <select name="instituto" id="instituto" required>
                        <option value="">Seleccione un instituto</option>
                        <%
                            List<String> institutos = (List<String>) request.getAttribute("institutos");

                            if (institutos != null) 
                            {
                                for (String instituto : institutos)
                                {
                        %>
                                    <option value="<%= instituto %>"
                                        <%= instituto.equals(request.getAttribute("institutoSeleccionado")) ? "selected" : "" %>>
                                        <%= instituto %>
                                    </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                
                <%
                    List<String> cursos = (List<String>) request.getAttribute("cursos");

                    String institutoSeleccionado = (String) request.getAttribute("institutoSeleccionado");
                %>

                <% if (institutoSeleccionado != null && !institutoSeleccionado.isBlank()) { %>
                
                <div class="campo">
                    <label for="curso">Curso:</label>

                    <select name="curso" id="curso" required>

                        <option value="">Seleccione un curso</option>

                        <% for (String curso : cursos) { %>
                        
                            <option value="<%= curso %>"
                                <%= curso.equals(request.getAttribute("cursoSeleccionado")) ? "selected" : "" %>>
                                <%= curso %>
                            </option>

                        <% } %>
                    </select>
                </div>
                <% } %>    



                <button 
                    type="submit" class="boton boton-secundario boton-pequeno">Continuar
                </button>
                
        </form>
                
                <% EdicionCurso edicionVigente = (EdicionCurso) request.getAttribute("edicionVigente");%>
                
                <% if (edicionVigente != null) { %>

                    <h2>Datos de la edición vigente</h2>

                    <p>
                        <strong>Nombre:</strong>
                        <%= edicionVigente.getNombre() %>
                    </p>

                    <p>
                        <strong>Fecha de inicio:</strong>
                        <%= edicionVigente.getFechaInicio() %>
                    </p>

                    <p>
                        <strong>Fecha de finalización:</strong>
                        <%= edicionVigente.getFechaFin() %>
                    </p>

                    <p>
                        <strong>Cupo:</strong>
                        <%= edicionVigente.getCupo() == -1 ? "Sin límite" : edicionVigente.getCupo() %>
                    </p>

                <% } %>
                
                <% List<DTInscripcion> inscripciones = (List<DTInscripcion>) request.getAttribute("inscripciones"); %>
                
                <% if(edicionVigente != null){ %>
                
                    <h2>Inscripciones de estudiantes</h2>
                    
                        <%  String ordenSeleccionado = (String) request.getAttribute("ordenSeleccionado"); %>

                        <form action="${pageContext.request.contextPath}/seleccionar-estudiantes"
                              method="GET">

                          <input type="hidden"
                                 name="instituto"
                                 value="<%= institutoSeleccionado %>">

                          <input type="hidden"
                                 name="curso"
                                 value="<%= request.getAttribute("cursoSeleccionado") %>">

                          <div class="campo">
                              <label for="orden">Ordenar inscripciones por:</label>

                              <select name="orden" id="orden">
                                  <option value="fecha"
                                      <%= "fecha".equals(ordenSeleccionado) ? "selected" : "" %>>
                                      Fecha de inscripción
                                  </option>

                                  <option value="prioridad"
                                      <%= "prioridad".equals(ordenSeleccionado) ? "selected" : "" %>>
                                      Prioridad (mayor a menor)
                                  </option>
                              </select>
                          </div>

                          <button type="submit"
                                  class="boton boton-secundario boton-pequeno">
                              Ordenar
                          </button>

                        </form>
                    
                    <% if (inscripciones != null && !inscripciones.isEmpty()) { %>
                    
                        <form action="${pageContext.request.contextPath}/seleccionar-estudiantes"
                              method="POST">   
                            
                            <input type="hidden" 
                                   name="edicion"
                                   value="<%= edicionVigente.getNombre() %>">
                            
                            <div class="tabla-contenedor">
                                <table class="tabla">
                                <thead>
                                    <tr>
                                        <th>Nickname</th>
                                        <th>Estudiante</th>
                                        <th>Fecha de inscripción</th>
                                        <th>Prioridad</th>
                                        <th>Estado actual</th>
                                        <th>Decisión</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <% for (DTInscripcion inscripcion : inscripciones) { %>

                                        <tr>
                                            <td><%= inscripcion.getNicknameEstudiante() %></td>
                                            <td><%= inscripcion.getNombreEstudiante() %></td>
                                            <td><%= inscripcion.getFechaInscripcion() %></td>
                                            <td><%= inscripcion.getPrioridad() %></td>
                                            <td><%= inscripcion.getEstado() %></td>
                                            <td>
                                                <% if (inscripcion.getEstado() == EstadoInscripcion.INSCRIPTO) { %>

                                                    <select name="decision_<%= inscripcion.getId() %>">
                                                        <option value="">Sin cambios</option>
                                                        <option value="ACEPTADA">Aceptada</option>
                                                        <option value="RECHAZADA">Rechazada</option>
                                                    </select>

                                                <% } else { %>

                                                    <%= inscripcion.getEstado() %>

                                                <% } %>
                                            </td>
                                        </tr>

                                    <% } %>
                                </tbody>
                            </table>
                                
                            </div>

                            <div class="acciones-registro">
                                <button type="submit" class="boton">
                                    Guardar selección
                                </button>
                            </div>
                        
                        </form>     
                            
                        
                            
                    <% } else { %>
                    
                        <p>No hay estudiantes inscriptos en esta edición.</p>
                    <% } %>
                    
                <% } %>
                   
                
                
 </section>

    </div>

</main>

<%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
    </body>
</html>

