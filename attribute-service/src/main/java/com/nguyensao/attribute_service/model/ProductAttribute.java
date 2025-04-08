package com.nguyensao.attribute_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_attributes")
@Data
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private ProductAttributeGroup group;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductAttributeValue> values = new ArrayList<>();

    @ManyToMany(mappedBy = "attributes")
    @JsonBackReference
    private Set<ProductAttributeTemplate> templates = new HashSet<>();
}