package com.nguyensao.promotion_service.dto.request;

import com.nguyensao.promotion_service.enums.DiscountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class VoucherRequest {
    private String codeVoucher;
    private DiscountType type;
    private BigDecimal value;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal minOrderAmount;
    private Integer maxUsage;
    private boolean active;
}