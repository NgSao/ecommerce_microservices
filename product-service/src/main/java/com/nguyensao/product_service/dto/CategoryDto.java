package com.nguyensao.product_service.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CategoryDto {
    Long id;
    String name;
    String slug;
    String imageUrl;
    Long parentId;
    int displayOrder;
    Set<CategoryDto> children = new HashSet<>();

}
