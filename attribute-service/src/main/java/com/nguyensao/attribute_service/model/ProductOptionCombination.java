package com.nguyensao.attribute_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_option_combinations")
@Data
public class ProductOptionCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    @JsonBackReference
    private ProductOption option;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "option_value_id", nullable = false)
    private ProductOptionValue optionValue;
}