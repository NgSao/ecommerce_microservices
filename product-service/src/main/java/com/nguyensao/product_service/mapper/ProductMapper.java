package com.nguyensao.product_service.mapper;

import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDto toProductDto(Product product) {
        if (product == null)
            return null;

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    public Product toProductEntity(ProductDto productDto) {
        if (productDto == null)
            return null;

        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .imageUrl(productDto.getImageUrl())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .active(productDto.getActive())
                .build();
    }
}
