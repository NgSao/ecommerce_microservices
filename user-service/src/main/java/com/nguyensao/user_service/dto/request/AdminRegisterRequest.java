package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class AdminRegisterRequest {
    @NotBlank(message = "Họ và tên không được bỏ trống")
    @Schema(description = "Họ và tên của người dùng", example = "Nguyễn Văn A")
    String fullName;

    String email;

    String phone;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Schema(description = "Mật khẩu của tài khoản", example = "Password@123")
    String password;
}
