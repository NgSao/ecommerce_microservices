package com.nguyensao.product_service.service;

import org.springframework.stereotype.Service;

import com.nguyensao.product_service.constant.ExceptionConstant;
import com.nguyensao.product_service.dto.BrandDto;
import com.nguyensao.product_service.dto.request.BrandRequest;
import com.nguyensao.product_service.exception.AppException;
import com.nguyensao.product_service.mapper.BrandMapper;
import com.nguyensao.product_service.model.Brand;
import com.nguyensao.product_service.repository.BrandRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper mapper;

    public BrandService(BrandRepository brandRepository, BrandMapper mapper) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
    }

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(mapper::brandToDto)
                .collect(Collectors.toList());
    }

    public BrandDto getBrand(Long id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.BRAND_NOT_FOUND));
        return mapper.brandToDto(brand);
    }

    public BrandDto createBrand(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ExceptionConstant.BRAND_EXISTS);
        }
        Brand brand = mapper.brandToEntity(request);
        brandRepository.save(brand);
        return mapper.brandToDto(brand);
    }

    public BrandDto updateBrand(Long id, BrandRequest request) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.BRAND_NOT_FOUND));
        if (request.getName() != null && brandRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException(ExceptionConstant.BRAND_EXISTS);
        }
        mapper.brandUpdatedToEntity(existing, request);
        Brand brand = brandRepository.save(existing);
        return mapper.brandToDto(brand);
    }

    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionConstant.BRAND_NOT_FOUND));
        brandRepository.deleteById(brand.getId());
    }
}
