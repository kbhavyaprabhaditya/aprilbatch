package com.example.app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new HomeHandler());
        server.createContext("/health", new HealthHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head><title>Java Web App</title></head>"
                + "<body>"
                + "<h1>Java Web App</h1>"
                + "<p>Docker image is working. Running on port 9090.</p>"
                + "<p>Java Version: " + System.getProperty("java.version") + "</p>"
                + "</body>"
                + "</html>";
            sendResponse(exchange, 200, "text/html; charset=UTF-8", html);
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\"status\":\"UP\",\"port\":9090}";
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
