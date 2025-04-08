package com.nguyensao.attribute_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.attribute_service.dto.AttributeDto;
import com.nguyensao.attribute_service.dto.AttributeValueDto;
import com.nguyensao.attribute_service.dto.ProductDto;
import com.nguyensao.attribute_service.exception.AppException;
import com.nguyensao.attribute_service.feignclient.ProductCatalogServiceClient;
import com.nguyensao.attribute_service.model.ProductAttribute;
import com.nguyensao.attribute_service.model.ProductAttributeValue;
import com.nguyensao.attribute_service.repository.ProductAttributeRepository;
import com.nguyensao.attribute_service.repository.ProductAttributeValueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeValueRepository attributeValueRepository;
    private final ProductCatalogServiceClient catalogServiceClient;

    public ProductAttributeService(
            ProductAttributeRepository attributeRepository,
            ProductAttributeValueRepository attributeValueRepository,
            ProductCatalogServiceClient catalogServiceClient) {
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    public List<ProductAttribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    public ProductAttribute getAttributeById(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Attribute not found with id: " + id));
    }

    public List<ProductAttribute> getAttributesByGroupId(Long groupId) {
        return attributeRepository.findByGroupId(groupId);
    }

    @Transactional
    public ProductAttribute createAttribute(ProductAttribute attribute) {
        return attributeRepository.save(attribute);
    }

    @Transactional
    public ProductAttribute updateAttribute(Long id, ProductAttribute attribute) {
        ProductAttribute existingAttribute = getAttributeById(id);

        existingAttribute.setName(attribute.getName());
        if (attribute.getGroup() != null) {
            existingAttribute.setGroup(attribute.getGroup());
        }

        return attributeRepository.save(existingAttribute);
    }

    @Transactional
    public void deleteAttribute(Long id) {
        ProductAttribute attribute = getAttributeById(id);
        attributeRepository.delete(attribute);
    }

    public List<ProductAttributeValue> getAttributeValuesByProductId(Long productId) {
        // Kiểm tra xem product có tồn tại không
        ProductDto product = catalogServiceClient.getProductById(productId);
        if (product == null) {
            throw new AppException("Product not found with id: " + productId);
        }

        return attributeValueRepository.findByProductId(productId);
    }

    public List<AttributeValueDto> getAttributeValueDtosByProductId(Long productId) {
        List<ProductAttributeValue> values = getAttributeValuesByProductId(productId);
        return values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductAttributeValue> addAttributeValuesToProduct(Long productId,
            List<AttributeValueDto> attributeValues) {
        // Kiểm tra xem product có tồn tại không
        ProductDto product = catalogServiceClient.getProductById(productId);
        if (product == null) {
            throw new AppException("Product not found with id: " + productId);
        }

        List<ProductAttributeValue> values = attributeValues.stream()
                .map(dto -> {
                    ProductAttribute attribute = attributeRepository.findById(dto.getAttributeId())
                            .orElseThrow(
                                    () -> new AppException("Attribute not found with id: " + dto.getAttributeId()));

                    ProductAttributeValue value = new ProductAttributeValue();
                    value.setProductId(productId);
                    value.setAttribute(attribute);
                    value.setValue(dto.getValue());

                    return value;
                })
                .collect(Collectors.toList());

        return attributeValueRepository.saveAll(values);
    }

    @Transactional
    public void updateAttributeValuesForProduct(Long productId, List<AttributeValueDto> attributeValues) {
        // Kiểm tra xem product có tồn tại không
        ProductDto product = catalogServiceClient.getProductById(productId);
        if (product == null) {
            throw new AppException("Product not found with id: " + productId);
        }

        // Xóa attribute values hiện tại
        attributeValueRepository.deleteByProductId(productId);

        // Thêm attribute values mới
        if (attributeValues != null && !attributeValues.isEmpty()) {
            addAttributeValuesToProduct(productId, attributeValues);
        }
    }

    @Transactional
    public void deleteAttributesByProductId(Long productId) {
        attributeValueRepository.deleteByProductId(productId);
    }

    public List<AttributeDto> getAllAttributeDtos() {
        return attributeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AttributeDto convertToDto(ProductAttribute attribute) {
        AttributeDto dto = new AttributeDto();
        dto.setId(attribute.getId());
        dto.setName(attribute.getName());

        if (attribute.getGroup() != null) {
            dto.setGroupId(attribute.getGroup().getId());
            dto.setGroupName(attribute.getGroup().getName());
        }

        return dto;
    }

    private AttributeValueDto convertToDto(ProductAttributeValue attributeValue) {
        AttributeValueDto dto = new AttributeValueDto();
        dto.setId(attributeValue.getId());
        dto.setAttributeId(attributeValue.getAttribute().getId());
        dto.setAttributeName(attributeValue.getAttribute().getName());
        dto.setValue(attributeValue.getValue());
        return dto;
    }
}