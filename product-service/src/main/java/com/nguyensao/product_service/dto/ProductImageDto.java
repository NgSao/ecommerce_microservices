package com.nguyensao.product_service.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private String caption;
    private Integer displayOrder;
    private boolean isMain;
}