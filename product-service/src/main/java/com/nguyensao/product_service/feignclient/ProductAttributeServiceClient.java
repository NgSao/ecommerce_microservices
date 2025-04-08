package com.nguyensao.product_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.product_service.dto.AttributeValueDto;
import com.nguyensao.product_service.dto.OptionCombinationDto;

import java.util.List;

@FeignClient(name = "attribute-service", url = "${application.config.attribute-url}", fallback = ProductAttributeServiceClientFallback.class)
public interface ProductAttributeServiceClient {

        @GetMapping("/api/attributes/products/{productId}/values")
        List<AttributeValueDto> getAttributeValuesByProductId(@PathVariable("productId") Long productId);

        @PostMapping("/api/attributes/products/{productId}/values")
        List<AttributeValueDto> addAttributeValuesToProduct(
                        @PathVariable("productId") Long productId,
                        @RequestBody List<AttributeValueDto> attributeValues);

        @PutMapping("/api/attributes/products/{productId}/values")
        ResponseEntity<Void> updateAttributeValuesForProduct(
                        @PathVariable("productId") Long productId,
                        @RequestBody List<AttributeValueDto> attributeValues);

        @DeleteMapping("/api/attributes/products/{productId}")
        ResponseEntity<Void> deleteProductAttributes(@PathVariable("productId") Long productId);

        @GetMapping("/api/options/products/{productId}/combinations")
        List<OptionCombinationDto> getOptionCombinationsByProductId(@PathVariable("productId") Long productId);

        @PostMapping("/api/options/products/{productId}/combinations")
        List<OptionCombinationDto> addOptionCombinationsToProduct(
                        @PathVariable("productId") Long productId,
                        @RequestBody List<OptionCombinationDto> combinations);

        @DeleteMapping("/api/options/products/{productId}/combinations")
        ResponseEntity<Void> deleteOptionCombinationsByProductId(@PathVariable("productId") Long productId);
}