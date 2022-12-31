package com.pedroalonso.productcrud;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ProductDTO(
        @NotNull @NotEmpty String name,
        @Max(1) @Min(0) int status
) {
    public Product toEntity() {
        return new Product(null, name, status);
    }
}
