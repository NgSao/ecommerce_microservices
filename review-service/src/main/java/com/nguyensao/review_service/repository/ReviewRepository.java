package com.nguyensao.review_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.review_service.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStatusTrue();

    List<Review> findByProductIdAndStatusTrue(Long productId);

    List<Review> findByUserIdAndStatusTrue(Long userId);

    List<Review> findByProductId(Long productId);

    List<Review> findByUserId(Long userId);
}
