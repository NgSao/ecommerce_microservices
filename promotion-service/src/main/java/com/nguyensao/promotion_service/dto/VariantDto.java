package com.nguyensao.promotion_service.dto;

import java.math.BigDecimal;

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
public class VariantDto {
    Long id;
    String color;

    String size;

    String sku;
    String slug;
    Long productId;
    String imageUrl;

    BigDecimal originalPrice;

    BigDecimal salePrice;

    Integer stockQuantity;

    Integer displayOrder;
}