/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package logica;

import java.time.LocalDate;

public final class DTResultadoEvaluacion {

    private final Long idInscripcion;
    private final String nicknameEstudiante;
    private final String nombreEstudiante;
    private final Integer calificacion;
    private final LocalDate fechaCalificacion;

    public DTResultadoEvaluacion(
            Long idInscripcion,
            String nicknameEstudiante,
            String nombreEstudiante,
            Integer calificacion,
            LocalDate fechaCalificacion) {

        this.idInscripcion = idInscripcion;
        this.nicknameEstudiante = nicknameEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.calificacion = calificacion;
        this.fechaCalificacion = fechaCalificacion;
    }

    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public String getNicknameEstudiante() {
        return nicknameEstudiante;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public LocalDate getFechaCalificacion() {
        return fechaCalificacion;
    }
}
