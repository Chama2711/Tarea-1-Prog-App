/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidadorAprobacionPrograma {

    public static final class ResultadoCurso {

        private final Long cursoId;
        private final String curso;
        private final boolean aprobado;
        private final Integer nota;
        private final LocalDate fechaResultado;

        public ResultadoCurso(
                Long cursoId,
                String curso,
                boolean aprobado,
                Integer nota,
                LocalDate fechaResultado) {

            this.cursoId = cursoId;
            this.curso = curso;
            this.aprobado = aprobado;
            this.nota = nota;
            this.fechaResultado = fechaResultado;
        }

        public Long cursoId() {
            return cursoId;
        }

        public String curso() {
            return curso;
        }

        public boolean aprobado() {
            return aprobado;
        }

        public Integer nota() {
            return nota;
        }

        public LocalDate fechaResultado() {
            return fechaResultado;
        }
    }

    public static final class ResultadoPrograma {

        private final String estudiante;
        private final String programa;
        private final boolean aprobado;
        private final int totalCursos;
        private final int cursosAprobados;
        private final List<ResultadoCurso> resultados;

        public ResultadoPrograma(
                String estudiante,
                String programa,
                boolean aprobado,
                int totalCursos,
                int cursosAprobados,
                List<ResultadoCurso> resultados) {

            this.estudiante = estudiante;
            this.programa = programa;
            this.aprobado = aprobado;
            this.totalCursos = totalCursos;
            this.cursosAprobados = cursosAprobados;
            this.resultados = List.copyOf(resultados);
        }

        public String estudiante() {
            return estudiante;
        }

        public String programa() {
            return programa;
        }

        public boolean aprobado() {
            return aprobado;
        }

        public int totalCursos() {
            return totalCursos;
        }

        public int cursosAprobados() {
            return cursosAprobados;
        }

        public List<ResultadoCurso> resultados() {
            return resultados;
        }
    }

    public ResultadoPrograma validar(
            Estudiante estudiante,
            ProgramaFormacion programa,
            int notaMinima) {

        Objects.requireNonNull(estudiante, "Estudiante obligatorio");
        Objects.requireNonNull(programa, "Programa obligatorio");

        if (notaMinima < 1 || notaMinima > 12) {
            throw new IllegalArgumentException(
                    "La nota mínima debe estar entre 1 y 12");
        }

        List<ResultadoCurso> resultados = new ArrayList<>();

        for (Curso curso : programa.getCursos().values()) {

            Inscripcion mejorInscripcion = null;

            for (Inscripcion inscripcion : estudiante.getInscripciones()) {

                EdicionCurso edicion = inscripcion.getEdicionCurso();

                if (edicion == null || edicion.getCurso() == null) {
                    continue;
                }

                Curso cursoInscripcion = edicion.getCurso();

                boolean mismoCurso = cursoInscripcion == curso
                        || (curso.getId() != null
                            && curso.getId().equals(cursoInscripcion.getId()));

                if (!mismoCurso
                        || inscripcion.getEstado() != EstadoInscripcion.ACEPTADA
                        || !edicion.isCerrada()
                        || inscripcion.getCalificacion() == null
                        || inscripcion.getFechaCalificacion() == null) {
                    continue;
                }

                if (mejorInscripcion == null
                        || inscripcion.getCalificacion()
                        > mejorInscripcion.getCalificacion()) {
                    mejorInscripcion = inscripcion;
                }
            }

            boolean aprobado = mejorInscripcion != null
                    && mejorInscripcion.getCalificacion() >= notaMinima;

            resultados.add(new ResultadoCurso(
                    curso.getId(),
                    curso.getNombre(),
                    aprobado,
                    mejorInscripcion == null
                            ? null : mejorInscripcion.getCalificacion(),
                    mejorInscripcion == null
                            ? null : mejorInscripcion.getFechaCalificacion()
            ));
        }

        int aprobados = (int) resultados.stream()
                .filter(ResultadoCurso::aprobado)
                .count();

        int total = resultados.size();

        return new ResultadoPrograma(
                estudiante.getNick(),
                programa.getNombre(),
                total > 0 && aprobados == total,
                total,
                aprobados,
                List.copyOf(resultados)
        );
    }
}
