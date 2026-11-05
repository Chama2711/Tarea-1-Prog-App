package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTUsuarioConsulta {
    public String nick;
    public String nombre;
    public String apellido;
    public String correo;
    @XmlJavaTypeAdapter(AdaptadorFecha.class)
    public LocalDate fechaNacimiento;
    public String rol;
    public boolean tieneImagen;
    public List<String> institutos = new ArrayList<>();
    public DTUsuarioConsulta() { }
    public DTUsuarioConsulta(logica.DTUsuarioConsulta dato) {
        nick = dato.nick(); nombre = dato.nombre(); apellido = dato.apellido(); correo = dato.correo();
        fechaNacimiento = dato.fechaNacimiento(); rol = dato.rol().name(); tieneImagen = dato.tieneImagen();
        institutos = new ArrayList<>(dato.institutos());
    }
}
