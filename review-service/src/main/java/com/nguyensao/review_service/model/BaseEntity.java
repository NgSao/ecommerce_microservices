package com.nguyensao.review_service.model;

import java.time.Instant;

import com.nguyensao.review_service.utils.JwtUtil;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    protected Instant createdAt;

    protected Instant updatedAt;

    protected String createdBy;

    protected String updatedBy;

    protected Boolean status = true;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        createdAt = Instant.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        updatedAt = Instant.now();
    }

}
