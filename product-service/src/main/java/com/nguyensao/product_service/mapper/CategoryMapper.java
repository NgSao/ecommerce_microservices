package com.nguyensao.product_service.mapper;

import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        if (category == null)
            return null;

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .build();
    }

    public Category toCategoryEntity(CategoryDto categoryDto) {
        if (categoryDto == null)
            return null;

        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .imageUrl(categoryDto.getImageUrl())
                .build();
    }
}
