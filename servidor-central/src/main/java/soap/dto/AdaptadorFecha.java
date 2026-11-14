package soap.dto;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/** Representa LocalDate en SOAP como fecha ISO: yyyy-MM-dd. */
public final class AdaptadorFecha extends XmlAdapter<String, LocalDate> {
    @Override public LocalDate unmarshal(String valor) {
        return valor == null ? null : LocalDate.parse(valor);
    }

    @Override public String marshal(LocalDate valor) {
        return valor == null ? null : valor.toString();
    }
}
