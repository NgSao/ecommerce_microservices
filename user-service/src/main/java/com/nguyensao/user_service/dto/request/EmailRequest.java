package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Địa chỉ email của người dùng", example = "nguyensaobe.java@gmail.com")
    private String email;

}