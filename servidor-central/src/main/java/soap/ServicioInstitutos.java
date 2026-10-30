/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.util.Objects;
import logica.IServidorCentral;


@WebService(
        serviceName = "InstitutosService",
        portName = "InstitutosPort",
        targetNamespace = "http://soap.edext/"
)


public class ServicioInstitutos {
    private final IServidorCentral central;

    public ServicioInstitutos(IServidorCentral central) {
        this.central = Objects.requireNonNull(central);
    }

    @WebMethod(operationName = "listarInstitutos")
    public String[] listarInstitutos() {
        return central.listarNombresInstitutos()
                .toArray(String[]::new);
    }
}
