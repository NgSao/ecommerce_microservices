package com.nguyensao.order_service.snaps;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSnapshot {
    String name;
    String color;
    String size;
    String imageUrl;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    int quantity;

}
