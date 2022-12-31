package com.pedroalonso.productcrud;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    record Response(@JsonProperty List<Product> list, @JsonProperty int total) {
    }

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping
    public Response findAll() {
        var list = this.productRepository.findAll();

        return new Response(list, list.size());
    }

    @GetMapping("{id}")
    public ProductRecord findById(@PathVariable Integer id) {
        return this.productService.findById(id);
    }

    @PostMapping
    public ProductRecord create(@RequestBody @Valid ProductDTO product) {
        var status = switch (product.status()) {
            case 1 -> ProductStatus.ACTIVE;
            case 0 -> ProductStatus.INACTIVE;
            default -> throw new IllegalArgumentException("Opção inválida");
        };

        return this.productService.create(product.name(), status);
    }
}
