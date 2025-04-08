package com.nguyensao.attribute_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.attribute_service.dto.AttributeGroupDto;
import com.nguyensao.attribute_service.model.ProductAttributeGroup;
import com.nguyensao.attribute_service.service.ProductAttributeGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/attribute-groups")
public class AttributeGroupController {

    private final ProductAttributeGroupService groupService;

    public AttributeGroupController(ProductAttributeGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<ProductAttributeGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<AttributeGroupDto>> getAllGroupDtos() {
        return ResponseEntity.ok(groupService.getAllGroupDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeGroup> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<AttributeGroupDto> getGroupDtoById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupDtoById(id));
    }

    @PostMapping
    public ResponseEntity<ProductAttributeGroup> createGroup(@RequestBody ProductAttributeGroup group) {
        return new ResponseEntity<>(groupService.createGroup(group), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAttributeGroup> updateGroup(
            @PathVariable Long id,
            @RequestBody ProductAttributeGroup group) {
        return ResponseEntity.ok(groupService.updateGroup(id, group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}