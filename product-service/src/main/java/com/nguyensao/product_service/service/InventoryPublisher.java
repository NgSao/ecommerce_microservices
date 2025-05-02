package com.nguyensao.product_service.service;

import com.nguyensao.product_service.amqp.EventEnum;
import com.nguyensao.product_service.amqp.InventoryEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "inventory.exchange";
    private static final String ROUTING_KEY = "inventory.event";

    public InventoryPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishInventoryEvent(EventEnum eventType, String skuProduct, String skuVariant, Integer quantity) {
        InventoryEvent inventoryEvent = new InventoryEvent();
        inventoryEvent.setEventType(eventType);
        inventoryEvent.setSkuProduct(skuProduct);
        inventoryEvent.setSkuVariant(skuVariant);
        inventoryEvent.setQuantity(quantity);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, inventoryEvent);
    }
}