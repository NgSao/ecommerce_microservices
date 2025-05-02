package com.nguyensao.promotion_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.nguyensao.promotion_service.enums.PromotionType;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    Instant startDate;

    Instant endDate;

    @Enumerated(EnumType.STRING)
    PromotionType type;

    boolean active;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    Set<PromotionItem> promotionItems = new HashSet<>();

}
