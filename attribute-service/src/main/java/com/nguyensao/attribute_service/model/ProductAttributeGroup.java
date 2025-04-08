package com.nguyensao.attribute_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_attribute_groups")
@Data
public class ProductAttributeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer displayOrder;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductAttribute> attributes = new ArrayList<>();
}