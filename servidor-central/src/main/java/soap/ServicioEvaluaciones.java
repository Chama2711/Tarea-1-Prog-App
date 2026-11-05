/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.Objects;
import logica.IServidorCentral;
import jakarta.jws.WebParam;
import soap.dto.DTInscripcionSoap;

@WebService(
        serviceName = "EvaluacionesService",
        portName = "EvaluacionesPort",
        targetNamespace = "http://soap.edext/"
)
public class ServicioEvaluaciones {

    private final IServidorCentral central;

    public ServicioEvaluaciones(IServidorCentral central) {
        this.central = Objects.requireNonNull(central);
    }

    @WebMethod(operationName = "listarEdicionesDocente")
    public String[] listarEdicionesDocente(
            @WebParam(name = "nickDocente") String nickDocente) {

        return central.listarEdicionesDocente(nickDocente)
                .toArray(String[]::new);
    }
    
    
    @WebMethod(operationName = "listarAceptadosEdicion")
    public DTInscripcionSoap[] listarAceptadosEdicion(
            @WebParam(name = "nickDocente") String nickDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion) {

        return central.listarAceptadosEdicion(
                nickDocente,
                nombreEdicion
        ).stream()
         .map(DTInscripcionSoap::desde)
         .toArray(DTInscripcionSoap[]::new);
    }
    
    
    @WebMethod(operationName = "registrarCalificacion")
    public void registrarCalificacion(
            @WebParam(name = "nickDocente")
            String nickDocente,

            @WebParam(name = "nombreEdicion")
            String nombreEdicion,

            @WebParam(name = "idInscripcion")
            Long idInscripcion,

            @WebParam(name = "nota")
            int nota) {

        central.registrarCalificacion(
                nickDocente,
                nombreEdicion,
                idInscripcion,
                nota
        );
    }


}
