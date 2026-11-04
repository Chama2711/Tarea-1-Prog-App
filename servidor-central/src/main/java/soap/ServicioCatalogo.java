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


@WebService(
        serviceName = "CatalogoService",
        portName = "CatalogoPort",
        targetNamespace = "http://soap.edext/"
)


public class ServicioCatalogo {
    private final IServidorCentral central;

    public ServicioCatalogo(IServidorCentral central) {
        this.central = Objects.requireNonNull(central);
    }

    @WebMethod(operationName = "listarInstitutos")
    public String[] listarInstitutos() {
        return central.listarNombresInstitutos()
                .toArray(String[]::new);
    }

    @WebMethod(operationName = "listarInstitutosDocente")
    public String[] listarInstitutosDocente(@WebParam(name = "nickDocente") String nickDocente) {
        return central.listarNombresInstitutosDocente(nickDocente)
                .toArray(String[]::new);
    }

    @WebMethod(operationName = "listarCategorias")
    public String[] listarCategorias() {
        return central.listarNombresCategorias().toArray(String[]::new);
    }

    @WebMethod(operationName = "listarCategoriasCurso")
    public String[] listarCategoriasCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
        try {
            return central.listarCategoriasCurso(nombreCurso).stream()
                    .map(logica.Categoria::getNombre).toArray(String[]::new);
        } catch (Exception error) {
            throw new IllegalStateException("No se pudieron consultar las categorías del curso.", error);
        }
    }

    @WebMethod(operationName = "listarCategoriasPrograma")
    public String[] listarCategoriasPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) {
        return central.listarNombresCategoriasPrograma(nombrePrograma).toArray(String[]::new);
    }

    @WebMethod(operationName = "listarCursos")
    public String[] listarCursos() {
        return central.listarNombresCursos().toArray(String[]::new);
    }

    @WebMethod(operationName = "listarCursosPorInstituto")
    public String[] listarCursosPorInstituto(@WebParam(name = "nombreInstituto") String nombreInstituto) {
        return central.listarNombresCursosPorInstituto(nombreInstituto).toArray(String[]::new);
    }

    @WebMethod(operationName = "consultarCurso")
    public soap.dto.DTCursoConsulta consultarCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
        return central.consultarCurso(nombreCurso);
    }

    @WebMethod(operationName = "obtenerImagenCurso")
    public byte[] obtenerImagenCurso(@WebParam(name = "nombreCurso") String nombreCurso) {
        try {
            return central.obtenerImagenCurso(nombreCurso);
        } catch (Exception error) {
            throw new IllegalStateException("No se pudo consultar la imagen del curso.", error);
        }
    }
}
