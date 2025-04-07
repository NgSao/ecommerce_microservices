package com.nguyensao.user_service.dto.request;

import com.nguyensao.user_service.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Email người dùng cần thay đổi quyền", example = "user@example.com")
    private String email;

    @NotNull(message = "Trạng thái không được để trống")
    @Schema(description = "Trạng thái mới muốn gán cho người dùng", example = "ACTIVE")
    private UserStatus status;
}
