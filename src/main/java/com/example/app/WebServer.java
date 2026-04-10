package com.example.app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

public class WebServer {

    private static final int PORT = 9090;

    // Read connection config from environment variables (injected by Docker Compose)
    private static final String DB_HOST    = System.getenv().getOrDefault("DB_HOST", "not set");
    private static final String DB_PORT    = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME    = System.getenv().getOrDefault("DB_NAME", "not set");
    private static final String REDIS_HOST = System.getenv().getOrDefault("REDIS_HOST", "not set");
    private static final String REDIS_PORT = System.getenv().getOrDefault("REDIS_PORT", "6379");

    // Prometheus counters — thread-safe atomic counters per endpoint
    static final AtomicLong reqTotal      = new AtomicLong(0);
    static final AtomicLong reqHome       = new AtomicLong(0);
    static final AtomicLong reqHealth     = new AtomicLong(0);
    static final AtomicLong reqMetrics    = new AtomicLong(0);
    static final AtomicLong req404        = new AtomicLong(0);
    static final long       startTimeMs   = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new HomeHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/metrics", new MetricsHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
        System.out.println("Metrics available at http://localhost:" + PORT + "/metrics");
        System.out.println("DB   -> " + DB_HOST + ":" + DB_PORT + "/" + DB_NAME);
        System.out.println("Redis-> " + REDIS_HOST + ":" + REDIS_PORT);
    }

    // -----------------------------------------------------------
    // Home Page
    // -----------------------------------------------------------
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            reqTotal.incrementAndGet();
            reqHome.incrementAndGet();
            String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head><title>Java Web App</title></head>"
                + "<body>"
                + "<p>Hello, this is Prabhaditya.</p>"
                + "<p>This Java application is running in a docker container</p>"
                + "</body>"
                + "</html>";
            sendResponse(exchange, 200, "text/html; charset=UTF-8", html);
        }
    }

    // -----------------------------------------------------------
    // Health Check
    // -----------------------------------------------------------
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            reqTotal.incrementAndGet();
            reqHealth.incrementAndGet();
            String json = "{"
                + "\"status\":\"UP\","
                + "\"port\":9090,"
                + "\"db\":\"" + DB_HOST + ":" + DB_PORT + "\","
                + "\"redis\":\"" + REDIS_HOST + ":" + REDIS_PORT + "\""
                + "}";
            sendResponse(exchange, 200, "application/json", json);
        }
    }

    // -----------------------------------------------------------
    // Prometheus /metrics endpoint — Prometheus text format
    // -----------------------------------------------------------
    static class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            reqTotal.incrementAndGet();
            reqMetrics.incrementAndGet();

            long uptimeSeconds = (System.currentTimeMillis() - startTimeMs) / 1000;
            Runtime rt = Runtime.getRuntime();
            long usedMemory  = rt.totalMemory() - rt.freeMemory();
            long totalMemory = rt.totalMemory();
            long maxMemory   = rt.maxMemory();

            StringBuilder sb = new StringBuilder();

            // HTTP request counters
            sb.append("# HELP http_requests_total Total number of HTTP requests\n");
            sb.append("# TYPE http_requests_total counter\n");
            sb.append("http_requests_total{endpoint=\"/\"}       ").append(reqHome.get()).append("\n");
            sb.append("http_requests_total{endpoint=\"/health\"}  ").append(reqHealth.get()).append("\n");
            sb.append("http_requests_total{endpoint=\"/metrics\"} ").append(reqMetrics.get()).append("\n");
            sb.append("http_requests_total{endpoint=\"all\"}      ").append(reqTotal.get()).append("\n");

            // Uptime
            sb.append("# HELP app_uptime_seconds Application uptime in seconds\n");
            sb.append("# TYPE app_uptime_seconds gauge\n");
            sb.append("app_uptime_seconds ").append(uptimeSeconds).append("\n");

            // JVM memory
            sb.append("# HELP jvm_memory_used_bytes JVM used heap memory in bytes\n");
            sb.append("# TYPE jvm_memory_used_bytes gauge\n");
            sb.append("jvm_memory_used_bytes ").append(usedMemory).append("\n");

            sb.append("# HELP jvm_memory_total_bytes JVM total heap memory in bytes\n");
            sb.append("# TYPE jvm_memory_total_bytes gauge\n");
            sb.append("jvm_memory_total_bytes ").append(totalMemory).append("\n");

            sb.append("# HELP jvm_memory_max_bytes JVM max heap memory in bytes\n");
            sb.append("# TYPE jvm_memory_max_bytes gauge\n");
            sb.append("jvm_memory_max_bytes ").append(maxMemory).append("\n");

            // App info
            sb.append("# HELP app_info Application information\n");
            sb.append("# TYPE app_info gauge\n");
            sb.append("app_info{version=\"v1\",name=\"java-web-app\",namespace=\"bhavyaprabhaditya\"} 1\n");

            sendResponse(exchange, 200, "text/plain; version=0.0.4; charset=UTF-8", sb.toString());
        }
    }

    // -----------------------------------------------------------
    // Utility
    // -----------------------------------------------------------
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
