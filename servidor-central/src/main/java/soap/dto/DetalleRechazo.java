package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DetalleRechazo {
    public String codigo;
    public String mensaje;
    public DetalleRechazo() { }
    public DetalleRechazo(String codigo, String mensaje) { this.codigo = codigo; this.mensaje = mensaje; }
}
