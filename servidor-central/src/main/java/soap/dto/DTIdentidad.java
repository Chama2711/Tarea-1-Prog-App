package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTIdentidad {
    public String nick;
    public String nombre;
    public String rol;
    public String tokenNotas;
    public DTIdentidad() { }
    public DTIdentidad(logica.DTAutenticacion dato) {
        nick = dato.getNick(); nombre = dato.getNombre(); rol = dato.getRol().name();
    }
}
