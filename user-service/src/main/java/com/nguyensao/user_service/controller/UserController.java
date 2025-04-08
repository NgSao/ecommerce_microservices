package com.nguyensao.user_service.controller;

import com.nguyensao.user_service.annotation.AppMessage;
import com.nguyensao.user_service.constant.SecurityConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.AddressCreateRequest;
import com.nguyensao.user_service.dto.request.AddressUpdateRequest;
import com.nguyensao.user_service.dto.request.EmailRequest;
import com.nguyensao.user_service.dto.request.OtpRequest;
import com.nguyensao.user_service.dto.request.ResetPasswordRequest;
import com.nguyensao.user_service.dto.request.RoleChangeRequest;
import com.nguyensao.user_service.dto.request.StatusChangeRequest;
import com.nguyensao.user_service.dto.request.UserLoginRequest;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.dto.response.UserCustomerResponse;
import com.nguyensao.user_service.dto.response.UserLoginResponse;
import com.nguyensao.user_service.enums.UserGender;
import com.nguyensao.user_service.exception.AppException;
import com.nguyensao.user_service.service.EmailService;
import com.nguyensao.user_service.service.TokenBlacklistService;
import com.nguyensao.user_service.service.UserService;
import com.nguyensao.user_service.utils.GenerateOTP;
import com.nguyensao.user_service.utils.GeneratePassword;
import com.nguyensao.user_service.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
        private final UserService userService;
        private final JwtUtil jwtUtil;
        private final EmailService emailService;
        private final TokenBlacklistService tokenBlacklistService;

        public UserController(UserService userService, JwtUtil jwtUtil, EmailService emailService,
                        TokenBlacklistService tokenBlacklistService) {
                this.userService = userService;
                this.jwtUtil = jwtUtil;
                this.emailService = emailService;
                this.tokenBlacklistService = tokenBlacklistService;
        }

        /**
         * 📌 1. API: Đăng ký tài khoản (chưa kích hoạt) +mã xác thực OTP qua email
         */
        @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới và gửi mã OTP xác thực qua email")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Đăng kí thành công, vui lòng kiểm tra email để kích hoạt tài khoản"),
                        @ApiResponse(responseCode = "400", description = "Thông tin đăng ký không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/public/register")
        @AppMessage("Đăng kí thành công, vui lòng kiểm tra email để kích hoạt tài khoản")
        public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegisterRequest userDto) {
                String verificationCode = GenerateOTP.generate();
                userService.registerUser(userDto, verificationCode);
                emailService.sendVerificationEmail(userDto.getEmail(), verificationCode);
                String token = jwtUtil.createOtpToken(userDto.getEmail(), verificationCode);
                ResponseCookie cookie = ResponseCookie
                                .from("_otp", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(SecurityConstant.EXPIRATION_OTP).build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body("Đăng kí tài khoản thành công.");
        }

        /**
         * 📌 2. API: Tài khoản đã có (chưa kích hoạt) + mã xác thực mới OTP qua email.
         */
        @Operation(summary = "Tài khoản đã có", description = "Chưa kích hoạt và gửi mã OTP xác thực qua email")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Vui lòng kiểm tra email để kích hoạt tài khoản"),
                        @ApiResponse(responseCode = "400", description = "Thông tin xác thực không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/public/verify")
        @AppMessage("Vui lòng kiểm tra mã xác thực trong gamil.")
        public ResponseEntity<String> verifyActivateUser(@Valid @RequestBody EmailRequest request,
                        @CookieValue(value = "_otp", defaultValue = "") String cookieCode) {
                userService.checkExistsEmail(request.getEmail());
                String verificationCode = GenerateOTP.generate();
                emailService.sendVerificationEmail(request.getEmail(), verificationCode);
                String token = jwtUtil.createOtpToken(request.getEmail(), verificationCode);
                ResponseCookie cookie = ResponseCookie
                                .from("_otp", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(SecurityConstant.EXPIRATION_OTP).build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body("Vui lòng kiểm tra mã xác thực trong gamil");
        }

        /**
         * 📌 3. API: Xác thực OTP để kích hoạt tài khoản
         */
        @Operation(summary = "Kích hoạt tài khoản", description = "Tài khoản đã đăng kí")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Kích hoạt tài khoản thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin xác thực không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/public/activate")
        @AppMessage("Kích hoạt tài khoản thành công.")
        public ResponseEntity<String> activateUser(@Valid @RequestBody OtpRequest request,
                        @CookieValue(value = "_otp", defaultValue = "") String cookieCode) {
                String codeToken = jwtUtil.decodedToken(cookieCode);
                String email = jwtUtil.decodedTokenClaimEmail(cookieCode);
                userService.verifyOTP(email, request.getCode(), codeToken);
                ResponseCookie cookie = ResponseCookie
                                .from("_otp", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body("Xác thực thành công! Tài khoản đã được kích hoạt.");
        }

        /**
         * 📌 4. API: Xác thực OTP để gửi mật khẩu mới về
         */
        @Operation(summary = "Xác thực OTP để gửi mật khẩu mới", description = "Tài khoản đã đăng kí")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Mật khẩu mới gửi về email"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/public/forgot-password")
        @AppMessage("Vui lòng kiểm tra email để nhận mật khẩu mới.")
        public ResponseEntity<String> verifyPassword(@Valid @RequestBody OtpRequest request,
                        @CookieValue(value = "_otp", defaultValue = "") String cookieCode) {
                String codeToken = jwtUtil.decodedToken(cookieCode);
                String email = jwtUtil.decodedTokenClaimEmail(cookieCode);
                String newPassword = GeneratePassword.generate();
                userService.forgotPassword(email, request.getCode(), codeToken, newPassword);
                emailService.sendVerificationPassword(email, newPassword);
                ResponseCookie cookie = ResponseCookie
                                .from("_otp", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body("Xác thực thành công! Vui lòng kiểm tra email để nhận mật khẩu mới.");
        }

        /**
         * 📌 5. API: Đăng nhập tài khoản
         */
        @Operation(summary = "Đăng nhập tài khoản", description = "Tài khoản đã đăng kí và kích hoạt")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/public/login")
        @AppMessage("Đăng nhập tài khoản thành công.")
        public ResponseEntity<UserLoginResponse> loginAuth(@Valid @RequestBody UserLoginRequest request) {
                UserLoginResponse authResponse = userService.loginUser(request);
                String refreshToken = userService.refreshToken(request.getEmail());
                ResponseCookie cookie = ResponseCookie
                                .from("_tk", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(SecurityConstant.REFRESH_TOKEN_EXP).build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(authResponse);
        }

        @GetMapping("/public/test")
        @AppMessage("Đăng nhập tài khoản thành công.")
        public void Test() {
                Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                System.out.println("Test" + jwt.getTokenValue());
                String email = jwt.getSubject();
                System.out.println("Test1" + email);
                String uuid = jwt.getClaimAsString("uuid");
                System.out.println("Test2" + uuid);

        }

        /**
         * 📌 6. API: Cập nhật lại tonken
         */
        @GetMapping("/admin/refresh")
        @AppMessage("Cập nhật thành công.")
        public ResponseEntity<UserLoginResponse> refreshToken(
                        @CookieValue(name = "_tk", required = false) String refreshToken) {
                if (refreshToken == null) {
                        throw new AppException("Không tìm thấy refresh token.");
                }

                try {
                        Jwt jwt = jwtUtil.checkValidRefreshToken(refreshToken);
                        String email = jwt.getSubject();

                        String newAccessToken = jwtUtil.createAccessToken(email);
                        String newRefreshToken = jwtUtil.createRefreshToken(email);

                        ResponseCookie cookie = ResponseCookie.from("_tk", newRefreshToken)
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .maxAge(SecurityConstant.REFRESH_TOKEN_EXP)
                                        .build();

                        UserLoginResponse response = new UserLoginResponse();
                        response.setAccessToken(newAccessToken);
                        response.setEmail(email);
                        return ResponseEntity.ok()
                                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                        .body(response);
                } catch (AppException e) {
                        throw new AppException("Refresh token không hợp lệ hoặc đã hết hạn.");
                }
        }

        /**
         * 📌 7. API: Đăng xuất
         */
        @Operation(summary = "Đăng xuất", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Đăng xuất thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @GetMapping("/public/logout")
        public ResponseEntity<?> logOutAuth(@CookieValue(value = "_tk", required = false) String refreshToken) {
                if (refreshToken == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Không tìm thấy refresh token để đăng xuất.");
                }

                // Xóa cookie bằng cách set maxAge = 0
                ResponseCookie cookie = ResponseCookie
                                .from("_tk", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body("Đăng xuất thành công.");
        }

        /**
         * 📌 8. API: Lấy người dùng bằng token uuid
         */
        @Operation(summary = "Lấy thông tin người dùng bằng token", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lấy người dùng thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @GetMapping("/customer/token")
        public ResponseEntity<UserCustomerResponse> getUserByToken() {
                return ResponseEntity.ok().body(userService.getUserByToken());
        }

        /**
         * 📌 9. API: Lấy thông tin người dùng theo email
         */
        @Operation(summary = "Lấy thông tin người dùng theo email", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lấy người dùng thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @GetMapping("/customer/email")
        public ResponseEntity<UserCustomerResponse> getUserByEmail(@RequestParam String email) {
                return ResponseEntity.ok().body(userService.getUserByEmail(email));
        }

        @Operation(summary = "Cập nhật thông tin tài khoản người dùng", description = "Cập nhật thông tin người dùng bao gồm tên, ngày sinh, giới tính và hình ảnh")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping(value = "/customer/updated", consumes = "multipart/form-data")
        public ResponseEntity<UserCustomerResponse> updateAccount(
                        @RequestPart(value = "file", required = false) @Schema(type = "string", format = "binary") MultipartFile file,
                        @RequestParam("fullname") @Schema(description = "Tên đầy đủ của người dùng") String fullname,
                        @RequestParam("birthday") @Schema(type = "string", format = "date", example = "2024-08-20") String birthdayString,
                        @RequestParam("gender") @Schema(description = "Giới tính của người dùng") UserGender gender) {

                try {
                        LocalDate birthday;
                        try {
                                birthday = LocalDate.parse(birthdayString, DateTimeFormatter.ISO_DATE);
                        } catch (DateTimeParseException e) {
                                return ResponseEntity.badRequest().body(null);
                        }

                        Instant birthdayInstant = birthday.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

                        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                                        .fullname(fullname)
                                        .birthday(birthdayInstant)
                                        .gender(gender)
                                        .build();

                        UserCustomerResponse updatedUser = userService.updateAccount(file, userUpdateRequest);
                        return ResponseEntity.ok().body(updatedUser);

                } catch (IOException e) {
                        return ResponseEntity.status(500).body(null);
                } catch (AppException e) {
                        return ResponseEntity.badRequest().build();
                }
        }

        /**
         * 📌 12. API: Cập nhật mật khẩu + gửi mail thông báo
         */
        @Operation(summary = "Cập nhật mật khẩu", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/customer/reset-password")
        public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
                userService.resetPassword(request);
                return ResponseEntity.ok().body("Cập nhật thành công");
        }

        /**
         * 📌 13. API: Tạo địa chỉ giao hàng
         */
        @Operation(summary = "Thêm địa chỉ giao hàng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Thêm thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/customer/address")
        public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressCreateRequest request) {
                return ResponseEntity.ok().body(userService.createAddress(request));
        }

        /**
         * 📌 13. API: Lấy danh sách địa chỉ giao hàng
         */
        @GetMapping("/customer/address")
        @Operation(summary = "Lấy danh sách địa chỉ giao hàng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        public ResponseEntity<Page<AddressDto>> getAllAddress(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size); // Nếu cần sort thì thêm .withSort(...) tại đây
                return ResponseEntity.ok().body(userService.getAllAddress(pageable));
        }

        /**
         * 📌 14. API: Thay đổi trạng thái địa chỉ (true, false)
         */
        @Operation(summary = "Thay đổi trạng thái địa chỉ", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Thay đổi trạng thái thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @GetMapping("/customer/address/active/{addressId}")
        public ResponseEntity<String> changeAddressStatus(@PathVariable String addressId) {
                userService.changeAddressStatus(addressId);
                return ResponseEntity.ok().body("Thay đổi trạng thái thành công");
        }

        /**
         * 📌 15. API: Cập nhật địa chỉ giao hàng
         */
        @Operation(summary = "Cập nhật địa chỉ giao hàng ", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @PostMapping("/customer/address/updated")
        public ResponseEntity<AddressDto> updateAddressByGmail(@Valid @RequestBody AddressUpdateRequest addressDto) {
                return ResponseEntity.ok().body(userService.updateAddress(addressDto));
        }

        /**
         * 📌 16. API: Xóa địa chỉ giao hàng
         */
        @Operation(summary = "Xóa địa chỉ giao hàng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                        @ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Xóa địa chỉ thành công.")
        @DeleteMapping("/address/delete/{addressId}")
        public ResponseEntity<String> deleteAddress(@PathVariable String addressId) {
                userService.deleteAddress(addressId);
                return ResponseEntity.ok().body("Xóa thành công");
        }

        /**
         * 📌 17. API: Tạo tài khoản người dùng, không cần kích hoạt (đúng là đúng)
         */
        @Operation(summary = "Tạo tài khoản người dùng", description = "Tạo tài khoản người dùng mới")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tạo tài khoản thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Tạo tài khoản thành công.")
        @PostMapping("/admin/account")
        public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto request) {
                return ResponseEntity.ok().body(userService.createUser(request));
        }

        /**
         * 📌 18. API: Thay đổi vai trò người dùng
         */
        @Operation(summary = "Thay đổi vai trò người dùng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Thay đổi vai trò thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Thay đổi vai trò thành công.")
        @PostMapping("/admin/account/role")
        public ResponseEntity<String> changeRole(@Valid @RequestBody RoleChangeRequest request) {
                userService.changeRole(request);
                return ResponseEntity.ok().body("Thay đổi vai trò thành công");
        }

        /**
         * 📌 19. API: Thay đổi trạng thái người dùng
         */
        @Operation(summary = "Thay đổi trạng thái người dùng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Thay đổi trạng thái thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Thay đổi trạng thái thành công.")
        @PostMapping("/admin/account/active")
        public ResponseEntity<String> changeStatus(@Valid @RequestBody StatusChangeRequest request) {
                userService.changeStatus(request);
                return ResponseEntity.ok().body("Thay đổi trạng thái thành công");
        }

        /**
         * 📌 20. API: Lấy ra danh sách người dùng
         */
        @Operation(summary = "Lấy danh sách người dùng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Lấy danh sách người dùng thành công.")
        @GetMapping("/admin/account")
        public ResponseEntity<Page<UserDto>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok().body(userService.getAllUsers(pageable));
        }

        /**
         * 📌 21. API: Xóa người dùng
         */
        @Operation(summary = "Xóa người dùng", description = "Tài khoản đã đăng nhập")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
                        @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        @AppMessage("Xóa người dùng thành công.")
        @DeleteMapping("/admin/account/{email}")
        public ResponseEntity<String> deleteUser(@PathVariable String email) {
                userService.deleteUser(email);
                return ResponseEntity.ok().body("Xóa người dùng thành công");
        }

}
