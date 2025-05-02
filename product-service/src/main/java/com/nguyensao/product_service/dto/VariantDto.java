package com.nguyensao.product_service.dto;

import java.math.BigDecimal;

import com.nguyensao.product_service.utils.CurrencyUtils;

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

    String imageUrl;

    BigDecimal originalPrice;

    BigDecimal salePrice;

    Integer stockQuantity;

    Integer displayOrder;

    public String getFormattedOriginalPrice() {
        return CurrencyUtils.formatVndCurrency(originalPrice);
    }

    public String getFormattedSalePrice() {
        return CurrencyUtils.formatVndCurrency(salePrice);
    }
}
