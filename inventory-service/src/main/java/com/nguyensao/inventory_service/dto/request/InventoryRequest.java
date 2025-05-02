package com.nguyensao.inventory_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {
    private String skuProduct;
    private String skuVariant;
    private Integer quantity;
}
