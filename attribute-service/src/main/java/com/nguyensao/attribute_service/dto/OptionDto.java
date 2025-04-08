package com.nguyensao.attribute_service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OptionDto {
    private Long id;
    private String name;
    private Integer displayOrder;
    private List<OptionValueDto> values = new ArrayList<>();
}