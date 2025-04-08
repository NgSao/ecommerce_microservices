package com.nguyensao.attribute_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyensao.attribute_service.dto.OptionCombinationDto;
import com.nguyensao.attribute_service.dto.OptionDto;
import com.nguyensao.attribute_service.dto.OptionValueDto;
import com.nguyensao.attribute_service.dto.ProductDto;
import com.nguyensao.attribute_service.exception.AppException;
import com.nguyensao.attribute_service.feignclient.ProductCatalogServiceClient;
import com.nguyensao.attribute_service.model.ProductOption;
import com.nguyensao.attribute_service.model.ProductOptionCombination;
import com.nguyensao.attribute_service.model.ProductOptionValue;
import com.nguyensao.attribute_service.repository.ProductOptionCombinationRepository;
import com.nguyensao.attribute_service.repository.ProductOptionRepository;
import com.nguyensao.attribute_service.repository.ProductOptionValueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOptionService {

    private final ProductOptionRepository optionRepository;
    private final ProductOptionValueRepository optionValueRepository;
    private final ProductOptionCombinationRepository combinationRepository;
    private final ProductCatalogServiceClient catalogServiceClient;

    public ProductOptionService(
            ProductOptionRepository optionRepository,
            ProductOptionValueRepository optionValueRepository,
            ProductOptionCombinationRepository combinationRepository,
            ProductCatalogServiceClient catalogServiceClient) {
        this.optionRepository = optionRepository;
        this.optionValueRepository = optionValueRepository;
        this.combinationRepository = combinationRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    public List<ProductOption> getAllOptions() {
        return optionRepository.findAll();
    }

    public ProductOption getOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new AppException("Option not found with id: " + id));
    }

    @Transactional
    public ProductOption createOption(ProductOption option) {
        return optionRepository.save(option);
    }

    @Transactional
    public ProductOption updateOption(Long id, ProductOption option) {
        ProductOption existingOption = getOptionById(id);

        existingOption.setName(option.getName());
        existingOption.setDisplayOrder(option.getDisplayOrder());

        return optionRepository.save(existingOption);
    }

    @Transactional
    public void deleteOption(Long id) {
        ProductOption option = getOptionById(id);
        optionRepository.delete(option);
    }

    public List<ProductOptionValue> getOptionValuesByOptionId(Long optionId) {
        return optionValueRepository.findByOptionId(optionId);
    }

    @Transactional
    public ProductOptionValue createOptionValue(Long optionId, ProductOptionValue optionValue) {
        ProductOption option = getOptionById(optionId);
        optionValue.setOption(option);
        return optionValueRepository.save(optionValue);
    }

    @Transactional
    public ProductOptionValue updateOptionValue(Long id, ProductOptionValue optionValue) {
        ProductOptionValue existingValue = optionValueRepository.findById(id)
                .orElseThrow(() -> new AppException("Option value not found with id: " + id));

        existingValue.setValue(optionValue.getValue());
        existingValue.setDisplayType(optionValue.getDisplayType());
        existingValue.setDisplayOrder(optionValue.getDisplayOrder());

        return optionValueRepository.save(existingValue);
    }

    @Transactional
    public void deleteOptionValue(Long id) {
        optionValueRepository.deleteById(id);
    }

    public List<ProductOptionCombination> getOptionCombinationsByProductId(Long productId) {
        // Kiểm tra xem product có tồn tại không
        ProductDto product = catalogServiceClient.getProductById(productId);
        if (product == null) {
            throw new AppException("Product not found with id: " + productId);
        }

        return combinationRepository.findByProductId(productId);
    }

    public List<OptionCombinationDto> getOptionCombinationDtosByProductId(Long productId) {
        List<ProductOptionCombination> combinations = getOptionCombinationsByProductId(productId);
        return combinations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductOptionCombination> addOptionCombinationsToProduct(Long productId,
            List<OptionCombinationDto> combinations) {
        // Kiểm tra xem product có tồn tại không
        ProductDto product = catalogServiceClient.getProductById(productId);
        if (product == null) {
            throw new AppException("Product not found with id: " + productId);
        }

        List<ProductOptionCombination> optionCombinations = combinations.stream()
                .map(dto -> {
                    ProductOption option = optionRepository.findById(dto.getOptionId())
                            .orElseThrow(() -> new AppException("Option not found with id: " + dto.getOptionId()));

                    ProductOptionValue optionValue = optionValueRepository.findById(dto.getOptionValueId())
                            .orElseThrow(() -> new AppException(
                                    "Option value not found with id: " + dto.getOptionValueId()));

                    ProductOptionCombination combination = new ProductOptionCombination();
                    combination.setProductId(productId);
                    combination.setOption(option);
                    combination.setOptionValue(optionValue);

                    return combination;
                })
                .collect(Collectors.toList());

        return combinationRepository.saveAll(optionCombinations);
    }

    @Transactional
    public void deleteOptionCombinationsByProductId(Long productId) {
        combinationRepository.deleteByProductId(productId);
    }

    public List<OptionDto> getAllOptionDtos() {
        return optionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OptionDto getOptionDtoById(Long id) {
        ProductOption option = getOptionById(id);
        return convertToDto(option);
    }

    private OptionDto convertToDto(ProductOption option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setName(option.getName());
        dto.setDisplayOrder(option.getDisplayOrder());

        dto.setValues(option.getValues().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private OptionValueDto convertToDto(ProductOptionValue value) {
        OptionValueDto dto = new OptionValueDto();
        dto.setId(value.getId());
        dto.setValue(value.getValue());
        dto.setDisplayType(value.getDisplayType());
        dto.setDisplayOrder(value.getDisplayOrder());
        return dto;
    }

    private OptionCombinationDto convertToDto(ProductOptionCombination combination) {
        OptionCombinationDto dto = new OptionCombinationDto();
        dto.setId(combination.getId());
        dto.setOptionId(combination.getOption().getId());
        dto.setOptionName(combination.getOption().getName());
        dto.setOptionValueId(combination.getOptionValue().getId());
        dto.setOptionValue(combination.getOptionValue().getValue());
        return dto;
    }
}