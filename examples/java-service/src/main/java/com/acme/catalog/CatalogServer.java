package com.acme.catalog;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Executors;

/** Minimal JDK-only HTTP service for the platform-pipeline Gradle path. */
public final class CatalogServer {

  private final Catalog catalog;

  public CatalogServer(Catalog catalog) {
    this.catalog = catalog;
  }

  /** Creates a server bound to {@code port}; call {@code start()} on the result. */
  public HttpServer create(int port) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/healthz", exchange -> respond(exchange, 200, "ok"));
    server.createContext(
        "/version",
        exchange -> respond(exchange, 200, "{\"version\":\"" + version() + "\"}"));
    server.createContext("/products/", this::product);
    server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    return server;
  }

  private void product(HttpExchange exchange) throws IOException {
    String sku = exchange.getRequestURI().getPath().substring("/products/".length());
    Optional<Product> product = catalog.find(sku);
    if (product.isPresent()) {
      respond(exchange, 200, product.get().toJson());
    } else {
      respond(exchange, 404, "{\"error\":\"not found\"}");
    }
  }

  static String version() {
    String version = System.getenv("APP_VERSION");
    return version == null || version.isBlank() ? "dev" : version;
  }

  private static void respond(HttpExchange exchange, int status, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(bytes);
    }
  }

  public static void main(String[] args) throws IOException {
    String port = System.getenv().getOrDefault("PORT", "8080");
    HttpServer server = new CatalogServer(Catalog.sample()).create(Integer.parseInt(port));
    server.start();
    System.out.println("catalog-service " + version() + " listening on " + port);
  }
}
