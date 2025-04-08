package com.nguyensao.attribute_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
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
}