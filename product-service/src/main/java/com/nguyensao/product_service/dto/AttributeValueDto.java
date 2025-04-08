package com.nguyensao.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValueDto {
    private Long id;
    private Long attributeId;
    private String attributeName;
    private String value;
}