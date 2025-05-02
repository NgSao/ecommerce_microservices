package com.nguyensao.review_service.service;

import com.nguyensao.review_service.amqp.EventEnum;
import com.nguyensao.review_service.amqp.InventoryEvent;
import com.nguyensao.review_service.dto.ProductDto;
import com.nguyensao.review_service.dto.ReviewDto;
import com.nguyensao.review_service.enums.ReviewType;
import com.nguyensao.review_service.feignclient.ProductClient;
import com.nguyensao.review_service.model.Review;
import com.nguyensao.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    public ReviewDto createReview(Long userId, Long productId, Integer rating, String comment, String imageUrl) {
        // Validate product exists
        ProductDto product = productClient.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        // Create review
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment);
        review.setImageUrl(imageUrl);
        review.setType(ReviewType.REVIEW);
        review.setStatus(true); // Set status to active

        // Save review
        Review savedReview = reviewRepository.save(review);

        // Send rating update event to product service
        InventoryEvent event = new InventoryEvent();
        event.setEventType(EventEnum.RATING_UPDATE);
        event.setSkuProduct(product.getSku());
        event.setRating(rating);
        rabbitTemplate.convertAndSend("rating.exchange", "rating.update", event);

        return mapToDto(savedReview);
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getActiveReviews() {
        return reviewRepository.findByStatusTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Optional<ReviewDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::mapToDto);
    }

    public ReviewDto updateReview(Long id, Integer rating, String comment, String imageUrl) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (rating != null) {
            review.setRating(rating);
            // Send rating update event to product service
            ProductDto product = productClient.getProductById(review.getProductId());
            InventoryEvent event = new InventoryEvent();
            event.setEventType(EventEnum.RATING_UPDATE);
            event.setSkuProduct(product.getSku());
            event.setRating(rating);
            rabbitTemplate.convertAndSend("rating.exchange", "rating.update", event);
        }
        if (comment != null) {
            review.setComment(comment);
        }
        if (imageUrl != null) {
            review.setImageUrl(imageUrl);
        }

        Review updatedReview = reviewRepository.save(review);
        return mapToDto(updatedReview);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);
    }

    public void hideReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setStatus(false);
        reviewRepository.save(review);
    }

    private ReviewDto mapToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .type(review.getType())
                .imageUrl(review.getImageUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .parentId(review.getParent() != null ? review.getParent().getId() : null)
                .children(review.getChildren().stream().map(this::mapToDto).collect(Collectors.toSet()))
                .build();
    }
}