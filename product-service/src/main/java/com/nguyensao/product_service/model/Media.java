package com.nguyensao.product_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medias")
@Data
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String imageUrl;

    private int displayOrder;

    private Boolean isPublished;
}