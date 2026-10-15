/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.Column;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Usuario implements Serializable {
    @Id
    private String nick;
    @Column(unique = true, nullable = false)
    private String mail;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    @Column(name = "CONTRASENA", length = 255)
    private String contrasena;

    @Column(name = "IMAGEN", length = 255)
    private String imagen;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public Usuario(){
        
    }
    
    public Usuario(String ni, String m, String no, String a, LocalDate fn){
        this.nick = ni;
        this.mail = m;
        this.nombre = no;
        this.apellido = a;
        this.fechaNacimiento = fn;
    }
    
    public String getNick(){
        return nick;
    }
    public void setNick(String ni){
        nick = ni;
    }
    
    public String getMail(){
        return mail;
    }
    public void setMail(String ni){
        mail = ni;
    }
    
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String no){
        nombre = no;
    }
    
    public String getApellido(){
        return apellido;
    }
    public void setApellido(String a){
        apellido = a;
    }
    
    public LocalDate getFechaNacimiento(){
        return fechaNacimiento;
    }
    public void setFechaNacimiento(LocalDate fn){
        fechaNacimiento = fn;
    }
    
    @Override
    public String toString() {
        return nick + " - " + nombre + " " + apellido;
    }
    
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_seguidores",
        joinColumns = @JoinColumn(name = "seguidor_nickname"),
        inverseJoinColumns = @JoinColumn(name = "seguido_nickname")
    )
    private List<Usuario> seguidos = new ArrayList<>();

    public List<Usuario> getSeguidos() {
        return seguidos;
    }

    public void seguir(Usuario u) {
        if (!this.seguidos.contains(u)) {
            this.seguidos.add(u);
        }
    }

    public void dejarDeSeguir(Usuario u) {
        this.seguidos.remove(u);
    }
    
}
