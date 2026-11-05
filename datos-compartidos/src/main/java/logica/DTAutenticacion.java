package logica;

import java.io.Serializable;

/** Datos que puede conservar la sesión web; no incluye credenciales. */
public final class DTAutenticacion implements Serializable {
    private static final long serialVersionUID = 1L;

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public void setApellido(String nuevoApellido) {
        this.apellido = nuevoApellido;
    }

    public enum Rol { ESTUDIANTE, DOCENTE }

    private final String nick;
    private String nombre;
    private String apellido;
    private final Rol rol;

    public DTAutenticacion(String nick, String nombre, Rol rol) {
        this.nick = nick;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getNick() { return nick; }
    public String getNombre() { return nombre; }
    public Rol getRol() { return rol; }
}
