package com.nguyensao.attribute_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nguyensao.attribute_service.dto.ProductDto;

@FeignClient(name = "product-service", url = "${application.config.product-url}", fallback = ProductCatalogServiceClientFallback.class)
public interface ProductCatalogServiceClient {

    @GetMapping("/api/catalog/products/{id}/dto")
    ProductDto getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/catalog/products/sku/{sku}")
    ProductDto getProductBySku(@PathVariable("sku") String sku);
}