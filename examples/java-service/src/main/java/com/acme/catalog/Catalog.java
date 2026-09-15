package com.acme.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** In-memory product catalog. */
public final class Catalog {

  private final Map<String, Product> products;

  public Catalog(List<Product> products) {
    this.products =
        products.stream().collect(Collectors.toUnmodifiableMap(Product::sku, Function.identity()));
  }

  public static Catalog sample() {
    return new Catalog(
        List.of(
            new Product("SKU-1", "Widget", new BigDecimal("9.99")),
            new Product("SKU-2", "Gadget", new BigDecimal("24.50"))));
  }

  public Optional<Product> find(String sku) {
    return Optional.ofNullable(products.get(sku));
  }

  public int size() {
    return products.size();
  }
}
