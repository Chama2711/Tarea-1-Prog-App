<%-- 
    Document   : altaCurso
    Created on : 5 oct 2026, 13:55:29
    Author     : Chama
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>

<% 
    List<String> institutos = (List<String>) request.getAttribute("institutos");
    List<String> categorias = (List<String>) request.getAttribute("categorias");
    List<String> previas = (List<String>) request.getAttribute("previas");

    String institutoSeleccionado = request.getParameter("instituto");
    String error = (String) request.getAttribute("error");
    
    List<String> categoriasSeleccionadas = (List<String>) request.getAttribute("categoriasSeleccionadas");
    List<String> previasSeleccionadas = (List<String>) request.getAttribute("previasSeleccionadas");


    //para nombre vacio
    String nombreIngresado = (String) request.getAttribute("nombreIngresado");

    if (nombreIngresado == null) 
    {
         nombreIngresado = "";
    }
   
    //para descri vacio
    String descripcionIngresada = (String) request.getAttribute("descripcionIngresada");
   
    if(descripcionIngresada == null)
    {
        descripcionIngresada = "";
    }
    
    //duracion vacio
    String duracionIngresada = (String) request.getAttribute("duracionIngresada");
   
    if(duracionIngresada == null)
    {
        duracionIngresada = "";
    }
    
    //url vacio
    String urlIngresada = (String) request.getAttribute("urlIngresada");

    if(urlIngresada == null)
    {
        urlIngresada = "";
    }
    
    //horas vacias
    Integer horasIngresadas = (Integer) request.getAttribute("horasIngresadas");
    
    //creditos vacio
    Integer creditosIngresados = (Integer) request.getAttribute("creditosIngresados");
    
    
    

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Alta de Curso</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
    </head>
    <body>
        <main class="alta-curso-contenedor">
        <h1>Alta de Curso</h1>
        
        <% if (error != null) { %>

            <div class="alta-curso-error">
                <%= error %>
            </div>

        <% } %>
        
        
        <form method="get" action="${pageContext.request.contextPath}/alta-curso">
            
            <div class="alta-curso-campo">
                <label>Instituto:</label>

                    <select name="instituto" onchange="this.form.submit()">

                        <option value="">Seleccione un instituto</option>

                        <% for (String instituto : institutos) { %>

                            <option value="<%= instituto %>" 
                                <%= instituto.equals(institutoSeleccionado) ? "selected" : "" %>>
                                <%= instituto %>
                            </option>

                        <%  } %>

                    </select>
            </div>
        </form>
        
        
    <div class="alta-curso-tarjeta">
        <form class="alta-curso-formulario" method="post" action="${pageContext.request.contextPath}/alta-curso">
           
            <div class="alta-curso-campo">    
                <label>Nombre:</label>
                <input 
                    type="text" 
                    name="nombre"
                    value="<%= nombreIngresado %>"
                >
            </div>
                    
            <div class="alta-curso-campo">           
                <label>Descripción:</label>
                <textarea name="descripcion"><%= descripcionIngresada %></textarea>
            </div>
            
            <div class="alta-curso-campo">
                <label>Duración:</label>
                <input 
                    type="text" 
                    name="duracion"
                    value="<%= duracionIngresada %>"
                >
            </div>
            
            <div class="alta-curso-campo">
                <label>Horas:</label>
                <input 
                    type="number" 
                    name="horas"
                    value="<%= horasIngresadas == null ? "" : horasIngresadas %>"
                >
            </div>
            
            <div class="alta-curso-campo">
                <label>Créditos:</label>
                <input 
                    type="number" 
                    name="creditos"
                    value="<%= creditosIngresados == null ? "" : creditosIngresados %>"
                >
            </div>
                
            <div class="alta-curso-campo">
                <label>URL:</label>
                <input 
                    type="text" 
                    name="url"
                    value="<%= urlIngresada %>"
                >
            </div>
            
            <div class="alta-curso-campo">
                <label>Categorías:</label>

                <div class="alta-curso-opciones">
                    <% for (String categoria : categorias){ %>

                        <label>
                            <input 
                                type="checkbox"
                                name="categorias"
                                value="<%= categoria %>"

                                <%= categoriasSeleccionadas != null && categoriasSeleccionadas.contains(categoria) ? "checked" : "" %>
                            >
                            <%= categoria %>

                        </label>

                    <% } %>
                </div>    
            </div>
            
            <% if (previas != null) { %>
                
                <div class="alta-curso-campo">
                    <label>Previas:</label>

                    <div class="alta-curso-opciones">
                        <% for(String previa : previas) { %>

                            <label>
                                <input 
                                    type="checkbox"
                                    name="previas"
                                    value="<%= previa %>"
                                    <%= previasSeleccionadas != null && previasSeleccionadas.contains(previa) ? "checked" : "" %>
                                >
                                <%= previa %>

                            </label>

                        <% } %>
                    </div>        
                </div>            
            <% } %>    
                
            
            <input type="hidden"
                   name="instituto"
                   value="<%= institutoSeleccionado == null ? "" : institutoSeleccionado %>">
            
            
            
            <div class="alta-curso-acciones">
                
                <a href="${pageContext.request.contextPath}/inicio">Cancelar</a>

                <button type="submit" class="alta-curso-boton">
                    Crear Curso
                </button>
                
            </div>
                
       </form>
    </div>
    </main>       
    </body>
</html>
