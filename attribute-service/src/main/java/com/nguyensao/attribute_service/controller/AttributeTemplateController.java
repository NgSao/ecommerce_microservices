package com.nguyensao.attribute_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.attribute_service.model.ProductAttributeTemplate;
import com.nguyensao.attribute_service.service.ProductAttributeTemplateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attribute-templates")
public class AttributeTemplateController {

    private final ProductAttributeTemplateService templateService;

    public AttributeTemplateController(ProductAttributeTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<ProductAttributeTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeTemplate> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @PostMapping
    public ResponseEntity<ProductAttributeTemplate> createTemplate(@RequestBody ProductAttributeTemplate template) {
        return new ResponseEntity<>(templateService.createTemplate(template), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAttributeTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody ProductAttributeTemplate template) {
        return ResponseEntity.ok(templateService.updateTemplate(id, template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{templateId}/attributes")
    public ResponseEntity<ProductAttributeTemplate> addAttributesToTemplate(
            @PathVariable Long templateId,
            @RequestBody Map<String, List<Long>> payload) {

        List<Long> attributeIds = payload.get("attributeIds");
        return ResponseEntity.ok(templateService.addAttributesToTemplate(templateId, attributeIds));
    }

    @DeleteMapping("/{templateId}/attributes/{attributeId}")
    public ResponseEntity<ProductAttributeTemplate> removeAttributeFromTemplate(
            @PathVariable Long templateId,
            @PathVariable Long attributeId) {
        return ResponseEntity.ok(templateService.removeAttributeFromTemplate(templateId, attributeId));
    }
}