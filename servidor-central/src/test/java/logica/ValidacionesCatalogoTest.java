package logica;

import java.time.LocalDate;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidacionesCatalogoTest {
    private PersistenciaLogicaPrueba catalogo() {
        var p = new PersistenciaLogicaPrueba();
        var instituto = new Instituto("INCO"); p.institutos.put("INCO", instituto);
        var curso = new Curso("Curso", "Mes", 10, 2, LocalDate.now(), "Descripción", "https://example.test");
        curso.setInstituto(instituto); p.cursos.put("Curso", curso);
        p.categorias.put("Tecnología", new Categoria("Tecnología"));
        p.usuarios.put("doc", new Docente("doc", "doc@test", "Docente", "Uno", LocalDate.of(1980, 1, 1)));
        return p;
    }
    private void altaCurso(ServidorCentral c, String instituto, String[] campos, int horas, int creditos, List<String> categorias) {
        c.altaCurso(instituto, campos[0], campos[1], campos[2], horas, creditos, campos[3],
                LocalDate.now(), null, categorias);
    }

    @Test public void cadaTextoObligatorioDelCursoSeValidaAntesDeGuardar() {
        var p = catalogo(); var c = new ServidorCentral(p);
        for (int campo = 0; campo < 4; campo++) for (String invalido : new String[]{null, "", " "}) {
            String[] datos = {"Nuevo", "Descripción", "Un mes", "https://example.test"}; datos[campo] = invalido;
            assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, 10, 2, List.of("Tecnología")));
        }
        assertEquals(0, p.escrituras); assertEquals(Set.of("Curso"), p.cursos.keySet());
    }
    @Test public void institutoAusenteONoRegistradoNoGeneraCursosNiEdiciones() {
        var p = catalogo(); var c = new ServidorCentral(p);
        for (String instituto : new String[]{null, "", " ", "Inexistente"}) {
            assertThrows(IllegalArgumentException.class, () -> altaCurso(c, instituto,
                    new String[]{"Nuevo", "Desc", "Mes", "https://example.test"}, 10, 2, List.of("Tecnología")));
            assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso(instituto,
                    "Curso", "Edición", LocalDate.now(), LocalDate.now(), 1, List.of("doc")));
        }
        assertEquals(0, p.escrituras); assertTrue(p.ediciones.isEmpty());
    }
    @Test public void cantidadesNegativasYCategoriasAusentesOEliminadasNoGuardan() {
        var p = catalogo(); var c = new ServidorCentral(p);
        String[] datos = {"Nuevo", "Desc", "Mes", "https://example.test"};
        assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, -1, 2, List.of("Tecnología")));
        assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, 10, -1, List.of("Tecnología")));
        assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, 10, 2, null));
        assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, 10, 2, List.of()));
        assertThrows(IllegalArgumentException.class, () -> altaCurso(c, "INCO", datos, 10, 2, List.of("Eliminada")));
        assertEquals(0, p.escrituras);
        altaCurso(c, "INCO", datos, 0, 0, List.of("Tecnología"));
        assertEquals(1, p.escrituras); assertTrue(p.cursos.get("Nuevo").getPrevias().isEmpty());
    }
    @Test public void edicionRechazaNombresFechasYCursosIncompletos() {
        var p = catalogo(); var c = new ServidorCentral(p); LocalDate hoy = LocalDate.now();
        for (String vacio : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", vacio, "Edición", hoy, hoy, 1, List.of()));
            assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", "Curso", vacio, hoy, hoy, 1, List.of()));
        }
        assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", "Curso", "Edición", null, hoy, 1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", "Curso", "Edición", hoy, null, 1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", "Ausente", "Edición", hoy, hoy, 1, List.of()));
        assertEquals(0, p.escrituras);
    }
    @Test public void docenteEliminadoAntesDeGuardarNoDejaUnaEdicionParcial() {
        var p = catalogo(); var c = new ServidorCentral(p);
        assertThrows(IllegalArgumentException.class, () -> c.altaEdicionCurso("INCO", "Curso", "Edición",
                LocalDate.now(), LocalDate.now(), 1, List.of("doc", "eliminado")));
        assertTrue(p.ediciones.isEmpty()); assertTrue(p.cursos.get("Curso").getEdiciones().isEmpty());
        assertEquals(0, p.escrituras);
    }
    @Test public void listasOpcionalesDeDocentesYPreviasPuedenSerNulas() {
        var p = catalogo(); var c = new ServidorCentral(p);
        c.altaEdicionCursoDocente("doc", "INCO", "Curso", "Edición", LocalDate.now(), LocalDate.now(), -1, null);
        assertTrue(p.ediciones.get("Edición").getDocentes().isEmpty()); assertEquals(1, p.escrituras);
        c.altaCursoDocente("doc", "INCO", "Nuevo", "Desc", "Mes", 0, 0, "https://example.test", null, List.of("Tecnología"));
        assertTrue(p.cursos.get("Nuevo").getPrevias().isEmpty()); assertEquals(2, p.escrituras);
    }
    @Test public void altaDocenteSinInstitutoNoGuardaAunqueLosOtrosDatosSeanValidos() {
        var p = catalogo(); var c = new ServidorCentral(p);
        for (String instituto : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> c.altaCursoDocente("doc", instituto,
                    "Nuevo", "Desc", "Mes", 10, 2, "https://example.test", List.of(), List.of("Tecnología")));
        }
        assertEquals(0, p.escrituras);
    }
}
