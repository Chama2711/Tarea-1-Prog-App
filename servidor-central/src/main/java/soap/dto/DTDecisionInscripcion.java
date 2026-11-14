package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTDecisionInscripcion {
    public Long id;
    public String estado;
    public DTDecisionInscripcion() { }
    public DTDecisionInscripcion(Long id, String estado) { this.id = id; this.estado = estado; }
}
