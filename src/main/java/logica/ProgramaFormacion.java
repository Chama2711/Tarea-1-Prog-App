/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.Table;

/**
 *
 * @author elizeth
 */
@Entity
@Table(name = "programas_formacion")
public class ProgramaFormacion implements Serializable {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaAlta;

    @ManyToMany
    @JoinTable(
        name = "programa_curso",
        joinColumns = @JoinColumn(name = "programa_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    @MapKey(name = "nombre")
    private Map<String, Curso> cursos = new HashMap<>();

    public ProgramaFormacion() {}
    
    public ProgramaFormacion(String n, String d, Date fi, Date ff, Date fa){
        this.nombre = n;
        this.descripcion = d;
        this.fechaInicio = fi;
        this.fechaFin = ff;
        this.fechaAlta = fa;
               
    }

    // Getters y Setters públicos
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }
    public Map<String, Curso> getCursos() { return cursos; }
    
    public void agregarCurso(Curso curso) {
        this.cursos.put(curso.getNombre(), curso);
    }
    
    
}
