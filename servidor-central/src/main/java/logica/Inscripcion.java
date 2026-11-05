/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import java.time.LocalDate;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

/**
 *
 * @author Nicolás
 */
@Entity
public class Inscripcion {
    
    public Inscripcion(){
        
    }
     //Inscripcion a Edicion de Curso
    public Inscripcion(LocalDate fi, EdicionCurso ec){
        this.fechaInscripcion = fi;
        this.edicion = ec;
    }
    
    //Inscripcion a Programa de Formacion
    public Inscripcion(LocalDate fi, ProgramaFormacion pf){
        this.fechaInscripcion = fi;
        this.programa = pf;
    }
    
    
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fechaInscripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoInscripcion estado = EstadoInscripcion.INSCRIPTO;

    @Column(name = "CALIFICACION")
    private Integer calificacion;

    @Column(name = "FECHA_CALIFICACION")
    private LocalDate fechaCalificacion;

    public EstadoInscripcion getEstado() {
        return estado;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public LocalDate getFechaCalificacion() {
        return fechaCalificacion;
    }
    

    public void registrarCalificacion(
            int nota,
            LocalDate fecha) {

        // Solo pueden calificarse inscripciones a ediciones.
        if (edicion == null) {
            throw new IllegalStateException(
                    "La inscripción no pertenece a una edición."
            );
        }

        // El estudiante debe haber sido aceptado.
        if (estado != EstadoInscripcion.ACEPTADA) {
            throw new IllegalStateException(
                    "Solo se pueden calificar estudiantes aceptados."
            );
        }

        // Escala de calificaciones de 1 a 12.
        if (nota < 1 || nota > 12) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 12."
            );
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha de calificación es obligatoria."
            );
        }

        this.calificacion = nota;
        this.fechaCalificacion = fecha;
    }


    public void cambiarEstado(EstadoInscripcion nuevoEstado) {
        if (nuevoEstado != EstadoInscripcion.ACEPTADA && nuevoEstado != EstadoInscripcion.RECHAZADA) {
            throw new IllegalArgumentException("Seleccione Aceptada o Rechazada.");
        }
        if (estado != EstadoInscripcion.INSCRIPTO) {
            throw new IllegalStateException("La inscripción ya fue resuelta.");
        }
        estado = nuevoEstado;
    }
    
    public LocalDate getFechaInscripcion(){
        return fechaInscripcion;
    }
    
    public void setFechaInscripcion(LocalDate fechaInscripcion){
        this.fechaInscripcion = fechaInscripcion;
    }
    
    @ManyToOne
    @JoinColumn(name = "edicion_nombre")
    private EdicionCurso edicion;
    
    @ManyToOne
    private ProgramaFormacion programa;
    
    public EdicionCurso getEdicionCurso(){
        return edicion;
    }
    
    public void setEdicionCurso(EdicionCurso edicion){
        this.edicion = edicion;
    }
    
    public ProgramaFormacion getProgramaFormacion(){
        return programa;
    }
    
    public void setProgramaFormacion(ProgramaFormacion programa){
        this.programa = programa;
    }
    
}
