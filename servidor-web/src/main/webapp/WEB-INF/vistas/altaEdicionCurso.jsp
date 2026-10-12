<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Alta de Edición de Curso | edEXT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>

<body>
    
    <main class="alta-curso-contenedor">
        
        <h1>Alta de Edición de Curso</h1>
        <div class="alta-curso-tarjeta">
        
        <form class="alta-curso-formulario"
            action="${pageContext.request.contextPath}/alta-edicion-curso"
              method="get">
                
                <div class="alta-curso-campo">
                    <label for="instituto">Instituto:</label>

                    <select id="instituto" name="instituto">

                        <option value =""> Seleccione un instituto</option>

                            <%
                                List<String> institutos = (List<String>) request.getAttribute("institutos");

                                if (institutos != null) 
                                {
                                    for (String instituto : institutos) 
                                    {
                            %>
                                        <option value="<%= instituto %>"
                                            <%= instituto.equals(request.getAttribute("institutoSeleccionado") != null ? request.getAttribute("institutoSeleccionado") : request.getParameter("instituto")) ? "selected" : "" %>>
                                            <%= instituto %>
                                        </option>
                            <%
                                    }
                                }
                            %>    

                    </select>
                </div>
                        
                        <button type="submit" class="alta-curso-boton">Seleccionar instituto</button>

        </form>
            <%            
                List<String> cursos = (List<String>) request.getAttribute("cursos");

                List<String> docentes = (List<String>) request.getAttribute("docentes");

                String institutoSeleccionado = (String) request.getAttribute("institutoSeleccionado");
                
                if(institutoSeleccionado != null)
                {
            %>
                
                    <h2>Datos de la edición</h2>
                    
                    <% if (request.getAttribute("error") != null) { %>

                        <p class="alta-curso-error">
                            ${error}
                        </p>

                    <% } %>
                    
                    <form class="alta-curso-formulario"
                        action="${pageContext.request.contextPath}/alta-edicion-curso"
                        method="post">

                        <input type="hidden"
                               name="instituto"
                               value="<%= institutoSeleccionado %>">

                        <div class="alta-curso-campo">
                            <label for="curso">Curso:</label>

                            <select id="curso" name="curso" required>

                                <option value="">Seleccione un curso</option>

                                <% if (cursos != null) 
                                   {
                                        for (String curso : cursos) 
                                        { %>

                                            <option value="<%= curso %>"
                                                <%= curso.equals(request.getAttribute("cursoIngresado")) ? "selected" : "" %>>
                                                <%= curso %>
                                            </option>
                                    <%  }
                                    } %>

                            </select>
                        </div>                
                                    
                            <div class="alta-curso-campo">
                                <label for="nombre">Nombre de la edición:</label>
                                <input 
                                    type="text"
                                    id="nombre"
                                    name="nombre"
                                    value="${nombreIngresado}"
                                    required>
                            </div>
                                
                            <div class="alta-curso-campo">    
                                <label for="fechaInicio">Fecha de inicio:</label>
                                <input
                                    type="date"
                                    id="fechaInicio"
                                    name="fechaInicio"
                                    value="${fechaInicioIngresada}"
                                    required>
                            </div>   
                                
                                
                            <div class="alta-curso-campo">    
                                <label for="fechaFin">Fecha de finalización:</label>
                                <input
                                    type="date"
                                    id="fechaFin"
                                    name="fechaFin"
                                    value="${fechaFinIngresada}"
                                    required>
                            </div> 
                                
                            <div class="alta-curso-campo">    
                                <label for="cupos">Cupos:</label>
                                <input
                                    type="number"
                                    id="cupos"
                                    name="cupos"
                                    value="${cuposIngresados}"
                                    min="1"
                                    placeholder="Sin límite">
                            </div> 
                                
                                
                                
                            <h3>Docentes disponibles</h3>
                            
                            <div class="alta-curso-opciones">

                                <% if (docentes != null) 
                                {
                                    for (String docente : docentes) 
                                    { %>
                                        <div>
                                            <label>
                                                <input 
                                                    type="checkbox"
                                                    name="docentes"
                                                    value="<%= docente %>"
                                                        <%= request.getAttribute("docentesSeleccionados") != null && ((List<String>) request.getAttribute("docentesSeleccionados")).contains(docente) ? "checked" : "" %>>
                                                    <%= docente %>
                                            </label>
                                        </div>
                                <%  }
                                } %>
                            
                            </div>
                            
                            <div class="alta-curso-acciones">
                                <button type="submit" class="alta-curso-boton">Crear edición</button>

                                <a href="${pageContext.request.contextPath}/inicio">
                                    Cancelar
                                </a>
                            </div>  
                            
                    </form>
             <% } %>
        </div>
    </main>

</body>
</html>
                         
        