package com.nguyensao.product_service.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.product_service.dto.MediaDto;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.dto.VariantDto;
import com.nguyensao.product_service.dto.request.ProductImageRequest;
import com.nguyensao.product_service.dto.request.ProductRequest;
import com.nguyensao.product_service.dto.request.VariantRequest;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.model.Brand;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.model.Media;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.model.Variant;
import com.nguyensao.product_service.repository.BrandRepository;
import com.nguyensao.product_service.repository.CategoryRepository;
import com.nguyensao.product_service.utils.SkuUtil;
import com.nguyensao.product_service.utils.SlugUtil;

@Component
public class ProductMapper {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    public ProductMapper(BrandRepository brandRepository, CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
    }

    public ProductDto productToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setSpecification(product.getSpecification());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setBrand(brandMapper.brandToDto(product.getBrand()));
        dto.setCategories(product.getCategories().stream()
                .map(category -> categoryMapper.categoryToDto(category))
                .collect(Collectors.toSet()));
        dto.setImages(product.getImages().stream()
                .map(this::produtImageToDto)
                .collect(Collectors.toSet()));
        dto.setVariants(product.getVariants().stream()
                .map(varinat -> variantToDto(varinat))
                .collect(Collectors.toSet()));
        return dto;
    }

    public VariantDto variantToDto(Variant variant) {
        VariantDto dto = new VariantDto();
        dto.setId(variant.getId());
        dto.setSlug(variant.getSlug());
        dto.setSku(variant.getSku());
        dto.setColor(variant.getColor());
        dto.setSize(variant.getSize());
        dto.setOriginalPrice(variant.getOriginalPrice());
        dto.setSalePrice(variant.getSalePrice());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setDisplayOrder(variant.getDisplayOrder());
        return dto;
    }

    private MediaDto produtImageToDto(Media image) {
        MediaDto dto = new MediaDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setIsPublished(dto.getIsPublished());
        return dto;
    }

    public Product productToEntity(ProductRequest request) {
        request.validatePrices();
        if (request.getStock() != null && request.getStock() <= 0) {
            throw new AppException("Số lượng tồn kho phải lớn hơn 0");
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(SkuUtil.generateSku(request.getName()));
        product.setSlug(SlugUtil.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setSpecification(request.getSpecification());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSalePrice(request.getSalePrice());
        if (request.getBrandId() != null) {
            product.setBrand(brandToId(request.getBrandId()));
        }

        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            product.setCategories(categoryToId(request.getCategoryId()));
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImages(productImageToEntity(request.getImages(), product));
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            product.setVariants(request.getVariants().stream()
                    .map(variantRequest -> variantToEntity(variantRequest, product))
                    .collect(Collectors.toSet()));
        }
        return product;
    }

    public Product productUpdatedToEntity(Product product, ProductRequest request) {
        if (request.getOriginalPrice() == null || request.getSalePrice() == null) {
            request.validatePrices();
        }

        if (request.getStock() != null && request.getStock() <= 0) {
            throw new AppException("Số lượng tồn kho phải lớn hơn 0");
        }
        if (request.getName() != null) {
            product.setName(request.getName());
            product.setSku(SkuUtil.generateSku(request.getName()));
            product.setSlug(SlugUtil.toSlug(request.getName()));
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getSpecification() != null) {
            product.setSpecification(request.getSpecification());
        }

        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }

        if (request.getSalePrice() != null) {
            product.setSalePrice(request.getSalePrice());
        }

        if (request.getBrandId() != null) {
            product.setBrand(brandToId(request.getBrandId()));
        }
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            product.setCategories(categoryToId(request.getCategoryId()));
        }
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImages(productImageToEntity(request.getImages(), product));
        }
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            product.setVariants(request.getVariants().stream()
                    .map(variantRequest -> variantToEntity(variantRequest, product))
                    .collect(Collectors.toSet()));
        }

        return product;
    }

    public Brand brandToId(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(() -> new AppException("Brand not found"));
    }

    public Set<Category> categoryToId(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(catId -> categoryRepository.findById(catId)
                        .orElseThrow(() -> new AppException("Category not found")))
                .collect(Collectors.toSet());
    }

    public Set<Media> productImageToEntity(Set<ProductImageRequest> imageRequests, Product product) {
        return imageRequests.stream()
                .map(imageReq -> createMedia(imageReq, product))
                .collect(Collectors.toSet());
    }

    private Media createMedia(ProductImageRequest imageReq, Product product) {
        Media image = new Media();
        image.setImageUrl(imageReq.getImageUrl());
        image.setProduct(product);
        return image;
    }

    public Variant variantToEntity(VariantRequest request, Product product) {
        request.validatePrices();
        if (request.getStock() != null && request.getStock() <= 0) {
            throw new AppException("Số lượng tồn kho phải lớn hơn 0");
        }
        Variant variant = new Variant();
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        String variantIdentifier = request.getColor();
        if (request.getSize() != null && !request.getSize().isEmpty()) {
            variantIdentifier += "-" + request.getSize();
        }
        variant.setSlug(SlugUtil.toSlug(variantIdentifier));
        variant.setSku(SkuUtil.generateSku(variantIdentifier));
        variant.setOriginalPrice(request.getOriginalPrice());
        variant.setSalePrice(request.getSalePrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setDisplayOrder(request.getDisplayOrder());
        variant.setProduct(product);
        return variant;
    }

    public Variant variantUpdatedToEntity(Variant variant, VariantRequest request, Product product) {
        if (request.getOriginalPrice() == null || request.getSalePrice() == null) {
            request.validatePrices();
        }
        if (request.getColor() != null && !request.getColor().isEmpty()) {
            variant.setColor(request.getColor());
        }

        if (request.getSize() != null && !request.getSize().isEmpty()) {
            variant.setSize(request.getSize());
        }

        String color = request.getColor() != null && !request.getColor().isEmpty() ? request.getColor()
                : variant.getColor();
        String size = request.getSize() != null && !request.getSize().isEmpty() ? request.getSize() : variant.getSize();

        String variantIdentifier = color;
        if (size != null && !size.isEmpty()) {
            variantIdentifier += "-" + size;
        }

        variant.setSlug(SlugUtil.toSlug(variantIdentifier));
        variant.setSku(SkuUtil.generateSku(variantIdentifier));

        if (request.getOriginalPrice() != null) {
            variant.setOriginalPrice(request.getOriginalPrice());
        }

        if (request.getSalePrice() != null) {
            variant.setSalePrice(request.getSalePrice());
        }

        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }

        if (request.getDisplayOrder() != null) {
            variant.setDisplayOrder(request.getDisplayOrder());
        }

        if (product != null) {
            variant.setProduct(product);
        }

        return variant;
    }

}
