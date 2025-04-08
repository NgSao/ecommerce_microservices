package com.nguyensao.attribute_service.dto;

import lombok.Data;

@Data
public class AttributeValueDto {
    private Long id;
    private Long attributeId;
    private String attributeName;
    private String value;
}