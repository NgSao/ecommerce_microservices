package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    @Schema(description = "Mật khẩu hiện tại của tài khoản", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Schema(description = "Mật khẩu mới", example = "newPassword456")
    private String newPassword;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    @Schema(description = "Xác nhận lại mật khẩu mới", example = "newPassword456")
    private String confirmNewPassword;
}