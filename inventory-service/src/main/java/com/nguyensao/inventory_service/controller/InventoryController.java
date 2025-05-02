package com.nguyensao.inventory_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.inventory_service.dto.InventoryDto;
import com.nguyensao.inventory_service.dto.request.InventoryRequest;
import com.nguyensao.inventory_service.service.InventoryService;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/admin/import")
    public ResponseEntity<InventoryDto> importInventory(@RequestBody InventoryRequest request) {
        InventoryDto inventoryDto = inventoryService.importInventory(request);
        return ResponseEntity.ok(inventoryDto);
    }

    @PostMapping("/admin/export")
    public ResponseEntity<InventoryDto> exportInventory(@RequestBody InventoryRequest request) {
        InventoryDto inventoryDto = inventoryService.exportInventory(request);
        return ResponseEntity.ok(inventoryDto);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<InventoryDto>> getAllInventories(
            @RequestParam(defaultValue = "all") String sortByStock) {
        List<InventoryDto> inventories = inventoryService.getAllInventories(sortByStock);
        return ResponseEntity.ok(inventories);
    }
}