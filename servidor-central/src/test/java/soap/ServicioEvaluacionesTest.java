/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;

import logica.DTInscripcion;
import logica.EstadoInscripcion;
import logica.IServidorCentral;

import org.junit.Test;
import static org.junit.Assert.*;

import soap.dto.DTInscripcionSoap;

public class ServicioEvaluacionesTest {

    private interface Consulta {
        Object ejecutar(String metodo, Object[] argumentos);
    }

    private static IServidorCentral simularCentral(Consulta consulta) {
        return (IServidorCentral) Proxy.newProxyInstance(
                IServidorCentral.class.getClassLoader(),
                new Class<?>[]{IServidorCentral.class},
                (proxy, metodo, args) ->
                        consulta.ejecutar(metodo.getName(), args)
        );
    }

    @Test
    public void consultaLasEdicionesDelDocente() {
        IServidorCentral central = simularCentral((metodo, args) -> {
            assertEquals("listarEdicionesDocente", metodo);
            assertEquals("docente1", args[0]);

            return List.of("Edicion 2026", "Edicion 2027");
        });

        ServicioEvaluaciones servicio =
                new ServicioEvaluaciones(central);

        assertArrayEquals(
                new String[]{"Edicion 2026", "Edicion 2027"},
                servicio.listarEdicionesDocente("docente1")
        );
    }

    @Test
    public void convierteAceptadosADatosSoap() {
        DTInscripcion inscripcion = new DTInscripcion(
                15L,
                "estudiante1",
                "Ana Pérez",
                "Edicion 2026",
                "Programacion",
                LocalDate.of(2026, 11, 5),
                EstadoInscripcion.ACEPTADA
        );

        IServidorCentral central = simularCentral((metodo, args) -> {
            assertEquals("listarAceptadosEdicion", metodo);
            assertArrayEquals(
                    new Object[]{"docente1", "Edicion 2026"},
                    args
            );
            return List.of(inscripcion);
        });

        DTInscripcionSoap[] resultado =
                new ServicioEvaluaciones(central)
                        .listarAceptadosEdicion(
                                "docente1", "Edicion 2026");

        assertEquals(1, resultado.length);

        DTInscripcionSoap dto = resultado[0];

        assertEquals(Long.valueOf(15), dto.getId());
        assertEquals("estudiante1", dto.getNicknameEstudiante());
        assertEquals("Ana Pérez", dto.getNombreEstudiante());
        assertEquals("Edicion 2026", dto.getNombreEdicion());
        assertEquals("Programacion", dto.getNombreCurso());
        assertEquals("2026-11-05", dto.getFechaInscripcion());
        assertEquals("ACEPTADA", dto.getEstado());
    }

    @Test
    public void devuelveArregloVacioSinAceptados() {
        IServidorCentral central =
                simularCentral((metodo, args) -> List.of());

        DTInscripcionSoap[] resultado =
                new ServicioEvaluaciones(central)
                        .listarAceptadosEdicion("docente1", "Edicion");

        assertEquals(0, resultado.length);
    }

    @Test
    public void permiteCamposOpcionalesNulos() {
        DTInscripcion inscripcion = new DTInscripcion(
                null, "estudiante1", "Ana",
                "Edicion", "Curso", null, null
        );

        DTInscripcionSoap dto = DTInscripcionSoap.desde(inscripcion);

        assertNull(dto.getId());
        assertNull(dto.getFechaInscripcion());
        assertNull(dto.getEstado());
    }

    @Test
    public void conservaLosErroresDelCentral() {
        IServidorCentral central = simularCentral((metodo, args) -> {
            throw new SecurityException("Docente no autorizado");
        });

        ServicioEvaluaciones servicio =
                new ServicioEvaluaciones(central);

        assertThrows(SecurityException.class,
                () -> servicio.listarEdicionesDocente("otro"));

        assertThrows(SecurityException.class,
                () -> servicio.listarAceptadosEdicion("otro", "Edicion"));
    }

    @Test
    public void validaConstructorYDtoVacio() {
        assertThrows(NullPointerException.class,
                () -> new ServicioEvaluaciones(null));

        DTInscripcionSoap dto = new DTInscripcionSoap();

        assertNull(dto.getId());
        assertNull(dto.getNicknameEstudiante());
        assertNull(dto.getNombreEstudiante());
        assertNull(dto.getNombreEdicion());
        assertNull(dto.getNombreCurso());
        assertNull(dto.getFechaInscripcion());
        assertNull(dto.getEstado());
    }
}
    