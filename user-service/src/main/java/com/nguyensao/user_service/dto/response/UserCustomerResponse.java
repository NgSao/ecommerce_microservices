package com.nguyensao.user_service.dto.response;

import java.time.Instant;

import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCustomerResponse {
    String fullName;
    String phone;
    String email;
    String profileImageUrl;
    RoleAuthorities role;
    UserStatus status;
    Instant birthday;
    Instant lastLoginDate;
    Instant createdAt;
}
