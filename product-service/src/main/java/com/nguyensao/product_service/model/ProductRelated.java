package com.nguyensao.product_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_related")
@Data
public class ProductRelated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "related_product_id", nullable = false)
    private Product relatedProduct;

}
