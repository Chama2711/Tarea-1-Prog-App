package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/** Identificación pública del docente de una edición. */
@XmlAccessorType(XmlAccessType.FIELD)
public final class DTDocenteResumen {
    private String nick;
    private String nombreCompleto;

    public DTDocenteResumen() { }
    public DTDocenteResumen(String nick, String nombreCompleto) {
        this.nick = nick;
        this.nombreCompleto = nombreCompleto;
    }
    public String getNick() { return nick; }
    public String getNombreCompleto() { return nombreCompleto; }
}
