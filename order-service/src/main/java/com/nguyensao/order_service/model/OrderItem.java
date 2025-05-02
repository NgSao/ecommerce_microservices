package com.nguyensao.order_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;

import com.nguyensao.order_service.converter.ProductSnapshotConverter;
import com.nguyensao.order_service.snaps.ProductSnapshot;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long productId;

    Long variantId;

    int quantity;
    BigDecimal price;

    BigDecimal discount;

    @Convert(converter = ProductSnapshotConverter.class)
    @Column(columnDefinition = "JSON")
    ProductSnapshot productSnapshot;
}
