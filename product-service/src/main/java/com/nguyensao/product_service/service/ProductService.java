package com.nguyensao.product_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nguyensao.product_service.amqp.EventEnum;
import com.nguyensao.product_service.constant.ExceptionConstant;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.dto.VariantDto;
import com.nguyensao.product_service.dto.request.ProductRequest;
import com.nguyensao.product_service.dto.request.VariantRequest;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.mapper.ProductMapper;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.model.Variant;
import com.nguyensao.product_service.repository.ProductRepository;
import com.nguyensao.product_service.repository.VariantRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final ProductMapper productMapper;
    private final InventoryPublisher inventoryPublisher;

    public ProductService(ProductRepository productRepository, VariantRepository variantRepository,
            ProductMapper productMapper, InventoryPublisher inventoryPublisher) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productMapper = productMapper;
        this.inventoryPublisher = inventoryPublisher;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToDto)
                .toList();

    }

    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.PRODUCT_NOT_FOUND));
        return productMapper.productToDto(product);
    }

    public ProductDto createProduct(ProductRequest request) {
        Product product = productMapper.productToEntity(request);
        productRepository.save(product);
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                inventoryPublisher.publishInventoryEvent(EventEnum.CREATE_INVENTORY, product.getSku(), null,
                        request.getStock());
            }
        } else {
            for (Variant variant : product.getVariants()) {
                if (variant.getStockQuantity() != null && variant.getStockQuantity() >= 0) {
                    inventoryPublisher.publishInventoryEvent(EventEnum.CREATE_INVENTORY, product.getSku(),
                            variant.getSku(),
                            variant.getStockQuantity());
                } else {
                    throw new AppException("Số lượng biến thể không hợp lệ");
                }
            }
        }
        return productMapper.productToDto(product);
    }

    public ProductDto updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.PRODUCT_NOT_FOUND));
        productMapper.productUpdatedToEntity(existingProduct, request);
        Product product = productRepository.save(existingProduct);
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                inventoryPublisher.publishInventoryEvent(EventEnum.UPDATE_INVENTORY, product.getSku(), null,
                        request.getStock());
            }
        } else {
            for (Variant variant : product.getVariants()) {
                if (variant.getStockQuantity() != null) {
                    inventoryPublisher.publishInventoryEvent(EventEnum.UPDATE_INVENTORY, product.getSku(),
                            variant.getSku(),
                            variant.getStockQuantity());
                }
            }
        }
        return productMapper.productToDto(product);
    }

    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.PRODUCT_NOT_FOUND));
        productRepository.delete(existingProduct);
        if (existingProduct.getVariants() == null || existingProduct.getVariants().isEmpty()) {
            inventoryPublisher.publishInventoryEvent(EventEnum.DELETE_INVENTORY, existingProduct.getSku(), null, 0);
        } else {
            for (Variant variant : existingProduct.getVariants()) {
                inventoryPublisher.publishInventoryEvent(EventEnum.DELETE_INVENTORY, existingProduct.getSku(),
                        variant.getSku(), 0);
            }
        }
    }

    public VariantDto addVariant(VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ExceptionConstant.PRODUCT_NOT_FOUND));
        Variant variant = productMapper.variantToEntity(request, product);
        Variant savedVariant = variantRepository.save(variant);
        inventoryPublisher.publishInventoryEvent(EventEnum.CREATE_INVENTORY, product.getSku(),
                variant.getSku(),
                variant.getStockQuantity());
        return productMapper.variantToDto(savedVariant);
    }

    public VariantDto updateVariant(Long variantId, VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ExceptionConstant.PRODUCT_NOT_FOUND));
        Variant existingVariant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ExceptionConstant.VARIANT_NOT_FOUND));
        productMapper.variantUpdatedToEntity(existingVariant, request, product);

        Variant savedVariant = variantRepository.save(existingVariant);
        inventoryPublisher.publishInventoryEvent(EventEnum.UPDATE_INVENTORY, product.getSku(),
                savedVariant.getSku(),
                savedVariant.getStockQuantity());
        return productMapper.variantToDto(savedVariant);
    }

    public void deleteVariant(Long variantId) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ExceptionConstant.VARIANT_NOT_FOUND));
        variantRepository.delete(variant);
        inventoryPublisher.publishInventoryEvent(EventEnum.DELETE_INVENTORY, variant.getProduct().getSku(),
                variant.getSku(), 0);
    }

}