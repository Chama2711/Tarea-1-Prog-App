package web;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.ws.Endpoint;
import java.net.InetSocketAddress;
import java.net.URI;
import logica.IServidorCentral;
import soap.ServicioCatalogo;
import web.soap.ClienteCatalogoSoap;

/** Servicio HTTP real para comprobar los servlets sin acceder a MySQL. */
final class CatalogoSoapPrueba implements AutoCloseable {
    private final HttpServer http;
    private final Endpoint endpoint;
    final ClienteCatalogoSoap cliente;

    CatalogoSoapPrueba(IServidorCentral central) throws Exception {
        http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        endpoint = Endpoint.create(new ServicioCatalogo(central));
        endpoint.publish(http.createContext("/institutos"));
        http.start();
        cliente = new ClienteCatalogoSoap(URI.create("http://127.0.0.1:"
                + http.getAddress().getPort() + "/institutos?wsdl").toURL());
    }

    @Override public void close() {
        try { endpoint.stop(); } finally { http.stop(0); }
    }
}
