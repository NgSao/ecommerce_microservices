package com.nguyensao.attribute_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_option_values")
@Data
public class ProductOptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    @JsonBackReference
    private ProductOption option;

    @Column(nullable = false)
    private String value;

    private String displayType;

    private Integer displayOrder;

    @OneToMany(mappedBy = "optionValue")
    @JsonManagedReference
    private List<ProductOptionCombination> combinations = new ArrayList<>();
}