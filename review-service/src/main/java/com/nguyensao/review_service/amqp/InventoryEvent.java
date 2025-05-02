package com.nguyensao.review_service.amqp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryEvent {
    EventEnum eventType;
    String skuProduct;
    Integer rating;
}
