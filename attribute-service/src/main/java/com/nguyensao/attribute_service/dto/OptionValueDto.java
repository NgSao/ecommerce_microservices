package com.nguyensao.attribute_service.dto;

import lombok.Data;

@Data
public class OptionValueDto {
    private Long id;
    private String value;
    private String displayType;
    private Integer displayOrder;
}