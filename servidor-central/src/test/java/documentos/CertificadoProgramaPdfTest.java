package documentos;

import java.time.LocalDate;
import java.util.stream.IntStream;
import logica.DTCertificadoPrograma;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;
import static org.junit.Assert.*;

public class CertificadoProgramaPdfTest {
    @Test public void palabraSinEspaciosMuyLargaNoSeSaleDelMargen() throws Exception {
        var datos = new DTCertificadoPrograma("ana", "Ana Pérez", "Programa", LocalDate.of(2026,11,10),
                java.util.List.of(new DTCertificadoPrograma.Resultado("X".repeat(500), "Edición ñ", 12, LocalDate.of(2026,11,10))));
        try (var documento = Loader.loadPDF(CertificadoProgramaPdf.generar(datos))) {
            assertTrue(new PDFTextStripper().getText(documento).contains("12/12"));
        }
    }
    @Test public void nombresLargosYMultiplesPaginasNoPierdenCursos() throws Exception {
        var resultados = IntStream.range(0,50).mapToObj(i -> new DTCertificadoPrograma.Resultado(
                "Curso " + i + " - " + "Educación ñ ".repeat(15), "Edición 2026", 6, LocalDate.of(2026,11,10))).toList();
        var datos = new DTCertificadoPrograma("ana", "Ana María Pérez", "Programa " + "largo ".repeat(30),
                LocalDate.of(2026,11,10),resultados);
        byte[] pdf = CertificadoProgramaPdf.generar(datos);
        try(var documento=Loader.loadPDF(pdf)) {
            assertTrue(documento.getNumberOfPages()>1);
            var texto=new PDFTextStripper().getText(documento);
            for(int i=0;i<50;i++)assertTrue(texto.contains("Curso " + i + " -"));
            assertTrue(texto.contains("Nota mínima de aprobación: 6"));
        }
    }
}
