package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/** Identificación pública del docente de una edición. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTDocenteResumen {
    private String nick;
    private String nombre;
    private String apellido;
    private String nombreCompleto;

    public DTDocenteResumen() { }
    public DTDocenteResumen(String nick, String nombreCompleto) {
        this.nick = nick;
        this.nombreCompleto = nombreCompleto;
    }
    public DTDocenteResumen(String nick, String nombre, String apellido) {
        this.nick = nick; this.nombre = nombre; this.apellido = apellido;
        this.nombreCompleto = nombre + " " + apellido;
    }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNick() { return nick; }
    public String getNombreCompleto() { return nombreCompleto; }
}
