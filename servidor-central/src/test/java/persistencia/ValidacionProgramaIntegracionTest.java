/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package persistencia;

import logica.ServidorCentral;
import logica.ValidadorAprobacionPrograma;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidacionProgramaIntegracionTest {

    @Test
    public void comprobarAprobacionChechiConMySQL() {

        // No ejecutar contra MySQL durante las pruebas normales.
        Assume.assumeTrue(
                Boolean.getBoolean("edext.test.mysql")
        );

        try (ServidorCentral central = new ServidorCentral()) {

            ValidadorAprobacionPrograma.ResultadoPrograma resultado =
                    central.validarAprobacionPrograma(
                            "chechi",
                            "EFI Ingeniería Mecánica",
                            8
                    );

            assertNotNull(resultado);

            assertEquals("chechi", resultado.estudiante());
            assertEquals("EFI Ingeniería Mecánica",
                    resultado.programa());

            assertEquals(3, resultado.totalCursos());
            assertEquals(3, resultado.cursosAprobados());
            assertTrue(resultado.aprobado());

            assertEquals(3, resultado.resultados().size());

            for (var curso : resultado.resultados()) {
                assertTrue(curso.aprobado());
                assertNotNull(curso.nota());
                assertNotNull(curso.fechaResultado());

                System.out.println(
                        curso.curso()
                        + " | Nota: " + curso.nota()
                        + " | Fecha: " + curso.fechaResultado()
                );
            }

            System.out.println("Programa aprobado: "
                    + resultado.aprobado());
        }
    }
}

