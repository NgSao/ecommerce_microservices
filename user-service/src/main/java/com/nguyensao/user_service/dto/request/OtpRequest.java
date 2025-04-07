package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    @NotBlank(message = "Mã xác thực không được bỏ trống")
    @Schema(description = "Mã OTP xác thực tài khoản", example = "123456")
    private String code;
}
