package logica;

import java.time.LocalDate;
import java.util.List;

/** Datos inmutables de consulta: no contienen credenciales ni entidades JPA. */
public final class DTUsuarioConsulta {
    private final String nick;
    private final String nombre;
    private final String apellido;
    private final String correo;
    private final LocalDate fechaNacimiento;
    private final DTAutenticacion.Rol rol;
    private final boolean tieneImagen;
    private final List<String> institutos;

    public DTUsuarioConsulta(String nick, String nombre, String apellido, String correo, LocalDate fechaNacimiento, DTAutenticacion.Rol rol, boolean tieneImagen, List<String> institutos) {
        this.nick = nick;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.tieneImagen = tieneImagen;
        this.institutos = List.copyOf(institutos);
    }

    public String nick() { return nick; }
    public String nombre() { return nombre; }
    public String apellido() { return apellido; }
    public String correo() { return correo; }
    public LocalDate fechaNacimiento() { return fechaNacimiento; }
    public DTAutenticacion.Rol rol() { return rol; }
    public boolean tieneImagen() { return tieneImagen; }
    public List<String> institutos() { return institutos; }
    public String nombreCompleto() { return nombre + " " + apellido; }
}
