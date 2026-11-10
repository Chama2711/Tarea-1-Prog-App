/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(
    name = "VALORACION_CURSO",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"ESTUDIANTE_NICK", "CURSO_ID"}
    )
)
public class ValoracionCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ESTUDIANTE_NICK", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CURSO_ID", nullable = false)
    private Curso curso;

    @Column(nullable = false)
    private int puntuacion;

    @Column(nullable = false)
    private LocalDate fechaValoracion;

    public ValoracionCurso() {
    }

    public ValoracionCurso(
            Estudiante estudiante,
            Curso curso,
            int puntuacion,
            LocalDate fechaValoracion) {

        if (estudiante == null || curso == null) {
            throw new IllegalArgumentException(
                    "El estudiante y el curso son obligatorios."
            );
        }

        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException(
                    "La puntuación debe estar entre 1 y 5."
            );
        }

        if (fechaValoracion == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria."
            );
        }

        this.estudiante = estudiante;
        this.curso = curso;
        this.puntuacion = puntuacion;
        this.fechaValoracion = fechaValoracion;
    }

    public Long getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public LocalDate getFechaValoracion() {
        return fechaValoracion;
    }
}