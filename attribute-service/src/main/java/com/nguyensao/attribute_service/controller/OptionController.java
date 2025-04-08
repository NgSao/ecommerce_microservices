package com.nguyensao.attribute_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.attribute_service.dto.OptionCombinationDto;
import com.nguyensao.attribute_service.dto.OptionDto;
import com.nguyensao.attribute_service.model.ProductOption;
import com.nguyensao.attribute_service.model.ProductOptionCombination;
import com.nguyensao.attribute_service.model.ProductOptionValue;
import com.nguyensao.attribute_service.service.ProductOptionService;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final ProductOptionService optionService;

    public OptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<ProductOption>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @GetMapping("/dto")
    public ResponseEntity<List<OptionDto>> getAllOptionDtos() {
        return ResponseEntity.ok(optionService.getAllOptionDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOption> getOptionById(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.getOptionById(id));
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<OptionDto> getOptionDtoById(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.getOptionDtoById(id));
    }

    @PostMapping
    public ResponseEntity<ProductOption> createOption(@RequestBody ProductOption option) {
        return new ResponseEntity<>(optionService.createOption(option), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductOption> updateOption(
            @PathVariable Long id,
            @RequestBody ProductOption option) {
        return ResponseEntity.ok(optionService.updateOption(id, option));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{optionId}/values")
    public ResponseEntity<List<ProductOptionValue>> getOptionValuesByOptionId(@PathVariable Long optionId) {
        return ResponseEntity.ok(optionService.getOptionValuesByOptionId(optionId));
    }

    @PostMapping("/{optionId}/values")
    public ResponseEntity<ProductOptionValue> createOptionValue(
            @PathVariable Long optionId,
            @RequestBody ProductOptionValue optionValue) {
        return new ResponseEntity<>(
                optionService.createOptionValue(optionId, optionValue),
                HttpStatus.CREATED);
    }

    @PutMapping("/values/{id}")
    public ResponseEntity<ProductOptionValue> updateOptionValue(
            @PathVariable Long id,
            @RequestBody ProductOptionValue optionValue) {
        return ResponseEntity.ok(optionService.updateOptionValue(id, optionValue));
    }

    @DeleteMapping("/values/{id}")
    public ResponseEntity<Void> deleteOptionValue(@PathVariable Long id) {
        optionService.deleteOptionValue(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/{productId}/combinations")
    public ResponseEntity<List<OptionCombinationDto>> getOptionCombinationsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(optionService.getOptionCombinationDtosByProductId(productId));
    }

    @PostMapping("/products/{productId}/combinations")
    public ResponseEntity<List<ProductOptionCombination>> addOptionCombinationsToProduct(
            @PathVariable Long productId,
            @RequestBody List<OptionCombinationDto> combinations) {
        return new ResponseEntity<>(
                optionService.addOptionCombinationsToProduct(productId, combinations),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{productId}/combinations")
    public ResponseEntity<Void> deleteOptionCombinationsByProductId(@PathVariable Long productId) {
        optionService.deleteOptionCombinationsByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}