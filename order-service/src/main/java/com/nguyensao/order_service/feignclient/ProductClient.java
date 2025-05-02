package com.nguyensao.order_service.feignclient;

import com.nguyensao.order_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    @GetMapping("/api/v1/products/public/{id}")
    ProductDto getProductById(@PathVariable("id") Long productId);
}