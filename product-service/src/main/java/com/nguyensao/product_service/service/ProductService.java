package com.nguyensao.product_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.product_service.dto.AttributeValueDto;
import com.nguyensao.product_service.dto.OptionCombinationDto;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.feignclient.ProductAttributeServiceClient;
import com.nguyensao.product_service.model.Brand;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.model.ProductCategory;
import com.nguyensao.product_service.model.ProductImage;
import com.nguyensao.product_service.model.ProductRelated;
import com.nguyensao.product_service.repository.BrandRepository;
import com.nguyensao.product_service.repository.ProductCategoryRepository;
import com.nguyensao.product_service.repository.ProductImageRepository;
import com.nguyensao.product_service.repository.ProductRelatedRepository;
import com.nguyensao.product_service.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductImageRepository imageRepository;
    private final ProductRelatedRepository relatedRepository;
    private final ProductAttributeServiceClient attributeServiceClient;

    public ProductService(
            ProductRepository productRepository,
            BrandRepository brandRepository,
            ProductCategoryRepository categoryRepository,
            ProductImageRepository imageRepository,
            ProductRelatedRepository relatedRepository,
            ProductAttributeServiceClient attributeServiceClient) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.relatedRepository = relatedRepository;
        this.attributeServiceClient = attributeServiceClient;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getPublishedProducts() {
        return productRepository.findByIsPublishedTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    public List<Product> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId);
    }

    public List<Product> searchProductsByKeyword(String keyword) {
        return productRepository.findByNameContainingOrShortDescriptionContainingOrDescriptionContaining(
                keyword, keyword, keyword);
    }

    public List<Product> searchProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional
    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        updateProductFromDto(product, productDto);

        Product savedProduct = productRepository.save(product);

        // Save images if provided
        if (productDto.getImages() != null && !productDto.getImages().isEmpty()) {
            for (var imageDto : productDto.getImages()) {
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageUrl(imageDto.getImageUrl());
                image.setCaption(imageDto.getCaption());
                image.setDisplayOrder(imageDto.getDisplayOrder());
                image.setMain(imageDto.isMain());
                imageRepository.save(image);
            }
        }

        // Save related products if provided
        if (productDto.getRelatedProductIds() != null && !productDto.getRelatedProductIds().isEmpty()) {
            for (Long relatedId : productDto.getRelatedProductIds()) {
                Product relatedProduct = productRepository.findById(relatedId)
                        .orElseThrow(() -> new AppException("Related product not found with id: " + relatedId));

                ProductRelated related = new ProductRelated();
                related.setProduct(savedProduct);
                related.setRelatedProduct(relatedProduct);
                relatedRepository.save(related);
            }
        }

        // Save attribute values if provided
        if (productDto.getAttributeValues() != null && !productDto.getAttributeValues().isEmpty()) {
            attributeServiceClient.addAttributeValuesToProduct(savedProduct.getId(), productDto.getAttributeValues());
        }

        // Save option combinations if provided
        if (productDto.getOptionCombinations() != null && !productDto.getOptionCombinations().isEmpty()) {
            attributeServiceClient.addOptionCombinationsToProduct(savedProduct.getId(),
                    productDto.getOptionCombinations());
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found with id: " + id));

        updateProductFromDto(product, productDto);

        Product updatedProduct = productRepository.save(product);

        // Update attribute values if provided
        if (productDto.getAttributeValues() != null) {
            attributeServiceClient.updateAttributeValuesForProduct(id, productDto.getAttributeValues());
        }

        // Update option combinations if provided
        if (productDto.getOptionCombinations() != null) {
            attributeServiceClient.deleteOptionCombinationsByProductId(id);
            if (!productDto.getOptionCombinations().isEmpty()) {
                attributeServiceClient.addOptionCombinationsToProduct(id, productDto.getOptionCombinations());
            }
        }

        return updatedProduct;
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException("Product not found with id: " + id);
        }

        // Delete attribute values and option combinations
        attributeServiceClient.deleteProductAttributes(id);
        attributeServiceClient.deleteOptionCombinationsByProductId(id);

        // Delete the product
        productRepository.deleteById(id);
    }

    public List<AttributeValueDto> getProductAttributes(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new AppException("Product not found with id: " + productId);
        }

        return attributeServiceClient.getAttributeValuesByProductId(productId);
    }

    public List<OptionCombinationDto> getProductOptionCombinations(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new AppException("Product not found with id: " + productId);
        }

        return attributeServiceClient.getOptionCombinationsByProductId(productId);
    }

    public ProductDto getProductDtoById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found with id: " + id));

        return convertToDto(product);
    }

    private void updateProductFromDto(Product product, ProductDto dto) {
        product.setName(dto.getName());
        product.setShortDescription(dto.getShortDescription());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setOldPrice(dto.getOldPrice());
        product.setSpecialPrice(dto.getSpecialPrice());
        product.setPublished(dto.isPublished());

        // Set brand if provided
        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new AppException("Brand not found with id: " + dto.getBrandId()));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        // Set parent product if provided
        if (dto.getParentId() != null) {
            Product parent = productRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new AppException(
                            "Parent product not found with id: " + dto.getParentId()));
            product.setParent(parent);
        } else {
            product.setParent(null);
        }

        // Set categories
        if (dto.getCategoryIds() != null) {
            Set<ProductCategory> categories = new HashSet<>();
            for (Long categoryId : dto.getCategoryIds()) {
                ProductCategory category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new AppException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            product.setCategories(categories);
        }
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setShortDescription(product.getShortDescription());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setOldPrice(product.getOldPrice());
        dto.setSpecialPrice(product.getSpecialPrice());
        dto.setPublished(product.isPublished());

        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getName());
        }

        if (product.getParent() != null) {
            dto.setParentId(product.getParent().getId());
        }

        // Set category IDs
        Set<Long> categoryIds = product.getCategories().stream()
                .map(ProductCategory::getId)
                .collect(Collectors.toSet());
        dto.setCategoryIds(categoryIds);

        // Get attribute values
        try {
            List<AttributeValueDto> attributeValues = attributeServiceClient
                    .getAttributeValuesByProductId(product.getId());
            dto.setAttributeValues(attributeValues);
        } catch (Exception e) {
            // Handle exception or log it
        }

        // Get option combinations
        try {
            List<OptionCombinationDto> optionCombinations = attributeServiceClient
                    .getOptionCombinationsByProductId(product.getId());
            dto.setOptionCombinations(optionCombinations);
        } catch (Exception e) {
            // Handle exception or log it
        }

        return dto;
    }
}