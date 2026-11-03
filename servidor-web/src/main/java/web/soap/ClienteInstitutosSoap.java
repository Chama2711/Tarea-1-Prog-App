package web.soap;

import jakarta.xml.ws.Service;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.xml.namespace.QName;

/** Consulta institutos mediante el servicio SOAP publicado por el central. */
public final class ClienteInstitutosSoap {
    private static final String NAMESPACE = "http://soap.edext/";
    private final Service servicio;

    public ClienteInstitutosSoap() throws IOException {
        this(Path.of(System.getProperty("user.home"), ".edEXT", "web.properties"));
    }

    ClienteInstitutosSoap(Path archivoConfiguracion) throws IOException {
        this(leerWsdl(archivoConfiguracion));
    }

    public ClienteInstitutosSoap(URL wsdl) {
        servicio = Service.create(Objects.requireNonNull(wsdl),
                new QName(NAMESPACE, "InstitutosService"));
    }

    public List<String> listarInstitutos() {
        PuertoInstitutos puerto = servicio.getPort(
                new QName(NAMESPACE, "InstitutosPort"), PuertoInstitutos.class);
        String[] nombres = puerto.listarInstitutos();
        return nombres == null ? List.of() : List.copyOf(Arrays.asList(nombres));
    }

    private static URL leerWsdl(Path archivo) throws IOException {
        Properties configuracion = new Properties();
        try (var lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            configuracion.load(lector);
        }
        String valor = configuracion.getProperty("soap.institutos.wsdl");
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta configurar soap.institutos.wsdl en " + archivo);
        }
        URI direccion = URI.create(valor.trim());
        if (!("http".equalsIgnoreCase(direccion.getScheme())
                || "https".equalsIgnoreCase(direccion.getScheme())) || direccion.getHost() == null) {
            throw new IllegalArgumentException("soap.institutos.wsdl debe ser una URL HTTP o HTTPS válida.");
        }
        return direccion.toURL();
    }
}
