package com.nguyensao.attribute_service.feignclient;

import org.springframework.stereotype.Component;

import com.nguyensao.attribute_service.dto.ProductDto;

@Component
public class ProductCatalogServiceClientFallback implements ProductCatalogServiceClient {

    @Override
    public ProductDto getProductById(Long id) {
        System.err.println("Fallback: Product not found with id: " + id);
        return null;
    }

    @Override
    public ProductDto getProductBySku(String sku) {
        return null;
    }
}