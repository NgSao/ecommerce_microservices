package com.nguyensao.product_service.controller;

import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.service.CategoryService;
import com.nguyensao.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /**
     * 📌 1. API: Tạo danh mục
     */
    @PostMapping("/admin/categories/add")
    public ResponseEntity<CategoryDto> createCategory(@RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        try {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name(name)
                    .build();
            return ResponseEntity.ok(categoryService.createCategory(file, categoryDto));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 📌 2. API: Cập nhật danh mục
     */
    @PostMapping("/admin/categories/update")
    public ResponseEntity<CategoryDto> updateCategory(@RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, file, name));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 📌 3. API: Lấy ra tất cả danh mục
     */
    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    /**
     * 📌 4. API: Lấy ra 1 danh mục
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * 📌 5. API: Thay đổi trạng thái danh mục
     */
    @GetMapping("/admin/categories/toggle-status/{id}")
    public ResponseEntity<String> toggleCategoryStatus(@PathVariable String id) {
        categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok("Trạng thái danh mục đã được thay đổi!");
    }

    /**
     * 📌 6. API: Xóa danh mục
     */
    @DeleteMapping("/admin/categories/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 📌 7. API: Tạo sản phẩm
     */
    @PostMapping("/admin/add")
    public ResponseEntity<ProductDto> createProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") String categoryId

    ) {
        try {
            ProductDto productDto = ProductDto.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .categoryId(categoryId)
                    .build();
            return ResponseEntity.ok(productService.createProduct(file, productDto));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        } catch (AppException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // /**
    // * 📌 8. API: Cập nhật sản phẩm + kiểm tra còn hàng không
    // */
    // @PutMapping("/admin/update")
    // public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto
    // productDto) {
    // return ResponseEntity.ok(productService.updateProduct(productDto));
    // }

    // /**
    // * 📌 9. API: Lấy sản phẩm theo id
    // */
    // @GetMapping("/admin/{id}")
    // public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
    // return ResponseEntity.ok(productService.getProductById(id));
    // }

    // /**
    // * 📌 10. API: Xóa sản phẩm + kiểm tra có đơn hàng chưa
    // */
    // @DeleteMapping("/admin/{id}")
    // public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    // productService.deleteProduct(id);
    // return ResponseEntity.ok().build();
    // }

    // /**
    // * 📌 11. API: Kiểm tra sản phẩm còn hàng không
    // */
    // @GetMapping("/admin/inventory")
    // public ResponseEntity<Boolean> checkProductStock(@RequestParam String
    // productId) {
    // return ResponseEntity.ok(productService.checkProductStock(productId));
    // }

    // /**
    // * 📌 12. API: Cập nhật trạng thái sản phẩm
    // */
    // @PatchMapping("/admin/active")
    // public ResponseEntity<Void> toggleProductStatus(@RequestParam String
    // productId) {
    // productService.toggleProductStatus(productId);
    // return ResponseEntity.ok().build();
    // }
}
