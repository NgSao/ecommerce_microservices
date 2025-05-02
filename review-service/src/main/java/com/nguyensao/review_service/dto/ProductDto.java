package com.nguyensao.review_service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    Long id;
    String sku;
    String slug;
    Long productId;
    String imageUrl;

    BigDecimal originalPrice;
    BigDecimal salePrice;

    Integer displayOrder;
}
