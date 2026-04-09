package com.example.app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer {

    private static final int PORT = 9090;

    // Read connection config from environment variables (injected by Docker Compose)
    private static final String DB_HOST     = System.getenv().getOrDefault("DB_HOST", "not set");
    private static final String DB_PORT     = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME     = System.getenv().getOrDefault("DB_NAME", "not set");
    private static final String REDIS_HOST  = System.getenv().getOrDefault("REDIS_HOST", "not set");
    private static final String REDIS_PORT  = System.getenv().getOrDefault("REDIS_PORT", "6379");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new HomeHandler());
        server.createContext("/health", new HealthHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
        System.out.println("DB   -> " + DB_HOST + ":" + DB_PORT + "/" + DB_NAME);
        System.out.println("Redis-> " + REDIS_HOST + ":" + REDIS_PORT);
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head><title>Java Web App</title></head>"
                + "<body>"
                + "<h1>Java Web App – Full Stack Deployment</h1>"
                + "<p>Java Version: " + System.getProperty("java.version") + "</p>"
                + "<hr>"
                + "<h2>Connected Services</h2>"
                + "<p><b>MySQL:</b> " + DB_HOST + ":" + DB_PORT + " / db: " + DB_NAME + "</p>"
                + "<p><b>Redis:</b> " + REDIS_HOST + ":" + REDIS_PORT + "</p>"
                + "</body>"
                + "</html>";
            sendResponse(exchange, 200, "text/html; charset=UTF-8", html);
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{"
                + "\"status\":\"UP\","
                + "\"port\":9090,"
                + "\"db\":\"" + DB_HOST + ":" + DB_PORT + "\","
                + "\"redis\":\"" + REDIS_HOST + ":" + REDIS_PORT + "\""
                + "}";
            sendResponse(exchange, 200, "application/json", json);
        }
    }

    // -------------------------------------------------------
    // Utility: send HTTP response
    // -------------------------------------------------------
    private static void sendResponse(HttpExchange exchange, int statusCode,
                                     String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
