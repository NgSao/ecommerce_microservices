package com.nguyensao.promotion_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nguyensao.promotion_service.dto.ProductDto;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    @GetMapping("/api/v1/products/public/{id}")
    ProductDto getProductById(@PathVariable("id") Long productId);

}