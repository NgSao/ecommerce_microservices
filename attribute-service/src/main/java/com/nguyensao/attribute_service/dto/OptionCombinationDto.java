package com.nguyensao.attribute_service.dto;

import lombok.Data;

@Data
public class OptionCombinationDto {
    private Long id;
    private Long optionId;
    private String optionName;
    private Long optionValueId;
    private String optionValue;
}