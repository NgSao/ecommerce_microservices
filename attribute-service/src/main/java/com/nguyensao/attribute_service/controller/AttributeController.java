package com.nguyensao.attribute_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.attribute_service.dto.AttributeDto;
import com.nguyensao.attribute_service.dto.AttributeValueDto;
import com.nguyensao.attribute_service.model.ProductAttribute;
import com.nguyensao.attribute_service.model.ProductAttributeValue;
import com.nguyensao.attribute_service.service.ProductAttributeService;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    private final ProductAttributeService attributeService;

    public AttributeController(ProductAttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping
    public ResponseEntity<List<ProductAttribute>> getAllAttributes() {
        return ResponseEntity.ok(attributeService.getAllAttributes());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<AttributeDto>> getAllAttributeDtos() {
        return ResponseEntity.ok(attributeService.getAllAttributeDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttribute> getAttributeById(@PathVariable Long id) {
        return ResponseEntity.ok(attributeService.getAttributeById(id));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ProductAttribute>> getAttributesByGroupId(@PathVariable Long groupId) {
        return ResponseEntity.ok(attributeService.getAttributesByGroupId(groupId));
    }

    @PostMapping
    public ResponseEntity<ProductAttribute> createAttribute(@RequestBody ProductAttribute attribute) {
        return new ResponseEntity<>(attributeService.createAttribute(attribute), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAttribute> updateAttribute(
            @PathVariable Long id,
            @RequestBody ProductAttribute attribute) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, attribute));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/{productId}/values")
    public ResponseEntity<List<AttributeValueDto>> getAttributeValuesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(attributeService.getAttributeValueDtosByProductId(productId));
    }

    @PostMapping("/products/{productId}/values")
    public ResponseEntity<List<ProductAttributeValue>> addAttributeValuesToProduct(
            @PathVariable Long productId,
            @RequestBody List<AttributeValueDto> attributeValues) {
        return new ResponseEntity<>(
                attributeService.addAttributeValuesToProduct(productId, attributeValues),
                HttpStatus.CREATED);
    }

    @PutMapping("/products/{productId}/values")
    public ResponseEntity<Void> updateAttributeValuesForProduct(
            @PathVariable Long productId,
            @RequestBody List<AttributeValueDto> attributeValues) {
        attributeService.updateAttributeValuesForProduct(productId, attributeValues);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProductAttributes(@PathVariable Long productId) {
        attributeService.deleteAttributesByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}