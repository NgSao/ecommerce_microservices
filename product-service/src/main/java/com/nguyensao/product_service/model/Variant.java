package com.nguyensao.product_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "variants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Variant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    String color;
    String size;

    String sku;
    String slug;

    String imageUrl;

    BigDecimal originalPrice;

    BigDecimal salePrice;

    Integer stockQuantity;

    int displayOrder;

}
