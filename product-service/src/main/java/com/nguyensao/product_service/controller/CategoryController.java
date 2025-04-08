package com.nguyensao.product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.model.ProductCategory;
import com.nguyensao.product_service.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<CategoryDto>> getAllCategoryDtos() {
        return ResponseEntity.ok(categoryService.getAllCategoryDtos());
    }

    @GetMapping("/published")
    public ResponseEntity<List<ProductCategory>> getPublishedCategories() {
        return ResponseEntity.ok(categoryService.getPublishedCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<CategoryDto> getCategoryDtoById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryDtoById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductCategory> getCategoryBySlug(@PathVariable String slug) {
        return categoryService.getCategoryBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<ProductCategory>> getCategoriesByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getCategoriesByParentId(parentId));
    }

    @PostMapping
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> updateCategory(@PathVariable Long id,
            @RequestBody ProductCategory category) {
        return categoryService.getCategoryById(id)
                .map(existingCategory -> {
                    category.setId(id);
                    return ResponseEntity.ok(categoryService.saveCategory(category));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    categoryService.deleteCategory(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
