/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author elizeth
 */
@Entity
@Table(name = "institutos")
public class Instituto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Único en el sistema

    // Relación con Cursos (Un instituto brinda varios cursos)
    @OneToMany(mappedBy = "instituto", cascade = CascadeType.ALL)
    private Set<Curso> cursos = new HashSet<>() ;

    public Instituto() {}

    public Instituto(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
        return nombre;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Set<Curso> getCursos() { return cursos; }
    public void setCursos(Set<Curso> cursos) { this.cursos = cursos; }
    public void agregoCurso(Curso c) { cursos.add(c); }
}
