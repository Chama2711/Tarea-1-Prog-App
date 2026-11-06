package soap;

import jakarta.xml.ws.WebFault;
import soap.dto.DetalleRechazo;

/** Fallo de negocio identificado en el contrato, separado de los errores de transporte. */
@WebFault(name = "OperacionRechazada", targetNamespace = "http://soap.edext/", faultBean = "soap.dto.DetalleRechazo")
public class OperacionRechazada extends Exception {
    private final DetalleRechazo faultInfo;
    public OperacionRechazada(String mensaje, DetalleRechazo faultInfo) {
        super(mensaje);
        this.faultInfo = faultInfo;
    }
    public DetalleRechazo getFaultInfo() { return faultInfo; }
}
