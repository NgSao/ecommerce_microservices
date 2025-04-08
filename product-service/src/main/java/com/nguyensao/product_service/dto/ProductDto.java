package com.nguyensao.product_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String shortDescription;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private BigDecimal specialPrice;
    private boolean isPublished;
    private Long brandId;
    private String brandName;
    private Long parentId;
    private Set<Long> categoryIds = new HashSet<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<ProductImageDto> images = new ArrayList<>();
    private List<AttributeValueDto> attributeValues = new ArrayList<>();
    private List<OptionCombinationDto> optionCombinations = new ArrayList<>();
    private List<Long> relatedProductIds = new ArrayList<>();
}