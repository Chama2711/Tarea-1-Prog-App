/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.EstadoInscripcion;
import logica.ValoracionCurso;
import org.junit.Test;
import org.junit.Assume;
import static org.junit.Assert.*;

public class ValoracionCursoIntegracionTest {

    @Test
    public void comprobarConsultasJPAConMySQL() {

        // Solo ejecutar esta prueba cuando lo indiquemos expresamente.
        Assume.assumeTrue(
                Boolean.getBoolean("edext.test.mysql")
        );

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("edEXTPU");

        try {
            EntityManager em = emf.createEntityManager();

            try {
                // Comprobar las relaciones Estudiante -> Inscripcion
                // -> EdicionCurso -> Curso.
                Long aceptados = em.createQuery(
                        "SELECT COUNT(i) "
                        + "FROM Estudiante e JOIN e.inscripciones i "
                        + "WHERE i.edicion.curso IS NOT NULL "
                        + "AND i.estado = :estado",
                        Long.class
                )
                .setParameter(
                        "estado",
                        EstadoInscripcion.ACEPTADA
                )
                .getSingleResult();

                assertNotNull(aceptados);

                // Comprobar el mapeo de la nueva entidad y tabla.
                List<ValoracionCurso> valoraciones = em.createQuery(
                        "SELECT v FROM ValoracionCurso v",
                        ValoracionCurso.class
                )
                .setMaxResults(1)
                .getResultList();

                assertNotNull(valoraciones);

                System.out.println(
                        "Inscripciones aceptadas en ediciones: "
                        + aceptados
                );
                System.out.println(
                        "Mapeo de ValoracionCurso: correcto"
                );

            } finally {
                em.close();
            }

        } finally {
            emf.close();
        }
    }
}