package web.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

/** Contrato del servicio de institutos; no depende de la lógica del central. */
@WebService(name = "ServicioInstitutos", targetNamespace = "http://soap.edext/")
public interface PuertoInstitutos {

    @WebMethod(operationName = "listarInstitutos")
    @WebResult(name = "return", targetNamespace = "")
    String[] listarInstitutos();
}
