package com.nguyensao.product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.product_service.dto.AttributeValueDto;
import com.nguyensao.product_service.dto.OptionCombinationDto;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/published")
    public ResponseEntity<List<Product>> getPublishedProducts() {
        return ResponseEntity.ok(productService.getPublishedProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<ProductDto> getProductDtoById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDtoById(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(productService.getProductsByBrand(brandId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(productService.searchProductsByKeyword(keyword));
        } else if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(productService.searchProductsByPriceRange(minPrice, maxPrice));
        } else {
            return ResponseEntity.ok(productService.getAllProducts());
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        Product createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Product updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/attributes")
    public ResponseEntity<List<AttributeValueDto>> getProductAttributes(@PathVariable Long id) {
        List<AttributeValueDto> attributes = productService.getProductAttributes(id);
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<OptionCombinationDto>> getProductOptionCombinations(@PathVariable Long id) {
        List<OptionCombinationDto> options = productService.getProductOptionCombinations(id);
        return ResponseEntity.ok(options);
    }
}