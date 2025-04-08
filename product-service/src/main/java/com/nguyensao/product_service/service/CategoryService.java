package com.nguyensao.product_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.model.ProductCategory;
import com.nguyensao.product_service.repository.ProductCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final ProductCategoryRepository categoryRepository;

    public CategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<ProductCategory> getPublishedCategories() {
        return categoryRepository.findByIsPublishedTrue();
    }

    public Optional<ProductCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<ProductCategory> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    public List<ProductCategory> getCategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional
    public ProductCategory saveCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<CategoryDto> getAllCategoryDtos() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryDtoById(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Category not found with id: " + id));
        return convertToDto(category);
    }

    private CategoryDto convertToDto(ProductCategory category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        return dto;
    }
}