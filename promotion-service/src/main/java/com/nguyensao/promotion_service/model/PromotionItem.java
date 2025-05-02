package com.nguyensao.promotion_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;

import com.nguyensao.promotion_service.enums.DiscountType;

@Entity
@Table(name = "promotion_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    Promotion promotion;

    Long productId;

    Long variantId;

    @Enumerated(EnumType.STRING)
    DiscountType type;

    BigDecimal value;

    int quantity;

    boolean active;

}
