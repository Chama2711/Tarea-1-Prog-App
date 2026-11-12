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
    private final Endpoint endpointCatalogo;
    private final Endpoint endpointEvaluaciones;

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
        Endpoint catalogo = null;
        Endpoint evaluaciones = null;

        // Ambos servicios compartirán el mismo servidor HTTP.
        String rutaEvaluaciones = ruta.substring(
                0, ruta.lastIndexOf('/') + 1
        ) + "evaluaciones";

        try {
            // Publicar el servicio de catálogo existente.
            catalogo = Endpoint.create(
                    new ServicioCatalogo(central)
            );

            catalogo.publish(http.createContext(ruta));

            // Publicar el nuevo servicio de evaluaciones.
            evaluaciones = Endpoint.create(
                    new ServicioEvaluaciones(central)
            );

            evaluaciones.publish(
                    http.createContext(rutaEvaluaciones)
            );

            // Iniciar el servidor HTTP una sola vez.
            http.start();

        } catch (RuntimeException error) {
            try {
                if (evaluaciones != null && evaluaciones.isPublished()) {
                    evaluaciones.stop();
                }
            } finally {
                try {
                    if (catalogo != null && catalogo.isPublished()) {
                        catalogo.stop();
                    }
                } finally {
                    http.stop(0);
                }
            }

            throw error;
        }

        endpointCatalogo = catalogo;
        endpointEvaluaciones = evaluaciones;


        System.out.println("Servicios SOAP publicados:");

        System.out.println(
                "Catálogo: http://" + host + ":" + puerto
                + ruta + "?wsdl"
        );

        System.out.println(
                "Evaluaciones: http://" + host + ":" + puerto
                + rutaEvaluaciones + "?wsdl"
        );
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
            endpointEvaluaciones.stop();
        } finally {
            try {
                endpointCatalogo.stop();
            } finally {
                http.stop(0);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            // Crea una implementación vacía de IServidorCentral al vuelo sin tocar la BD
            IServidorCentral central = (IServidorCentral) java.lang.reflect.Proxy.newProxyInstance(
                    IServidorCentral.class.getClassLoader(),
                    new Class<?>[]{IServidorCentral.class},
                    (proxy, method, methodArgs) -> null
            );

            PublicadorSoap publicador = new PublicadorSoap(central);
            System.out.println("Servidor SOAP activo. Presiona el cuadrado rojo (Stop) para detenerlo.");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}