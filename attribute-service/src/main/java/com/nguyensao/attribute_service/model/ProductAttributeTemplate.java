package com.nguyensao.attribute_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_attribute_templates")
@Data
public class ProductAttributeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "product_attribute_template_relation", joinColumns = @JoinColumn(name = "template_id"), inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private Set<ProductAttribute> attributes = new HashSet<>();
}