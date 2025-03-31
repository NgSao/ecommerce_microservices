package com.nguyensao.product_service.service;

import com.nguyensao.product_service.constant.GithubConstant;
import com.nguyensao.product_service.dto.CategoryDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.mapper.CategoryMapper;
import com.nguyensao.product_service.model.Category;
import com.nguyensao.product_service.repository.CategoryRepository;
import com.nguyensao.product_service.utils.FileValidation;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Thêm mới danh mục
     */
    public CategoryDto createCategory(MultipartFile file, CategoryDto categoryDto) throws IOException {
        GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
        GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String imagePath = "microservice/categories/" + timestamp + "_" + file.getOriginalFilename();
        if (!FileValidation.isValidImage(file)) {
            throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
        }
        repository.createContent()
                .content(file.getBytes())
                .path(imagePath)
                .message("Tải ảnh danh mục: " + file.getOriginalFilename())
                .branch(GithubConstant.BRANCH)
                .commit();
        String imageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/" + GithubConstant.BRANCH
                + "/" + imagePath;
        Category category = categoryMapper.toCategoryEntity(categoryDto);
        category.setImageUrl(imageUrl);
        categoryRepository.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    /**
     * Cập nhật danh mục
     */
    public CategoryDto updateCategory(String id, MultipartFile file, String name) throws IOException {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Danh mục không tồn tại"));

        existingCategory.setName(name);
        if (file != null && !file.isEmpty()) {
            if (!FileValidation.isValidImage(file)) {
                throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
            }
            GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
            GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);

            String oldImageUrl = existingCategory.getImageUrl();
            if (oldImageUrl != null && oldImageUrl.contains(GithubConstant.REPO_NAME)) {
                String oldImagePath = oldImageUrl.substring(oldImageUrl.indexOf("microservice"));
                GHContent content = repository.getFileContent(oldImagePath, GithubConstant.BRANCH);
                content.delete("Xóa ảnh cũ khi cập nhật danh mục");
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String imagePath = "microservice/categories/" + timestamp + "_" + file.getOriginalFilename();

            repository.createContent()
                    .content(file.getBytes())
                    .path(imagePath)
                    .message("Cập nhật ảnh danh mục: " + file.getOriginalFilename())
                    .branch(GithubConstant.BRANCH)
                    .commit();

            String newImageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/"
                    + GithubConstant.BRANCH
                    + "/" + imagePath;
            existingCategory.setImageUrl(newImageUrl);
        }

        categoryRepository.save(existingCategory);
        return categoryMapper.toCategoryDto(existingCategory);
    }

    /**
     * Lấy danh sách tất cả danh mục
     */
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toCategoryDto);
    }

    /**
     * Lấy thông tin danh mục theo ID
     */
    public CategoryDto getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Danh mục không tồn tại"));
        return categoryMapper.toCategoryDto(category);
    }

    /**
     * Xóa danh mục (Kiểm tra nếu có sản phẩm thuộc danh mục)
     */
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Danh mục không tồn tại"));

        if (!category.getProducts().isEmpty()) {
            throw new AppException("Không thể xóa danh mục vì còn sản phẩm thuộc danh mục này");
        }

        categoryRepository.deleteById(id);
    }

    public void toggleCategoryStatus(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Danh mục không tồn tại"));
        category.setStatus(!category.getStatus());
        categoryRepository.save(category);
    }

}
