package logica;

import java.time.LocalDate;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CatalogoServidorCentralTest {
    private PersistenciaLogicaPrueba catalogo() {
        var p = new PersistenciaLogicaPrueba();
        p.institutos.put("INCO", new Instituto("INCO"));
        p.institutos.put("DISI", new Instituto("DISI"));
        p.categorias.put("Tecnología", new Categoria("Tecnología"));
        p.categorias.put("Educativo", new Categoria("Educativo"));
        p.usuarios.put("doc", new Docente("doc", "doc@test", "Ana", "Docente", LocalDate.of(1980, 1, 1)));
        return p;
    }

    @Test public void altaYConsultaDeCursoConservanLosDatosYLasRelaciones() {
        var p = catalogo(); var central = new ServidorCentral(p);
        LocalDate fecha = LocalDate.of(2026, 1, 1);
        central.altaCurso("INCO", " Previa ", " Descripción ", " Un mes ", 20, 3,
                " https://example.test ", fecha, List.of(), List.of("Tecnología"));
        central.altaCurso("INCO", "Curso", "Continuación", "Dos meses", 40, 6,
                "https://example.test/curso", List.of("Previa"), List.of("Educativo"));
        Curso curso = central.buscarCurso("Curso");
        assertSame(curso, central.buscarCursoInstituto("Curso", p.institutos.get("INCO")));
        assertNull(central.buscarCursoInstituto("Curso", p.institutos.get("DISI")));
        assertTrue(central.existeCurso("Curso")); assertFalse(central.existeCurso("Ausente"));
        assertEquals("Continuación", curso.getDescripcion()); assertEquals("Dos meses", curso.getDuracion());
        assertEquals(40, curso.getCantidadHoras()); assertEquals(6, curso.getCreditos());
        assertEquals("https://example.test/curso", curso.getUrl()); assertEquals(LocalDate.now(), curso.getFechaRegistro());
        assertEquals("Curso", curso.toString());
        assertEquals(Set.of(p.cursos.get("Previa")), curso.getPrevias());
        assertEquals(fecha, p.cursos.get("Previa").getFechaRegistro());
        assertEquals(List.of("Previa", "Curso"), central.listarNombresCursosPorInstituto("INCO"));
        assertEquals(2, central.listarCursosPorInstituto("INCO").size());
        assertEquals(2, central.listarCursos().size());
        assertEquals(List.of("Previa", "Curso"), central.listarNombresCursos());
    }

    @Test public void listadosParaCombosDistinguenCatalogosVaciosYSeleccionIncompleta() {
        var p = catalogo(); var central = new ServidorCentral(p);
        assertEquals(List.of("INCO", "DISI"), central.listarNombresInstitutos());
        assertEquals(List.of("Tecnología", "Educativo"), central.listarNombresCategorias());
        assertEquals(List.of("doc"), central.listarNicknamesDocentes());
        for (String instituto : new String[]{null, "", " ", "DISI"})
            assertTrue(central.listarNombresCursosPorInstituto(instituto).isEmpty());
        assertEquals(2, central.obtenerInstitutos().size());
        assertSame(p.institutos.get("INCO"), central.buscarInstituto("INCO"));
        assertEquals(List.of("INCO"), central.listarNombresInstitutosDocente("doc"));
        assertEquals(List.of("doc"), central.listarNicknamesDocentesPorInstituto("INCO"));
        assertSame(p.usuarios.get("doc"), central.buscarDocentePorNick("doc"));
        var vacio = new ServidorCentral(new PersistenciaLogicaPrueba());
        assertTrue(vacio.listarNombresInstitutos().isEmpty()); assertTrue(vacio.listarNombresCategorias().isEmpty());
        assertTrue(vacio.listarNicknamesDocentes().isEmpty());
    }

    @Test public void categoriaAgregadaSeConsultaDesdeElCurso() throws Exception {
        var p = catalogo(); var central = new ServidorCentral(p);
        central.altaInstituto("Física"); assertEquals("Física", central.buscarInstituto("Física").getNombre());
        assertEquals(3, central.listarInstitutos().size());
        central.altaCategoria("Social"); assertEquals("Social", central.buscarCategoria("Social").getNombre());
        central.altaCurso("INCO", "Curso", "Desc", "Mes", 10, 2, "https://example.test", List.of(), List.of("Tecnología"));
        central.agregarCategoriaACurso("Curso", "Social");
        assertEquals(Set.of("Social", "Tecnología"), new HashSet<>(central.listarCategoriasCurso("Curso").stream().map(Categoria::getNombre).toList()));
        assertEquals(3, central.listarCategorias().size());
    }

    @Test public void altaDeEdicionAdministrativaConservaCursoDocentesYCupo() {
        var p = catalogo(); var central = new ServidorCentral(p);
        central.altaCurso("INCO", "Curso", "Desc", "Mes", 10, 2, "https://example.test", List.of(), List.of("Tecnología"));
        LocalDate inicio = LocalDate.now(), fin = inicio.plusDays(30);
        central.altaEdicionCurso("INCO", "Curso", " Edición ", inicio, fin, -1, List.of("doc", "doc"));
        var e = central.buscarEdicion("Edición");
        assertEquals(inicio, e.getFechaInicio()); assertEquals(fin, e.getFechaFin());
        assertEquals(-1, e.getCupo()); assertEquals(inicio, e.getFechaPublicacion());
        assertEquals("Edición", e.toString()); assertSame(p.cursos.get("Curso"), e.getCurso());
        assertEquals(Set.of(p.usuarios.get("doc")), e.getDocentes());
        assertEquals(List.of(e), central.listarEdicionesCurso(p.cursos.get("Curso")));
    }

    @Test public void altasDocentesConImagenLaEntreganConLaEntidadCorrecta() {
        var p = catalogo(); var central = new ServidorCentral(p); byte[] imagen = {4, 5};
        central.altaCursoDocente("doc", "INCO", "Curso", "Desc", "Mes", 10, 2, "https://example.test",
                List.of(), List.of("Tecnología"), imagen);
        assertArrayEquals(imagen, p.ultimaImagen); assertNotNull(central.buscarCurso("Curso"));
        central.altaEdicionCursoDocente("doc", "INCO", "Curso", "Edición", LocalDate.now(), LocalDate.now().plusDays(1),
                2, List.of("doc"), imagen);
        assertArrayEquals(imagen, p.ultimaImagen); assertEquals(2, central.buscarEdicion("Edición").getCupo());
    }

    @Test public void programaCreadoSeConsultaYRecibeCursosPorLaFachada() throws Exception {
        var p = catalogo(); var central = new ServidorCentral(p);
        LocalDate inicio = LocalDate.of(2026, 1, 1), fin = inicio.plusMonths(6);
        central.altaProgramaFormacion(" Programa ", " Formación ", inicio, fin, inicio.minusDays(1));
        var programa = central.obtenerDetallePrograma("Programa");
        assertEquals("Formación", programa.getDescripcion()); assertEquals(inicio, programa.getFechaInicio());
        assertEquals(fin, programa.getFechaFin()); assertEquals(inicio.minusDays(1), programa.getFechaAlta());
        assertEquals(List.of("Programa"), central.listarNombresProgramas());
        assertEquals(List.of(programa), central.obtenerProgramas());
        central.altaCurso("INCO", "Curso", "Desc", "Mes", 10, 2, "https://example.test", List.of(), List.of("Tecnología"));
        central.agregarCursoAPrograma("Programa", "Curso");
        assertSame(p.cursos.get("Curso"), programa.getCursos().get("Curso"));
        assertEquals(List.of("Programa"), central.listarNombresProgramasCurso("Curso"));
        assertEquals(List.of("Tecnología"), central.listarNombresCategoriasPrograma("Programa"));
        assertEquals(1, p.cursos.get("Curso").getCategorias().size());
    }

    @Test public void inscripcionPorFachadaConservaEstudianteDestinoYFecha() {
        var p = catalogo(); var central = new ServidorCentral(p);
        var alumno = new Estudiante("ana", "ana@test", "Ana", "Pérez", LocalDate.of(2000, 1, 1));
        var edicion = new EdicionCurso("Edición", LocalDate.now(), LocalDate.now().plusDays(1), 10, LocalDate.now());
        central.inscriboAEdicionCurso(alumno, edicion, LocalDate.now());
        assertEquals("ana", p.ultimoActor); assertEquals("Edición", p.ultimaEdicion); assertEquals(LocalDate.now(), p.ultimaFecha);
        central.inscriboAProgramaFormacion(alumno, new ProgramaFormacion(), LocalDate.now().minusDays(1));
        assertEquals("ana", p.ultimoActor); assertEquals(LocalDate.now().minusDays(1), p.ultimaFecha);
        assertTrue(central.estaInscriptoAPrograma("ana", 9L)); assertFalse(central.estaInscriptoAPrograma("ana", 8L));
    }

    @Test public void imagenesSePidenConElIdentificadorDeSuEntidad() throws Exception {
        var p = catalogo(); var central = new ServidorCentral(p); p.ultimaImagen = new byte[]{9, 8};
        assertArrayEquals(p.ultimaImagen, central.obtenerImagenUsuario("ana")); assertEquals("ana", p.ultimoActor);
        assertArrayEquals(p.ultimaImagen, central.obtenerImagenCurso("Curso")); assertEquals("Curso", p.ultimaEdicion);
        assertArrayEquals(p.ultimaImagen, central.obtenerImagenEdicion("Edición")); assertEquals("Edición", p.ultimaEdicion);
        assertArrayEquals(p.ultimaImagen, central.obtenerImagenPrograma("Programa")); assertEquals("Programa", p.ultimaEdicion);
        central.close(); assertTrue(p.cerrada);
    }
}
