package soap.dto;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.*;

/** Datos transportados por SOAP, sin entidades de persistencia. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTPerfilConsulta {
    public DTUsuarioConsulta usuario;
    public boolean propio;
    public List<DTInscripcionConsulta> inscripciones = new ArrayList<>();
    public List<DTEdicionConsulta> ediciones = new ArrayList<>();
    public List<String> programas = new ArrayList<>();
    public List<DTInscripcionConsulta> aceptados = new ArrayList<>();
    public DTPerfilConsulta() { }
    public DTPerfilConsulta(logica.DTPerfilUsuario dato) {
        usuario = new DTUsuarioConsulta(dato.usuario()); propio = dato.propio();
        dato.inscripciones().forEach(i -> inscripciones.add(new DTInscripcionConsulta(i)));
        dato.ediciones().forEach(e -> ediciones.add(new DTEdicionConsulta(e)));
        programas = new ArrayList<>(dato.programas());
        dato.aceptados().forEach(i -> aceptados.add(new DTInscripcionConsulta(i)));
    }
}
