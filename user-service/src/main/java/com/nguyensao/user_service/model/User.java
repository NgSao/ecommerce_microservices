package com.nguyensao.user_service.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.nguyensao.user_service.enums.Provider;
import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserGender;
import com.nguyensao.user_service.enums.UserStatus;
import com.nguyensao.user_service.utils.JwtUtil;

import jakarta.persistence.*;
import lombok.*;
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

    String fullName;

    String email;

    String password;

    String phone;

    Instant birthday;

    String profileImageUrl;

    Instant lastLoginDate;

    @Enumerated(EnumType.STRING)
    UserGender gender;

    @Enumerated(EnumType.STRING)
    Provider provider;

    @Enumerated(EnumType.STRING)
    RoleAuthorities role;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    Instant createdAt;

    String createdBy;

    Instant updatedAt;

    String updatedBy;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<UserProvider> providers = new HashSet<>();

}
