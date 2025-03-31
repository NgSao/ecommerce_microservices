package com.nguyensao.user_service.dto.request;

import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserStatus;

import jakarta.validation.constraints.NotBlank;
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
public class UserRegisterRequest {
    @NotBlank(message = "Fullname is required")
    String fullname;

    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Password is required")
    String password;

    RoleAuthorities role;
    UserStatus status;
}
