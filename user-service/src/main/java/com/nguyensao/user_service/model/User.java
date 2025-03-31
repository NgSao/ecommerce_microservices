package com.nguyensao.user_service.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserGender;
import com.nguyensao.user_service.enums.UserStatus;
import com.nguyensao.user_service.utils.JwtUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String fullname;
    String password;
    String email;
    Instant birthday;
    @Enumerated(EnumType.STRING)
    UserGender gender;
    String profileImageUrl;
    Instant lastLoginDate;

    @Enumerated(EnumType.STRING)
    RoleAuthorities role;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    Instant createdAt;

    String createdBy;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";

        createdAt = Instant.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Address> addresses = new HashSet<>();

}
