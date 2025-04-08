package com.nguyensao.product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.product_service.dto.BrandDto;
import com.nguyensao.product_service.model.Brand;
import com.nguyensao.product_service.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<BrandDto>> getAllBrandDtos() {
        return ResponseEntity.ok(brandService.getAllBrandDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        return brandService.getBrandById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<BrandDto> getBrandDtoById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandDtoById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Brand> getBrandBySlug(@PathVariable String slug) {
        return brandService.getBrandBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        return new ResponseEntity<>(brandService.saveBrand(brand), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody Brand brand) {
        return brandService.getBrandById(id)
                .map(existingBrand -> {
                    brand.setId(id);
                    return ResponseEntity.ok(brandService.saveBrand(brand));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        return brandService.getBrandById(id)
                .map(brand -> {
                    brandService.deleteBrand(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}