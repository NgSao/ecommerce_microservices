package com.nguyensao.order_service.dto.request;

import java.util.List;

import com.nguyensao.order_service.enums.PaymentMethod;
import com.nguyensao.order_service.snaps.AddressSnapshot;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private String voucherCode;
    private AddressSnapshot address;
    private PaymentMethod paymentMethod;
}