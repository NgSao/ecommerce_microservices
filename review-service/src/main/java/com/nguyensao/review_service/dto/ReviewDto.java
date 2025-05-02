package com.nguyensao.review_service.dto;

import com.nguyensao.review_service.enums.ReviewType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long productId;
    private ReviewType type;
    private String imageUrl;
    private Integer rating;
    private String comment;
    private Boolean status;
    private Long parentId;
    private Set<ReviewDto> children = new HashSet<>();
}