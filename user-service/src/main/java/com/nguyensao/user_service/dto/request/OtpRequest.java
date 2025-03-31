package com.nguyensao.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    @NotBlank(message = "Mã xác thực không được bỏ trống")
    private String code;
}
