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
import web.soap.generado.CatalogoService;
import web.soap.generado.ServicioCatalogo;
import web.soap.generado.DtCursoConsulta;
import web.soap.generado.DtProgramaConsulta;
import web.soap.generado.DtEdicionConsulta;

/** Consulta el catálogo mediante el servicio SOAP del central. */
public final class ClienteCatalogoSoap {
    private final CatalogoService servicio;

    public ClienteCatalogoSoap() throws IOException {
        this(Path.of(System.getProperty("user.home"), ".edEXT", "web.properties"));
    }

    ClienteCatalogoSoap(Path archivoConfiguracion) throws IOException {
        this(leerWsdl(archivoConfiguracion));
    }

    public ClienteCatalogoSoap(URL wsdl) {
        servicio = new CatalogoService(Objects.requireNonNull(wsdl));
    }

    public List<String> listarInstitutos() {
        ServicioCatalogo puerto = servicio.getCatalogoPort();
        List<String> nombres = puerto.listarInstitutos();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarInstitutosDocente(String nickDocente) {
        ServicioCatalogo puerto = servicio.getCatalogoPort();
        List<String> nombres = puerto.listarInstitutosDocente(nickDocente);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategorias() {
        List<String> nombres = servicio.getCatalogoPort().listarCategorias();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategoriasCurso(String nombreCurso) {
        List<String> nombres = servicio.getCatalogoPort().listarCategoriasCurso(nombreCurso);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCategoriasPrograma(String nombrePrograma) {
        List<String> nombres = servicio.getCatalogoPort().listarCategoriasPrograma(nombrePrograma);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCursos() {
        List<String> nombres = servicio.getCatalogoPort().listarCursos();
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public List<String> listarCursosPorInstituto(String nombreInstituto) {
        List<String> nombres = servicio.getCatalogoPort().listarCursosPorInstituto(nombreInstituto);
        return nombres == null ? List.of() : List.copyOf(nombres);
    }

    public DtCursoConsulta consultarCurso(String nombreCurso) {
        return servicio.getCatalogoPort().consultarCurso(nombreCurso);
    }

    public byte[] obtenerImagenCurso(String nombreCurso) {
        return servicio.getCatalogoPort().obtenerImagenCurso(nombreCurso);
    }

    public List<DtProgramaConsulta> listarProgramas() {
        var programas = servicio.getCatalogoPort().listarProgramas();
        return programas == null ? List.of() : List.copyOf(programas);
    }

    public DtProgramaConsulta consultarPrograma(String nombrePrograma) {
        return servicio.getCatalogoPort().consultarPrograma(nombrePrograma);
    }

    public boolean estaInscriptoAPrograma(String nickEstudiante, Long programaId) {
        return servicio.getCatalogoPort().estaInscriptoAPrograma(nickEstudiante, programaId);
    }

    public DtEdicionConsulta consultarEdicion(String nombreEdicion) {
        return servicio.getCatalogoPort().consultarEdicion(nombreEdicion);
    }

    public byte[] obtenerImagenPrograma(String nombrePrograma) {
        return servicio.getCatalogoPort().obtenerImagenPrograma(nombrePrograma);
    }

    public byte[] obtenerImagenEdicion(String nombreEdicion) {
        return servicio.getCatalogoPort().obtenerImagenEdicion(nombreEdicion);
    }

    private static URL leerWsdl(Path archivo) throws IOException {
        Properties configuracion = new Properties();
        try (var lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            configuracion.load(lector);
        }
        String valor = configuracion.getProperty("soap.catalogo.wsdl");
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta configurar soap.catalogo.wsdl en " + archivo);
        }
        URI direccion = URI.create(valor.trim());
        if (!("http".equalsIgnoreCase(direccion.getScheme())
                || "https".equalsIgnoreCase(direccion.getScheme())) || direccion.getHost() == null) {
            throw new IllegalArgumentException("soap.catalogo.wsdl debe ser una URL HTTP o HTTPS válida.");
        }
        return direccion.toURL();
    }
}
