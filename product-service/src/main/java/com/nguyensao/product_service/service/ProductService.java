package com.nguyensao.product_service.service;

import com.nguyensao.product_service.constant.GithubConstant;
import com.nguyensao.product_service.dto.ProductDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.mapper.ProductMapper;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.model.Product;
import com.nguyensao.product_service.repository.CategoryRepository;
import com.nguyensao.product_service.repository.ProductRepository;
import com.nguyensao.product_service.utils.FileValidation;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;

    }

    /**
     * Thêm mới sản phẩm
     * 
     */
    @Transactional
    public ProductDto createProduct(MultipartFile file, ProductDto productDto) throws IOException {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
        GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String imagePath = "microservice/products/" + timestamp + "_" + file.getOriginalFilename();
        if (!FileValidation.isValidImage(file)) {
            throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
        }
        repository.createContent()
                .content(file.getBytes())
                .path(imagePath)
                .message("Tải ảnh sản phẩm: " + file.getOriginalFilename())
                .branch(GithubConstant.BRANCH)
                .commit();
        String imageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/" + GithubConstant.BRANCH
                + "/" + imagePath;
        Product product = productMapper.toProductEntity(productDto);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toProductDto(product);
    }

    /**
     * Cập nhật sản phẩm
     * 
     * @throws IOException
     */
    @Transactional
    public ProductDto updateProduct(MultipartFile file, ProductDto productDto) throws IOException {
        Product existingProduct = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        productMapper.toProductEntity(productDto);
        if (file != null && !file.isEmpty()) {
            if (!FileValidation.isValidImage(file)) {
                throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
            }
            GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
            GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);

            String oldImageUrl = existingProduct.getImageUrl();
            if (oldImageUrl != null && oldImageUrl.contains(GithubConstant.REPO_NAME)) {
                String oldImagePath = oldImageUrl.substring(oldImageUrl.indexOf("microservice"));
                GHContent content = repository.getFileContent(oldImagePath, GithubConstant.BRANCH);
                content.delete("Xóa ảnh cũ khi cập nhật danh mục");
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String imagePath = "microservice/products/" + timestamp + "_" + file.getOriginalFilename();

            repository.createContent()
                    .content(file.getBytes())
                    .path(imagePath)
                    .message("Cập nhật ảnh sản phẩm: " + file.getOriginalFilename())
                    .branch(GithubConstant.BRANCH)
                    .commit();

            String newImageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/"
                    + GithubConstant.BRANCH
                    + "/" + imagePath;
            existingProduct.setImageUrl(newImageUrl);
        }

        productRepository.save(existingProduct);
        return productMapper.toProductDto(existingProduct);
    }

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
