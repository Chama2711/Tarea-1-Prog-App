<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="promedio" required="true" type="java.lang.Double" %>
<%@ attribute name="total" required="true" type="java.lang.Long" %>
<div class="puntaje-curso">
    <span class="puntaje-estrellas" aria-hidden="true"><span>★★★★★</span><span class="puntaje-relleno" style="width:<%= web.Puntajes.porcentaje(promedio) %>%">★★★★★</span></span>
    <span><% if (total == 0) { %>Sin valoraciones<% } else { %><%= web.Puntajes.promedio(promedio) %> de 5 · <%= total %> <%= total == 1 ? "valoración" : "valoraciones" %><% } %></span>
</div>
