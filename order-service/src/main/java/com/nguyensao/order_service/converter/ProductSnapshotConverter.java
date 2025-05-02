package com.nguyensao.order_service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyensao.order_service.snaps.ProductSnapshot;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProductSnapshotConverter implements AttributeConverter<ProductSnapshot, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ProductSnapshot productSnapshot) {
        try {
            return objectMapper.writeValueAsString(productSnapshot);
        } catch (Exception e) {
            throw new RuntimeException("Error converting ProductSnapshot to JSON", e);
        }
    }

    @Override
    public ProductSnapshot convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, ProductSnapshot.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to ProductSnapshot", e);
        }
    }
}