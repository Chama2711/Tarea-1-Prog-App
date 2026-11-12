package web;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;

import java.io.IOException;
import java.io.OutputStream;

import web.soap.evaluaciones.generado.DtActaEstudio;
import web.soap.evaluaciones.generado.DtCursoAprobado;
        
import logica.DTAutenticacion;

@WebServlet("/GenerarActaPDF")
public class GenerarActaPDFServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("usuario") instanceof DTAutenticacion usuario)) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }
        if (usuario.getRol() != DTAutenticacion.Rol.ESTUDIANTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String nombrePrograma = request.getParameter("programa");
        if (nombrePrograma == null || nombrePrograma.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de programa requerido.");
            return;
        }

        DtActaEstudio acta;
        try {
            acta = InicializadorAplicacion.getClienteEvaluaciones(getServletContext())
                    .obtenerActaEstudio(usuario.getNick(), nombrePrograma.trim());
        } catch (WebServiceException | IllegalStateException error) {
            getServletContext().log("No se pudo solicitar el certificado por SOAP.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "No se pudo generar el certificado. Intentá nuevamente más tarde.");
            return;
        }
        
        if (acta == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "El certificado está disponible cuando aprobás todos los cursos del programa.");
            return;
        }

        response.setHeader("Cache-Control", "no-store");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"certificado-edEXT.pdf\"");

        // 4. Generar documento PDF con iText
        try (OutputStream out = response.getOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Encabezado
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("edEXT - Constancia de Acta de Estudio", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            // Datos del estudiante
            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            document.add(new Paragraph("Estudiante: " + acta.getNombreEstudiante(), fontSub));
            document.add(new Paragraph("Programa de Formación: " + acta.getNombrePrograma(), fontSub));
            document.add(new Paragraph("Fecha de Emisión: " + acta.getFechaEmision().toString(), fontSub));
            document.add(Chunk.NEWLINE);

            // Tabla de cursos
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{4, 2, 2});

            addTableHeader(tabla, "Curso");
            addTableHeader(tabla, "Resultado");
            addTableHeader(tabla, "Fecha Aprobación");

            for (DtCursoAprobado curso : acta.getCursosAprobados()) {
                tabla.addCell(curso.getNombreCurso());
                tabla.addCell("Aprobado (" + curso.getNota() + ")");
                tabla.addCell(curso.getFechaAprobacion().toString());
            }

            document.add(tabla);
            document.close();
        } catch (DocumentException e) {
            throw new ServletException("Error al generar el documento PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        table.addCell(header);
    }
}
