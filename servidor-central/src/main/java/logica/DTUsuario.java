/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import java.time.LocalDate;
/**
 *
 * @author Nicolás
 */
public class DTUsuario{
    private String nick;
    private String mail;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    
    public DTUsuario(){
        
    }
    
    public DTUsuario(String ni, String m, String no, String a, LocalDate fn){
        this.setNick(ni);
        this.setMail(m);
        this.setNombre(no);
        this.setApellido(a);
        this.setFechaNacimiento(fn);
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
    
}
