package com.nguyensao.order_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.nguyensao.order_service.converter.AddressSnapshotConverter;
import com.nguyensao.order_service.enums.OrderStatus;
import com.nguyensao.order_service.enums.PaymentMethod;
import com.nguyensao.order_service.enums.PaymentStatus;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    BigDecimal subTotal;
    BigDecimal discountAmount;
    String voucherCode;
    BigDecimal shippingFee;
    BigDecimal totalAmount;

    @Convert(converter = AddressSnapshotConverter.class)
    @Column(columnDefinition = "JSON")
    String address;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    Set<OrderItem> orderItems = new HashSet<>();
}
