package com.nguyensao.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class AddressCreateRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Schema(description = "Họ tên người nhận", example = "Nguyễn Văn A")
    String fullname;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ")
    @Schema(description = "Số điện thoại người nhận", example = "0912345678")
    String phone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Schema(description = "Tỉnh/Thành phố", example = "Hồ Chí Minh")
    String city;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Schema(description = "Quận/Huyện", example = "Quận 1")
    String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Schema(description = "Phường/Xã", example = "Phường Bến Nghé")
    String street;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Schema(description = "Địa chỉ chi tiết", example = "Số 1, ngõ 2, đường ABC")
    String addressDetail;

    @Schema(description = "Địa chỉ đang được sử dụng mặc định", example = "true")
    Boolean active;
}
