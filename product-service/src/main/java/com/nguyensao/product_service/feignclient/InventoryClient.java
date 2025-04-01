package com.nguyensao.product_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nguyensao.product_service.model.Inventory;

@FeignClient(name = "inventory-service", url = "${application.config.inventory-url}")
public interface InventoryClient {
    @GetMapping("/api/v1/inventories/{id}")
    public Inventory getInventory(@PathVariable("id") String id);

}
