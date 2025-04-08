package com.nguyensao.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionCombinationDto {
    private Long id;
    private Long optionId;
    private String optionName;
    private Long optionValueId;
    private String optionValue;
}