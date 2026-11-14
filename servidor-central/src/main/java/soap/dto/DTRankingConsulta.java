package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTRankingConsulta {
    public DTInscripcionConsulta inscripcion;
    public long rechazosAnteriores;
    public DTRankingConsulta() { }
    public DTRankingConsulta(logica.DTRankingInscripcion dato) {
        inscripcion = new DTInscripcionConsulta(dato.getInscripcion());
        rechazosAnteriores = dato.getRechazosAnteriores();
    }
}
