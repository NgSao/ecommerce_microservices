package com.nguyensao.product_service.service;

import org.springframework.stereotype.Service;

import com.nguyensao.product_service.constant.ExceptionConstant;
import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.dto.request.CategoryRequest;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.mapper.CategoryMapper;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;

    }

    public CategoryDto createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ExceptionConstant.CATEGORY_EXISTS);
        }
        Category category = categoryMapper.categoryToEntity(request);
        categoryRepository.save(category);
        return categoryMapper.categoryToDto(category);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToDto)
                .toList();
    }

    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.CATEGORY_NOT_FOUND));
        return categoryMapper.categoryToDto(category);

    }

    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.CATEGORY_NOT_FOUND));
        if (request.getName() != null && categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException(ExceptionConstant.CATEGORY_EXISTS);
        }
        categoryMapper.categoryUpdatedToEntity(existing, request);
        Category category = categoryRepository.save(existing);
        return categoryMapper.categoryToDto(category);

    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.CATEGORY_NOT_FOUND));
        deleteRecursively(category);
    }

    private void deleteRecursively(Category category) {
        for (Category child : category.getChildren()) {
            deleteRecursively(child);
        }
        categoryRepository.delete(category);
    }

}
