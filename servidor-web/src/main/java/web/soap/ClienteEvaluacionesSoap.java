/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package web.soap;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import web.soap.evaluaciones.generado.EvaluacionesService;
import web.soap.evaluaciones.generado.ServicioEvaluaciones;
import web.soap.evaluaciones.generado.DtInscripcionSoap;
import web.soap.evaluaciones.generado.DtNotaEstudianteSoap;
import web.soap.evaluaciones.generado.DtResultadoEvaluacionSoap;
import web.soap.evaluaciones.generado.DtActaEstudio;

public final class ClienteEvaluacionesSoap {

    private final EvaluacionesService servicio;

    // Utiliza el archivo de configuración de la Web.
    public ClienteEvaluacionesSoap() throws IOException {
        this(leerWsdl(Path.of(
                System.getProperty("user.home"),
                ".edEXT",
                "web.properties"
        )));
    }

    // Permite utilizar otra URL en las pruebas.
    public ClienteEvaluacionesSoap(URL wsdl) {
        servicio = new EvaluacionesService(
                Objects.requireNonNull(wsdl)
        );
    }

    // Consulta las ediciones del docente mediante SOAP.
    public List<String> listarEdicionesDocente(String nickDocente) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        List<String> resultado =
                puerto.listarEdicionesDocente(nickDocente);

        return resultado == null
                ? List.of()
                : List.copyOf(resultado);
    }

    // Consulta los estudiantes aceptados mediante SOAP.
    public List<DtInscripcionSoap> listarAceptadosEdicion(
            String nickDocente,
            String nombreEdicion) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        List<DtInscripcionSoap> resultado =
                puerto.listarAceptadosEdicion(
                        nickDocente,
                        nombreEdicion
                );

        return resultado == null
                ? List.of()
                : List.copyOf(resultado);
    }

    // Consulta los resultados definitivos de una edición
    // mediante el servicio SOAP del Servidor Central.
    public List<DtResultadoEvaluacionSoap> consultarResultadosEdicion(
            String nickDocente,
            String nombreEdicion) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        List<DtResultadoEvaluacionSoap> resultados =
                puerto.consultarResultadosEdicion(
                        nickDocente,
                        nombreEdicion
                );

        return resultados == null
                ? List.of()
                : List.copyOf(resultados);
    }

    public List<DtNotaEstudianteSoap> listarMisNotas(String tokenNotas) {
        var notas = servicio.getEvaluacionesPort().listarMisNotas(tokenNotas);
        return notas == null ? List.of() : List.copyOf(notas);
    }

    // Lee la dirección del servicio desde web.properties.
    private static URL leerWsdl(Path archivo) throws IOException {

        Properties propiedades = new Properties();

        try (var lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
        }

        String valor = propiedades.getProperty(
                "soap.evaluaciones.wsdl"
        );

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta configurar soap.evaluaciones.wsdl en "
                    + archivo
            );
        }

        URI direccion = URI.create(valor.trim());

        if (!("http".equalsIgnoreCase(direccion.getScheme())
                || "https".equalsIgnoreCase(direccion.getScheme()))
                || direccion.getHost() == null) {

            throw new IllegalArgumentException(
                    "La dirección WSDL de evaluaciones es inválida."
            );
        }

        return direccion.toURL();
    }
    
    public void registrarCalificacion(
        String nickDocente,
        String nombreEdicion,
        Long idInscripcion,
        int nota) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        puerto.registrarCalificacion(
                nickDocente,
                nombreEdicion,
                idInscripcion,
                nota
        );
    }
    
    // Solicita el cierre de una edición al Servidor Central.
    public void cerrarEdicion(
            String nickDocente,
            String nombreEdicion) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        puerto.cerrarEdicion(
                nickDocente,
                nombreEdicion
        );
    }

    
   // Obtiene el acta de estudio de un estudiante en un programa mediante SOAP.
    public DtActaEstudio obtenerActaEstudio(
            String nickEstudiante,
            String nombrePrograma) {

        ServicioEvaluaciones puerto =
                servicio.getEvaluacionesPort();

        return puerto.obtenerActaEstudio(
                nickEstudiante,
                nombrePrograma
        );
    }
    
    
}

