package com.nguyensao.attribute_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_attribute_values")
@Data
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    @JsonBackReference
    private ProductAttribute attribute;

    @Column(nullable = false)
    private String value;
}