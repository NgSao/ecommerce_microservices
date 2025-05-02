package com.nguyensao.user_service.dto;

import java.time.Instant;

import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserGender;
import com.nguyensao.user_service.enums.UserStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    String id;
    String fullName;
    String password;
    String email;
    String phone;
    Instant birthday;
    UserGender gender;
    String profileImageUrl;
    String refreshToken;
    Instant lastLoginDate;
    RoleAuthorities role;
    UserStatus status;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
}