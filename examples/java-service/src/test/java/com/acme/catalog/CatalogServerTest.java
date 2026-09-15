package com.acme.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogServerTest {

  private HttpServer server;
  private final HttpClient client = HttpClient.newHttpClient();

  @BeforeEach
  void start() throws Exception {
    server = new CatalogServer(Catalog.sample()).create(0);
    server.start();
  }

  @AfterEach
  void stop() {
    server.stop(0);
  }

  private HttpResponse<String> get(String path) throws Exception {
    URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path);
    return client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void healthz() throws Exception {
    HttpResponse<String> response = get("/healthz");
    assertEquals(200, response.statusCode());
    assertEquals("ok", response.body());
  }

  @Test
  void version() throws Exception {
    HttpResponse<String> response = get("/version");
    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("\"version\""));
  }

  @Test
  void knownProduct() throws Exception {
    HttpResponse<String> response = get("/products/SKU-1");
    assertEquals(200, response.statusCode());
    assertEquals("{\"sku\":\"SKU-1\",\"name\":\"Widget\",\"price\":\"9.99\"}", response.body());
  }

  @Test
  void unknownProduct() throws Exception {
    assertEquals(404, get("/products/SKU-404").statusCode());
  }

  @Test
  void catalogSize() {
    assertEquals(2, Catalog.sample().size());
  }

  @Test
  void negativePriceIsRejected() {
    assertThrows(
        IllegalArgumentException.class, () -> new Product("X", "Bad", new BigDecimal("-1")));
  }
}
