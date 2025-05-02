package com.nguyensao.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.product_service.dto.BrandDto;
import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.dto.VariantDto;
import com.nguyensao.product_service.dto.request.BrandRequest;
import com.nguyensao.product_service.dto.request.CategoryRequest;
import com.nguyensao.product_service.dto.request.ProductRequest;
import com.nguyensao.product_service.dto.request.VariantRequest;
import com.nguyensao.product_service.service.BrandService;
import com.nguyensao.product_service.service.CategoryService;
import com.nguyensao.product_service.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductController(ProductService productService, CategoryService categoryService,
            BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;

    }

    @GetMapping("/public")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProduct(id));
    }

    @PostMapping("/admin")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.createProduct(request));
    }

    @PostMapping("/admin/variant")
    public ResponseEntity<VariantDto> addVariant(@RequestBody VariantRequest request) {
        return ResponseEntity.ok().body(productService.addVariant(request));
    }

    @PutMapping("/admin/variant/{id}")
    public ResponseEntity<VariantDto> updateVariant(@PathVariable Long id, @RequestBody VariantRequest request) {
        return ResponseEntity.ok().body(productService.updateVariant(id, request));
    }

    @DeleteMapping("/admin/variant/{id}")
    public ResponseEntity<String> deleteVariant(@PathVariable Long id) {
        productService.deleteVariant(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    // Category
    @GetMapping("/public/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }

    @GetMapping("/public/categories/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.getCategory(id));

    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.createCategory(request));

    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.updateCategory(id, request));

    }

    @DeleteMapping("/admin/categories/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "Category with id " + id + " deleted successfully";
    }

    // Brand
    @GetMapping("/public/brands")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok().body(brandService.getAllBrands());
    }

    @GetMapping("/public/brands/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok().body(brandService.getBrand(id));
    }

    @PostMapping("/admin/brands")
    public ResponseEntity<BrandDto> createBrand(@RequestBody BrandRequest request) {
        return ResponseEntity.ok().body(brandService.createBrand(request));
    }

    @PutMapping("/admin/brands/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id, BrandRequest request) {
        return ResponseEntity.ok().body(brandService.updateBrand(id, request));
    }

    @DeleteMapping("/admin/brands/{id}")
    public String deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return "Xóa thành công";
    }

}