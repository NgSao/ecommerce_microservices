package com.nguyensao.product_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.product_service.dto.BrandDto;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.model.Brand;
import com.nguyensao.product_service.repository.BrandRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    public Optional<Brand> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }

    @Transactional
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    public List<BrandDto> getAllBrandDtos() {
        return brandRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BrandDto getBrandDtoById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException("Brand not found with id: " + id));
        return convertToDto(brand);
    }

    private BrandDto convertToDto(Brand brand) {
        BrandDto dto = new BrandDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setDescription(brand.getDescription());
        dto.setLogoUrl(brand.getLogoUrl());
        return dto;
    }
}