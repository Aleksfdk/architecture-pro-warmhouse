import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) throws IOException {
        String upstream = System.getenv().getOrDefault(
                "TEMPERATURE_API_URL", "http://temperature-api:8081");
        HttpClient client = HttpClient.newHttpClient();

        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
        server.createContext("/health", exchange -> {
            byte[] body = "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.createContext("/temperature", exchange -> {
            try {
                String path = exchange.getRequestURI().getRawPath();
                String query = exchange.getRequestURI().getRawQuery();
                String url = upstream + path + (query == null ? "" : "?" + query);
                HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
                HttpResponse<String> response = client.send(
                        request, HttpResponse.BodyHandlers.ofString());
                byte[] body = response.body().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(response.statusCode(), body.length);
                exchange.getResponseBody().write(body);
            } catch (Exception error) {
                byte[] body = "{\"error\":\"telemetry upstream failed\"}"
                        .getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(502, body.length);
                exchange.getResponseBody().write(body);
            } finally {
                exchange.close();
            }
        });
        server.start();
        System.out.println("telemetry listening on 8083, upstream " + upstream);
    }
}
