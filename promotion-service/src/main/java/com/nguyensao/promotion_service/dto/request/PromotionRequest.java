package com.nguyensao.promotion_service.dto.request;

import java.time.Instant;
import java.util.Set;

import com.nguyensao.promotion_service.enums.PromotionType;

import lombok.Data;

@Data
public class PromotionRequest {
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private PromotionType type;
    private boolean active;
    private Set<PromotionItemRequest> items;
}
