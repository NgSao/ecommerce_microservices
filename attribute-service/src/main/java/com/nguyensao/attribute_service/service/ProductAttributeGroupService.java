package com.nguyensao.attribute_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.attribute_service.dto.AttributeGroupDto;
import com.nguyensao.attribute_service.exception.AppException;
import com.nguyensao.attribute_service.model.ProductAttributeGroup;
import com.nguyensao.attribute_service.repository.ProductAttributeGroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeGroupService {

    private final ProductAttributeGroupRepository groupRepository;
    private final ProductAttributeService attributeService;

    public ProductAttributeGroupService(
            ProductAttributeGroupRepository groupRepository,
            ProductAttributeService attributeService) {
        this.groupRepository = groupRepository;
        this.attributeService = attributeService;
    }

    public List<ProductAttributeGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public ProductAttributeGroup getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new AppException("Attribute group not found with id: " + id));
    }

    @Transactional
    public ProductAttributeGroup createGroup(ProductAttributeGroup group) {
        return groupRepository.save(group);
    }

    @Transactional
    public ProductAttributeGroup updateGroup(Long id, ProductAttributeGroup group) {
        ProductAttributeGroup existingGroup = getGroupById(id);

        existingGroup.setName(group.getName());
        existingGroup.setDisplayOrder(group.getDisplayOrder());

        return groupRepository.save(existingGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        ProductAttributeGroup group = getGroupById(id);
        groupRepository.delete(group);
    }

    public List<AttributeGroupDto> getAllGroupDtos() {
        return groupRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AttributeGroupDto getGroupDtoById(Long id) {
        ProductAttributeGroup group = getGroupById(id);
        return convertToDto(group);
    }

    private AttributeGroupDto convertToDto(ProductAttributeGroup group) {
        AttributeGroupDto dto = new AttributeGroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDisplayOrder(group.getDisplayOrder());
        dto.setAttributes(group.getAttributes().stream()
                .map(attribute -> attributeService.convertToDto(attribute))
                .collect(Collectors.toList()));

        return dto;
    }
}