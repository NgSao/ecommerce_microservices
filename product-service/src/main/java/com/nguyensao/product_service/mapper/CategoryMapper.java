package com.nguyensao.product_service.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.product_service.constant.ExceptionConstant;
import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.dto.request.CategoryRequest;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.repository.CategoryRepository;
import com.nguyensao.product_service.utils.SlugUtil;

@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category categoryToEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(SlugUtil.toSlug(request.getName()));
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ExceptionConstant.PARENT_CATEGORY_NOT_FOUND)));
        }
        return category;
    }

    public Category categoryUpdatedToEntity(Category category, CategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
            category.setSlug(SlugUtil.toSlug(request.getName()));

        }

        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ExceptionConstant.PARENT_CATEGORY_NOT_FOUND)));
        }
        return category;
    }

    public CategoryDto categoryToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setChildren(category.getChildren().stream()
                .map(this::categoryToDto)
                .collect(Collectors.toSet()));
        return dto;
    }

}
