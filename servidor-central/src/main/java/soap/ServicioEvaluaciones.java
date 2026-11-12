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
import soap.dto.DTInscripcionSoap;
import soap.dto.DTNotaEstudianteSoap;
import logica.DTResultadoEvaluacion;
import soap.dto.DTResultadoEvaluacionSoap;

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

    @WebMethod(operationName = "consultarResultadosEdicion")
    public DTResultadoEvaluacionSoap[] consultarResultadosEdicion(
            @WebParam(name = "nickDocente")
            String nickDocente,

            @WebParam(name = "nombreEdicion")
            String nombreEdicion) {

        return central.consultarResultadosEdicion(
                nickDocente,
                nombreEdicion
        ).stream()
         .map(DTResultadoEvaluacionSoap::desde)
         .toArray(DTResultadoEvaluacionSoap[]::new);
    }

    @WebMethod(operationName = "listarMisNotas")
    public DTNotaEstudianteSoap[] listarMisNotas(@WebParam(name = "tokenNotas") String tokenNotas) {
        return central.listarNotasEstudiante(TokenNotas.estudiante(tokenNotas)).stream()
                .map(DTNotaEstudianteSoap::new)
                .toArray(DTNotaEstudianteSoap[]::new);
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

    
    @WebMethod(operationName = "cerrarEdicion")
    public void cerrarEdicion(
            @WebParam(name = "nickDocente")
            String nickDocente,

            @WebParam(name = "nombreEdicion")
            String nombreEdicion) {

        central.cerrarEdicion(
                nickDocente,
                nombreEdicion
        );
    }

    @WebMethod(operationName = "obtenerActaEstudio")
    public logica.DTActaEstudio obtenerActaEstudio(
            @WebParam(name = "nickEstudiante") String nickEstudiante,
            @WebParam(name = "nombrePrograma") String nombrePrograma) {

        // Llamamos al método correspondiente en tu fachada IServidorCentral
        return central.obtenerActaEstudio(nickEstudiante, nombrePrograma);
    }
    
}
