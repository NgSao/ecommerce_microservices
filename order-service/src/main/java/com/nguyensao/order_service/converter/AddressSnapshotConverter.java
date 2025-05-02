package com.nguyensao.order_service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyensao.order_service.snaps.AddressSnapshot;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AddressSnapshotConverter implements AttributeConverter<AddressSnapshot, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AddressSnapshot addressSnapshot) {
        try {
            return objectMapper.writeValueAsString(addressSnapshot);
        } catch (Exception e) {
            throw new RuntimeException("Error converting AddressSnapshot to JSON", e);
        }
    }

    @Override
    public AddressSnapshot convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, AddressSnapshot.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to ProductSnapshot", e);
        }
    }
}