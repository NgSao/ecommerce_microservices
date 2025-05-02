package com.nguyensao.product_service.dto.request;

import java.math.BigDecimal;

import com.nguyensao.product_service.exception.AppException;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class VariantRequest {

    Long productId;

    String color;

    String size;

    String imageUrl;

    @NotNull(message = "Giá gốc không được để trống")
    @Positive(message = "Giá gốc phải lớn hơn 0")
    BigDecimal originalPrice;

    @NotNull(message = "Giá bán không được để trống")
    @Positive(message = "Giá bán phải lớn hơn 0")
    BigDecimal salePrice;

    @Positive(message = "Số lượng tồn kho phải lớn hơn 0")
    Integer stock;

    Integer stockQuantity;

    Integer displayOrder;

    public void validatePrices() {
        if (salePrice != null && originalPrice != null && salePrice.compareTo(originalPrice) > 0) {
            throw new AppException("Giá bán không được lớn hơn giá gốc");
        }
    }
}
