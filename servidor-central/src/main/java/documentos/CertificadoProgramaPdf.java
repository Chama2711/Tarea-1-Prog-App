package documentos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import logica.DTCertificadoPrograma;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/** PDF dinámico con fuente incluida en el JAR; no requiere archivos del equipo. */
public final class CertificadoProgramaPdf {
    private CertificadoProgramaPdf() { }

    public static byte[] generar(DTCertificadoPrograma datos) throws IOException {
        try (var documento = new PDDocument(); var salida = new ByteArrayOutputStream();
                var fuente = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")) {
            var font = PDType0Font.load(documento, fuente);
            documento.getDocumentInformation().setTitle("Certificado de aprobación - " + datos.programa());
            documento.getDocumentInformation().setAuthor("edEXT");
            try (var pagina = new Texto(documento, font)) {
                pagina.parrafo("CERTIFICADO DE APROBACIÓN", 22);
                pagina.parrafo("Programa de formación", 13);
                pagina.parrafo(datos.programa(), 18);
                pagina.espacio();
                pagina.parrafo("Se certifica que " + datos.estudiante() + " (usuario: " + datos.nick()
                        + ") aprobó todos los cursos del programa detallados a continuación.", 12);
                pagina.parrafo("Fecha de emisión: " + fecha(datos.emision()), 11);
                pagina.espacio();
                for (var resultado : datos.resultados()) {
                    pagina.reservar(90);
                    pagina.parrafo(resultado.curso(), 14);
                    pagina.parrafo("Edición: " + resultado.edicion(), 11);
                    pagina.parrafo("Resultado: APROBADO | Nota: " + resultado.nota() + "/12 | Fecha del resultado: "
                            + fecha(resultado.fecha()), 11);
                    pagina.espacio();
                }
                pagina.parrafo("Escala: 1 a 12. Nota mínima de aprobación: 6. Se incluyen únicamente resultados de ediciones cerradas.", 10);
            }
            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private static String fecha(java.time.LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static final class Texto implements AutoCloseable {
        final PDDocument documento;
        final PDType0Font fuente;
        PDPageContentStream contenido;
        float y;
        Texto(PDDocument documento, PDType0Font fuente) throws IOException {
            this.documento = documento; this.fuente = fuente; nuevaPagina();
        }
        void nuevaPagina() throws IOException {
            if (contenido != null) contenido.close();
            var pagina = new PDPage(PDRectangle.A4); documento.addPage(pagina);
            contenido = new PDPageContentStream(documento, pagina);
            escribir("edEXT", 48, 800, 13);
            escribir("Página " + documento.getNumberOfPages(), 48, 32, 9);
            y = 758;
        }
        void reservar(float alto) throws IOException { if (y - alto < 65) nuevaPagina(); }
        void espacio() { y -= 12; }
        void escribir(String texto, float x, float y, float tamano) throws IOException {
            contenido.beginText(); contenido.setFont(fuente, tamano);
            contenido.newLineAtOffset(x, y); contenido.showText(texto); contenido.endText();
        }
        void parrafo(String texto, float tamano) throws IOException {
            var limpio = new StringBuilder();
            for (int cp : texto.replaceAll("\\s+", " ").strip().codePoints().toArray()) {
                limpio.append(fuente.hasGlyph(cp) ? new String(Character.toChars(cp)) : "?");
            }
            var linea = new StringBuilder();
            for (String palabra : limpio.toString().split(" ")) {
                String candidato = linea.isEmpty() ? palabra : linea + " " + palabra;
                if (!linea.isEmpty() && fuente.getStringWidth(candidato) / 1000 * tamano > 498) {
                    renglon(linea.toString(), tamano);
                    linea.setLength(0);
                }
                if (!linea.isEmpty()) linea.append(' ');
                // Solo dividir dentro de una palabra si no cabe en una línea completa.
                for (int cp : palabra.codePoints().toArray()) {
                    String caracter = new String(Character.toChars(cp));
                    if (!linea.isEmpty() && fuente.getStringWidth(linea + caracter) / 1000 * tamano > 498) {
                        renglon(linea.toString(), tamano); linea.setLength(0);
                    }
                    linea.append(caracter);
                }
            }
            if (!linea.isEmpty()) renglon(linea.toString(), tamano);
            y -= 5;
        }
        void renglon(String texto, float tamano) throws IOException {
            reservar(tamano * 1.5f); escribir(texto, 48, y, tamano); y -= tamano * 1.5f;
        }
        public void close() throws IOException { if (contenido != null) contenido.close(); }
    }
}
