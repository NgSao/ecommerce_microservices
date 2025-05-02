package com.nguyensao.promotion_service.dto.request;

import java.math.BigDecimal;

import com.nguyensao.promotion_service.enums.DiscountType;

import lombok.Data;

@Data
public class PromotionItemRequest {
    private Long productId;
    private Long variantId;
    private DiscountType discountType;
    private BigDecimal value;
    private int quantity;
    private boolean active;
}