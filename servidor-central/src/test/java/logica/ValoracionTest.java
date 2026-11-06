package logica;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValoracionTest {

    @Test
    public void valorarCursoExitosoYValidacionRango() {
        var p = new PersistenciaLogicaPrueba();
        var central = new ServidorCentral(p);

        // 1. Invocación válida
        central.valorarCurso("ana", "Curso Java", 5);
        assertEquals("ana", p.ultimoEstudianteValoracion);
        assertEquals(5, p.ultimaPuntuacion);

        // 2. Puntajes fuera de rango lanzan IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> 
            central.valorarCurso("ana", "Curso Java", 0)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            central.valorarCurso("ana", "Curso Java", 6)
        );
    }

    @Test
    public void obtenerPromedioCursoValidoEInvalido() {
        var p = new PersistenciaLogicaPrueba();
        var central = new ServidorCentral(p);

        // 1. Cursos nulos o vacíos retornan 0.0f
        assertEquals(0.0f, central.obtenerPromedioCurso(null), 0.01f);
        assertEquals(0.0f, central.obtenerPromedioCurso(""), 0.01f);

        // 2. Promedio tras registrar una valoración
        central.valorarCurso("ana", "Curso Java", 4);
        assertEquals(4.0f, central.obtenerPromedioCurso("Curso Java"), 0.01f);
    }
}