package com.nguyensao.review_service.controller;

import com.nguyensao.review_service.dto.ReviewDto;
import com.nguyensao.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            @RequestParam(required = false) String imageUrl) {
        ReviewDto review = reviewService.createReview(userId, productId, rating, comment, imageUrl);
        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ReviewDto>> getActiveReviews() {
        return ResponseEntity.ok(reviewService.getActiveReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long id,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) String imageUrl) {
        ReviewDto updatedReview = reviewService.updateReview(id, rating, comment, imageUrl);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<Void> hideReview(@PathVariable Long id) {
        reviewService.hideReview(id);
        return ResponseEntity.noContent().build();
    }
}