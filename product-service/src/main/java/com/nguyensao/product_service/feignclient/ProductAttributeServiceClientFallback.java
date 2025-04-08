package com.nguyensao.product_service.feignclient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.nguyensao.product_service.dto.AttributeValueDto;
import com.nguyensao.product_service.dto.OptionCombinationDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductAttributeServiceClientFallback implements ProductAttributeServiceClient {

    @Override
    public List<AttributeValueDto> getAttributeValuesByProductId(Long productId) {
        return new ArrayList<>();
    }

    @Override
    public List<AttributeValueDto> addAttributeValuesToProduct(Long productId,
            List<AttributeValueDto> attributeValues) {
        return new ArrayList<>();
    }

    @Override
    public ResponseEntity<Void> updateAttributeValuesForProduct(Long productId,
            List<AttributeValueDto> attributeValues) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteProductAttributes(Long productId) {
        return ResponseEntity.ok().build();
    }

    @Override
    public List<OptionCombinationDto> getOptionCombinationsByProductId(Long productId) {
        return new ArrayList<>();
    }

    @Override
    public List<OptionCombinationDto> addOptionCombinationsToProduct(Long productId,
            List<OptionCombinationDto> combinations) {
        return new ArrayList<>();
    }

    @Override
    public ResponseEntity<Void> deleteOptionCombinationsByProductId(Long productId) {
        return ResponseEntity.ok().build();
    }
}