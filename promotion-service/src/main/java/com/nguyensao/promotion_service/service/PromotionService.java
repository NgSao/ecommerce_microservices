package com.nguyensao.promotion_service.service;

import com.nguyensao.promotion_service.client.ProductClient;
import com.nguyensao.promotion_service.dto.ProductDto;
import com.nguyensao.promotion_service.dto.VariantDto;
import com.nguyensao.promotion_service.dto.request.PromotionItemRequest;
import com.nguyensao.promotion_service.dto.request.PromotionRequest;
import com.nguyensao.promotion_service.enums.DiscountType;
import com.nguyensao.promotion_service.exception.AppException;
import com.nguyensao.promotion_service.model.Promotion;
import com.nguyensao.promotion_service.model.PromotionItem;
import com.nguyensao.promotion_service.repository.PromotionRepository;
import com.nguyensao.promotion_service.repository.PromotionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionItemRepository promotionItemRepository;
    private final ProductClient productClient;

    public Promotion createPromotion(PromotionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setType(request.getType());
        promotion.setActive(request.isActive());

        promotionRepository.save(promotion);

        Set<PromotionItem> promotionItems = request.getItems().stream()
                .map(itemRequest -> createPromotionItem(promotion, itemRequest))
                .collect(Collectors.toSet());
        promotion.setPromotionItems(promotionItems);

        promotionItemRepository.saveAll(promotionItems);

        return promotion;
    }

    private PromotionItem createPromotionItem(Promotion promotion, PromotionItemRequest itemRequest) {
        ProductDto product = productClient.getProductById(itemRequest.getProductId());
        if (product == null) {
            throw new AppException("Không tìm thấy sản phẩm: " + itemRequest.getProductId());
        }

        VariantDto selectedVariant = null;
        if (itemRequest.getVariantId() != null) {
            selectedVariant = product.getVariants().stream()
                    .filter(variant -> variant.getId().equals(itemRequest.getVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException("Không tìm thấy biến thể: " + itemRequest.getVariantId()));
        }

        int availableStock = selectedVariant != null ? selectedVariant.getStockQuantity() : product.getStock();
        if (itemRequest.getQuantity() > availableStock) {
            throw new AppException(
                    "Số lượng yêu cầu (" + itemRequest.getQuantity() + ") vượt quá tồn kho: " + availableStock);
        }

        BigDecimal referencePrice = selectedVariant != null ? selectedVariant.getSalePrice() : product.getSalePrice();
        if (referencePrice == null) {
            referencePrice = selectedVariant != null ? selectedVariant.getOriginalPrice() : product.getOriginalPrice();
        }

        if (itemRequest.getDiscountType() != null && itemRequest.getValue() != null) {
            if (DiscountType.PERCENTAGE.equals(itemRequest.getDiscountType())) {
                if (itemRequest.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new AppException("Giá trị giảm giá phần trăm không được vượt quá 100%");
                }
            } else if (DiscountType.AMOUNT.equals(itemRequest.getDiscountType())) {
                if (itemRequest.getValue().compareTo(referencePrice) > 0) {
                    throw new AppException("Giá trị giảm giá cố định (" + itemRequest.getValue()
                            + ") không được vượt quá giá sản phẩm: " + referencePrice);
                }
            } else {
                throw new AppException("Loại giảm giá không hợp lệ: " + itemRequest.getDiscountType());
            }
        }

        PromotionItem item = new PromotionItem();
        item.setPromotion(promotion);
        item.setProductId(itemRequest.getProductId());
        item.setVariantId(itemRequest.getVariantId());
        item.setType(itemRequest.getDiscountType());
        item.setValue(itemRequest.getValue());
        item.setQuantity(itemRequest.getQuantity());
        item.setActive(itemRequest.isActive());

        return item;
    }

    public Promotion getPromotion(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy khuyến mãi: " + id));
    }

    public boolean isPromotionValid(Long promotionId) {
        Promotion promotion = getPromotion(promotionId);
        Instant now = Instant.now();
        return promotion.isActive() &&
                now.isAfter(promotion.getStartDate()) &&
                now.isBefore(promotion.getEndDate());
    }
}