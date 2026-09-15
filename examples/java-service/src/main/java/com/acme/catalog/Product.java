package com.acme.catalog;

import java.math.BigDecimal;
import java.util.Objects;

/** A catalog entry. */
public record Product(String sku, String name, BigDecimal price) {

  public Product {
    Objects.requireNonNull(sku, "sku");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(price, "price");
    if (price.signum() < 0) {
      throw new IllegalArgumentException("price must not be negative");
    }
  }

  /** JSON without a library; names are catalog-controlled, so no escaping beyond quotes. */
  public String toJson() {
    return "{\"sku\":\""
        + sku
        + "\",\"name\":\""
        + name.replace("\"", "\\\"")
        + "\",\"price\":\""
        + price.toPlainString()
        + "\"}";
  }
}
