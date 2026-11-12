package soap.dto;

import jakarta.xml.bind.JAXBContext;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DTCursoConsultaTest {
    @Test public void resumenViajaPorXmlYConservaLosCincoPuntajes() throws Exception {
        var original = new DTCursoConsulta();
        original.asignarValoraciones(List.of(0L, 1L, 0L, 0L, 3L));
        var copia = idaYVuelta(original);
        assertEquals(4, copia.getTotalValoraciones());
        assertEquals(4.25, copia.getPromedioValoraciones(), .001);
        assertEquals(original.getValoracionesPorPuntuacion(), copia.getValoracionesPorPuntuacion());
        original.asignarValoraciones(List.of(0L, 0L, 0L, 0L, 0L));
        assertEquals(0, original.getPromedioValoraciones(), .001);
    }
    private DTCursoConsulta idaYVuelta(DTCursoConsulta curso) throws Exception {
        var contexto = JAXBContext.newInstance(DTCursoConsulta.class);
        var salida = new StringWriter();
        contexto.createMarshaller().marshal(curso, salida);
        return (DTCursoConsulta) contexto.createUnmarshaller().unmarshal(new StringReader(salida.toString()));
    }

    @Test public void xmlConservaDatosRelacionesFechasYTildesSinEntidadesJpa() throws Exception {
        var original = new DTCursoConsulta("Robótica & Educación", "Texto <inicial>", "Un mes",
                20, 3, "https://example.test/curso", LocalDate.of(2026, 11, 4), "Eléctrica", true,
                List.of("Curso previo"), List.of("Tecnología"), List.of("Programa ñ"),
                List.of(new DTEdicionResumen("Edición 2026", LocalDate.of(2026, 11, 5),
                        LocalDate.of(2026, 12, 5), -1, true)));
        var curso = idaYVuelta(original);
        assertEquals(original.getNombre(), curso.getNombre());
        assertEquals(original.getDescripcion(), curso.getDescripcion());
        assertEquals(original.getDuracion(), curso.getDuracion());
        assertEquals(20, curso.getCantidadHoras()); assertEquals(3, curso.getCreditos());
        assertEquals(original.getUrl(), curso.getUrl());
        assertEquals(LocalDate.of(2026, 11, 4), curso.getFechaRegistro());
        assertEquals("Eléctrica", curso.getNombreInstituto()); assertTrue(curso.isTieneImagen());
        assertEquals(List.of("Curso previo"), curso.getPrevias());
        assertEquals(List.of("Tecnología"), curso.getCategorias());
        assertEquals(List.of("Programa ñ"), curso.getProgramas());
        assertEquals(1, curso.getEdiciones().size());
        var edicion = curso.getEdiciones().get(0);
        assertEquals("Edición 2026", edicion.getNombre());
        assertEquals(LocalDate.of(2026, 11, 5), edicion.getFechaInicio());
        assertEquals(LocalDate.of(2026, 12, 5), edicion.getFechaFin());
        assertEquals(-1, edicion.getCupo()); assertTrue(edicion.isTieneImagen());
    }

    @Test public void datosOpcionalesYListasVaciasPuedenViajarPorXml() throws Exception {
        var curso = idaYVuelta(new DTCursoConsulta());
        assertNull(curso.getFechaRegistro()); assertNull(curso.getNombreInstituto());
        assertFalse(curso.isTieneImagen());
        assertTrue(curso.getPrevias().isEmpty()); assertTrue(curso.getCategorias().isEmpty());
        assertTrue(curso.getProgramas().isEmpty()); assertTrue(curso.getEdiciones().isEmpty());
    }

    @Test public void modificarLasListasOriginalesNoAlteraLosDatosDeConsulta() {
        var previas = new ArrayList<>(List.of("Previa"));
        var categorias = new ArrayList<>(List.of("Tecnología"));
        var programas = new ArrayList<>(List.of("Programa"));
        var ediciones = new ArrayList<>(List.of(new DTEdicionResumen()));
        var curso = new DTCursoConsulta("Curso", "", "", 0, 0, null, null, null, false,
                previas, categorias, programas, ediciones);
        previas.clear(); categorias.clear(); programas.clear(); ediciones.clear();
        assertEquals(List.of("Previa"), curso.getPrevias());
        assertEquals(List.of("Tecnología"), curso.getCategorias());
        assertEquals(List.of("Programa"), curso.getProgramas());
        assertEquals(1, curso.getEdiciones().size());
        assertThrows(UnsupportedOperationException.class, () -> curso.getCategorias().clear());
    }

    @Test public void adaptadorRespetaFechasAusentesYRechazaFechasInvalidas() {
        var adaptador = new AdaptadorFecha();
        assertNull(adaptador.marshal(null)); assertNull(adaptador.unmarshal(null));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> adaptador.unmarshal("2026-02-30"));
    }
}
