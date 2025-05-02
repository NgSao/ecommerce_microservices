package com.nguyensao.promotion_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

import com.nguyensao.promotion_service.enums.DiscountType;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String codeVoucher;

    @Enumerated(EnumType.STRING)
    DiscountType type;

    BigDecimal value;

    Instant startDate;

    Instant endDate;

    BigDecimal minOrderAmount;

    Integer usageCount;

    Integer maxUsage;

    boolean active;

}
