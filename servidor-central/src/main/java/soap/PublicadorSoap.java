/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package soap;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import logica.IServidorCentral;

public class PublicadorSoap implements AutoCloseable {

    private final HttpServer http;
    private final Endpoint endpoint;

    public PublicadorSoap(IServidorCentral central) throws IOException {

        // Leer la configuración externa.
        Path archivo = Path.of(
                System.getProperty("user.home"),
                ".edEXT",
                "central.properties"
        );

        Properties config = new Properties();

        try (var lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {
            config.load(lector);
        }

        String host = requerida(config, "soap.host");
        int puerto = Integer.parseInt(requerida(config, "soap.port"));
        String ruta = requerida(config, "soap.path");

        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("Puerto inválido.");
        }

        if (!ruta.startsWith("/")) {
            throw new IllegalArgumentException(
                    "soap.path debe comenzar con /");
        }

        // Usar el servidor HTTP incluido en el JDK.
        http = HttpServer.create(
                new InetSocketAddress(host, puerto), 0);

        Endpoint servicio = null;

        try {
            servicio = Endpoint.create(
                    new ServicioInstitutos(central));

            servicio.publish(http.createContext(ruta));
            http.start();
        } catch (RuntimeException error) {
            try {
                if (servicio != null && servicio.isPublished()) {
                    servicio.stop();
                }
            } finally {
                http.stop(0);
            }
            throw error;
        }

        endpoint = servicio;

        System.out.println("Servicio SOAP publicado:");
        System.out.println(
                "http://" + host + ":" + puerto + ruta + "?wsdl");
    }

    private static String requerida(Properties config, String nombre) {
        String valor = config.getProperty(nombre);

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta configurar " + nombre);
        }

        return valor.trim();
    }

    @Override
    public void close() {
        try {
            endpoint.stop();
        } finally {
            http.stop(0);
        }
    }
}