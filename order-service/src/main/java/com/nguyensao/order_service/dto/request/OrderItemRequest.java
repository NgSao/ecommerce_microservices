package com.nguyensao.order_service.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private Long variantId;
    private int quantity;
}