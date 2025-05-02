package com.nguyensao.review_service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import com.nguyensao.review_service.enums.ReviewType;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    Long productId;

    @Enumerated(EnumType.STRING)
    ReviewType type;

    String imageUrl;
    Integer rating;

    String comment;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    Review parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    Set<Review> children = new HashSet<>();

}
