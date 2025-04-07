package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
public class UserLoginRequest {
    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Địa chỉ email của người dùng", example = "nguyensaobe.java@gmail.com")
    String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Schema(description = "Mật khẩu của tài khoản", example = "Password@123")
    String password;

}