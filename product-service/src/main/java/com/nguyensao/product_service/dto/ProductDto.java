package com.nguyensao.product_service.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
public class ProductDto {
    Long id;
    String name;
    String slug;
    String sku;
    String specification;
    String description;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    Integer stock;

    Integer sold;

    Integer rating;
    BrandDto brand;
    Set<CategoryDto> categories = new HashSet<>();
    Set<MediaDto> images = new HashSet<>();
    Set<VariantDto> variants = new HashSet<>();

    public String getFormattedOriginalPrice() {
        return CurrencyUtils.formatVndCurrency(originalPrice);
    }

    public String getFormattedSalePrice() {
        return CurrencyUtils.formatVndCurrency(salePrice);
    }
}
