package com.nguyensao.order_service.enums;

public enum OrderStatus {
    PENDING, // Chờ xác nhận
    CONFIRMED, // Đã xác nhận
    SHIPPING, // Đang giao hàng
    SHIPPED, // Giao thành công
    CANCELLED // Đã hủy
}
