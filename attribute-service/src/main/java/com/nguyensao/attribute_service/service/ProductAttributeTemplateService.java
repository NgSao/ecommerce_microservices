package com.nguyensao.attribute_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.attribute_service.exception.AppException;
import com.nguyensao.attribute_service.model.ProductAttribute;
import com.nguyensao.attribute_service.model.ProductAttributeTemplate;
import com.nguyensao.attribute_service.repository.ProductAttributeRepository;
import com.nguyensao.attribute_service.repository.ProductAttributeTemplateRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductAttributeTemplateService {

    private final ProductAttributeTemplateRepository templateRepository;
    private final ProductAttributeRepository attributeRepository;

    public ProductAttributeTemplateService(
            ProductAttributeTemplateRepository templateRepository,
            ProductAttributeRepository attributeRepository) {
        this.templateRepository = templateRepository;
        this.attributeRepository = attributeRepository;
    }

    public List<ProductAttributeTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public ProductAttributeTemplate getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new AppException("Attribute template not found with id: " + id));
    }

    @Transactional
    public ProductAttributeTemplate createTemplate(ProductAttributeTemplate template) {
        return templateRepository.save(template);
    }

    @Transactional
    public ProductAttributeTemplate updateTemplate(Long id, ProductAttributeTemplate template) {
        ProductAttributeTemplate existingTemplate = getTemplateById(id);

        existingTemplate.setName(template.getName());

        return templateRepository.save(existingTemplate);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        ProductAttributeTemplate template = getTemplateById(id);
        templateRepository.delete(template);
    }

    @Transactional
    public ProductAttributeTemplate addAttributesToTemplate(Long templateId, List<Long> attributeIds) {
        ProductAttributeTemplate template = getTemplateById(templateId);

        Set<ProductAttribute> attributes = new HashSet<>(template.getAttributes());

        for (Long attributeId : attributeIds) {
            ProductAttribute attribute = attributeRepository.findById(attributeId)
                    .orElseThrow(() -> new AppException("Attribute not found with id: " + attributeId));
            attributes.add(attribute);
        }

        template.setAttributes(attributes);
        return templateRepository.save(template);
    }

    @Transactional
    public ProductAttributeTemplate removeAttributeFromTemplate(Long templateId, Long attributeId) {
        ProductAttributeTemplate template = getTemplateById(templateId);

        template.getAttributes().removeIf(attribute -> attribute.getId().equals(attributeId));

        return templateRepository.save(template);
    }
}