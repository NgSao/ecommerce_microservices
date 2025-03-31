package com.nguyensao.product_service.service;

import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.mapper.ProductMapper;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.repository.CategoryRepository;
import com.nguyensao.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * Lấy danh sách tất cả sản phẩm
     */
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toProductDto).collect(Collectors.toList());
    }

    /**
     * Lấy thông tin sản phẩm theo ID
     */
    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        return productMapper.toProductDto(product);
    }

    /**
     * Thêm mới sản phẩm
     */
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        Product product = productMapper.toProductEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);

        return productMapper.toProductDto(product);
    }

    /**
     * Cập nhật sản phẩm
     */
    @Transactional
    public ProductDto updateProduct(String id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setStock(productDto.getStock());
        existingProduct.setActive(productDto.getActive());

        productRepository.save(existingProduct);
        return productMapper.toProductDto(existingProduct);
    }

    /**
     * Xóa sản phẩm
     */
    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        productRepository.deleteById(id);
    }

    /**
     * Kiểm tra sản phẩm còn hàng không
     */
    public boolean isProductInStock(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        return product.getStock();
    }
}
