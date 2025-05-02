package com.nguyensao.order_service.controller;

import com.nguyensao.order_service.dto.request.OrderRequest;
import com.nguyensao.order_service.model.Order;
import com.nguyensao.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // @PostMapping
    // public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
    // return ResponseEntity.ok(orderService.createOrder(request));
    // }

    // @GetMapping("/vnpay-callback")
    // public ResponseEntity<Order> handleVNPayCallback(
    // @RequestParam("vnp_TxnRef") String transactionId,
    // @RequestParam("vnp_TransactionStatus") String status,
    // @RequestParam("vnp_Amount") BigDecimal amount) {
    // // VNPay amount is in VND * 100
    // amount = amount.divide(BigDecimal.valueOf(100));
    // String normalizedStatus = "00".equals(status) ? "SUCCESS" : "FAILED";
    // Order updatedOrder = orderService.handleVNPayCallback(transactionId,
    // normalizedStatus, amount);
    // return ResponseEntity.ok(updatedOrder);
    // }
}