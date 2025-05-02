package com.nguyensao.order_service.service;

import com.nguyensao.order_service.repository.OrderItemRepository;
import com.nguyensao.order_service.repository.OrderRepository;

import com.nguyensao.order_service.feignclient.ProductClient;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productClient = productClient;
    }

    // @Transactional
    // public Order createOrder(OrderRequest request) {
    // // Validate products and variants
    // Set<OrderItem> orderItems = validateAndCreateOrderItems(request);

    // // Calculate totals
    // BigDecimal subTotal = orderItems.stream()
    // .map(item ->
    // item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
    // .reduce(BigDecimal.ZERO, BigDecimal::add);
    // BigDecimal discountAmount = calculateDiscount(request.getVoucherCode(),
    // subTotal);
    // // BigDecimal shippingFee = calculateShippingFee(request.getAddress());
    // BigDecimal totalAmount = subTotal.subtract(discountAmount).add(shippingFee);

    // // Create Order
    // Order order = new Order();
    // order.setUserId(request.getUserId());
    // order.setStatus(OrderStatus.PENDING);
    // order.setSubTotal(subTotal);
    // order.setDiscountAmount(discountAmount);
    // order.setVoucherCode(request.getVoucherCode());
    // order.setShippingFee(shippingFee);
    // order.setTotalAmount(totalAmount);
    // order.setAddress(request.getAddress());
    // order.setPaymentMethod(request.getPaymentMethod());
    // order.setPaymentStatus(PaymentStatus.PENDING);
    // order.setOrderItems(orderItems);

    // // Associate orderItems with order
    // orderItems.forEach(item -> item.setOrder(order));

    // // Save order
    // Order savedOrder = orderRepository.save(order);

    // // Initiate payment if VNPAY
    // if (PaymentMethod.VNPAY.equals(request.getPaymentMethod())) {
    // String paymentUrl = vnPayService.createPaymentUrl(savedOrder);
    // // Return payment URL to client (or handle redirection)
    // savedOrder.setPaymentTransactionId("VNPAY_" + savedOrder.getId()); //
    // Temporary, update after payment
    // }

    // return savedOrder;
    // }

    // private Set<OrderItem> validateAndCreateOrderItems(OrderRequest request) {
    // return request.getItems().stream().map(itemRequest -> {
    // ProductDto product =
    // productClient.getProductById(itemRequest.getProductId());
    // if (product == null) {
    // throw new RuntimeException("Product not found: " +
    // itemRequest.getProductId());
    // }

    // VariantDto variant = null;
    // if (itemRequest.getVariantId() != null) {
    // variant = product.getVariants().stream()
    // .filter(v -> v.getId().equals(itemRequest.getVariantId()))
    // .findFirst()
    // .orElseThrow(() -> new RuntimeException("Variant not found: " +
    // itemRequest.getVariantId()));
    // }

    // // Validate stock
    // int availableStock = variant != null ? variant.getStockQuantity() :
    // product.getStock();
    // if (itemRequest.getQuantity() > availableStock) {
    // throw new RuntimeException("Insufficient stock for product: " +
    // product.getName());
    // }

    // // Create OrderItem
    // OrderItem orderItem = new OrderItem();
    // orderItem.setProductId(itemRequest.getProductId());
    // orderItem.setVariantId(itemRequest.getVariantId());
    // orderItem.setQuantity(itemRequest.getQuantity());
    // orderItem.setPrice(variant != null ? variant.getSalePrice() :
    // product.getSalePrice());
    // orderItem.setDiscount(BigDecimal.ZERO); // Add discount logic if needed
    // orderItem.setProductSnapshot(createProductSnapshot(product, variant));
    // return orderItem;
    // }).collect(Collectors.toSet());
    // }

    // private ProductSnapshot createProductSnapshot(ProductDto product, VariantDto
    // variant) {
    // ProductSnapshot snapshot = new ProductSnapshot();
    // snapshot.setName(product.getName());
    // snapshot.setOriginalPrice(product.getOriginalPrice());
    // snapshot.setSalePrice(product.getSalePrice());
    // snapshot.setQuantity(product.getStock());
    // if (variant != null) {
    // snapshot.setColor(variant.getColor());
    // snapshot.setSize(variant.getSize());
    // snapshot.setImageUrl(variant.getImageUrl());
    // }
    // return snapshot;
    // }

    // private BigDecimal calculateDiscount(String voucherCode, BigDecimal subTotal)
    // {
    // // Implement voucher logic (e.g., call promotion-service)
    // return BigDecimal.ZERO; // Placeholder
    // }

    // private BigDecimal calculateShippingFee(String address) {
    // // Implement shipping fee calculation
    // return BigDecimal.valueOf(30000); // Placeholder
    // }

    // @Transactional
    // public Order handleVNPayCallback(String transactionId, String status,
    // BigDecimal amount) {
    // Order order = orderRepository.findByPaymentTransactionId(transactionId)
    // .orElseThrow(() -> new RuntimeException("Order not found for transaction: " +
    // transactionId));

    // if ("SUCCESS".equals(status) && amount.compareTo(order.getTotalAmount()) ==
    // 0) {
    // order.setPaymentStatus(PaymentStatus.COMPLETED);
    // order.setStatus(OrderStatus.CONFIRMED);
    // order.setPaymentDate(LocalDateTime.now());
    // } else {
    // order.setPaymentStatus(PaymentStatus.FAILED);
    // order.setStatus(OrderStatus.CANCELLED);
    // }

    // return orderRepository.save(order);
    // }
}