package com.nguyensao.user_service.controller;

import com.nguyensao.user_service.annotation.AppMessage;
import com.nguyensao.user_service.constant.SecurityConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.EmailRequest;
import com.nguyensao.user_service.dto.request.OtpRequest;
import com.nguyensao.user_service.dto.request.ResetPasswordRequest;
import com.nguyensao.user_service.dto.request.UserLoginRequest;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.dto.response.UserLoginResponse;
import com.nguyensao.user_service.enums.UserGender;
import com.nguyensao.user_service.exception.AppException;
import com.nguyensao.user_service.service.EmailService;
import com.nguyensao.user_service.service.UserService;
import com.nguyensao.user_service.utils.GenerateOTP;
import com.nguyensao.user_service.utils.GeneratePassword;
import com.nguyensao.user_service.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
        private final UserService userService;
        private final JwtUtil jwtUtil;
        private final EmailService emailService;

        public UserController(UserService userService, JwtUtil jwtUtil, EmailService emailService) {
                this.userService = userService;
                this.jwtUtil = jwtUtil;
                this.emailService = emailService;
        }

        /**
         * 📌 1. API: Đăng ký tài khoản (chưa kích hoạt) +mã xác thực OTP qua email
         */
        @PostMapping("/public/register")
        @AppMessage("Đăng kí thành công, vui lòng kiểm tra email để kích hoạt tài khoản")
        public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest userDto) {
                String verificationCode = GenerateOTP.generate();
                userService.registerUser(userDto, verificationCode);
                emailService.sendVerificationEmail(userDto.getEmail(), verificationCode);
                String token = jwtUtil.createOtpToken(userDto.getEmail(), verificationCode);
                ResponseCookie cookie = ResponseCookie
                                .from("OTP", token)
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
        @PostMapping("/public/verify")
        @AppMessage("Vui lòng kiểm tra mã xác thực trong gamil.")
        public ResponseEntity<String> verifyActivateUser(@Valid @RequestBody EmailRequest request,
                        @CookieValue(value = "OTP", defaultValue = "") String cookieCode) {
                userService.checkExistsEmail(request.getEmail());
                String verificationCode = GenerateOTP.generate();
                emailService.sendVerificationEmail(request.getEmail(), verificationCode);
                String token = jwtUtil.createOtpToken(request.getEmail(), verificationCode);
                ResponseCookie cookie = ResponseCookie
                                .from("OTP", token)
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
        @PostMapping("/public/activate")
        @AppMessage("Kích hoạt tài khoản thành công.")
        public ResponseEntity<String> activateUser(@Valid @RequestBody OtpRequest request,
                        @CookieValue(value = "OTP", defaultValue = "") String cookieCode) {
                String codeToken = jwtUtil.decodedToken(cookieCode);
                System.out.println("Email từ token: " + codeToken);

                String email = jwtUtil.decodedTokenClaimEmail(cookieCode);
                System.out.println("Email từ token: " + email);

                userService.verifyOTP(email, request.getCode(), codeToken);
                ResponseCookie cookie = ResponseCookie
                                .from("OTP", null)
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
        @PostMapping("/public/forgot-password")
        @AppMessage("Vui lòng kiểm tra email để nhận mật khẩu mới.")
        public ResponseEntity<String> verifyPassword(@Valid @RequestBody OtpRequest request,
                        @CookieValue(value = "OTP", defaultValue = "") String cookieCode) {
                String codeToken = jwtUtil.decodedToken(cookieCode);
                String email = jwtUtil.decodedTokenClaimEmail(cookieCode);
                String newPassword = GeneratePassword.generate();
                userService.forgotPassword(email, request.getCode(), codeToken, newPassword);
                emailService.sendVerificationPassword(email, newPassword);
                ResponseCookie cookie = ResponseCookie
                                .from("OTP", null)
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
        @PostMapping("/public/login")
        @AppMessage("Đăng nhập tài khoản thành công.")
        public ResponseEntity<UserLoginResponse> loginAuth(@Valid @RequestBody UserLoginRequest request) {
                UserLoginResponse authResponse = userService.loginUser(request);
                String refreshToken = userService.refreshToken(request.getEmail());
                ResponseCookie cookie = ResponseCookie
                                .from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(SecurityConstant.REFRESH_TOKEN_EXP).build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(authResponse);
        }

        /**
         * 📌 6. API: Cập nhật lại tonken
         */
        @GetMapping("/refresh")
        @AppMessage("Cập nhật thành công.")
        public ResponseEntity<UserLoginResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
                if (refreshToken == null) {
                        throw new AppException("Không tìm thấy refresh token.");
                }
                try {
                        Jwt jwt = jwtUtil.checkValidRefreshToken(refreshToken);
                        String email = jwt.getSubject();

                        String newAccessToken = jwtUtil.createAccessToken(email);
                        String newRefreshToken = jwtUtil.createRefreshToken(email);

                        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
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
        @GetMapping("/logout")
        public ResponseEntity<Void> logOutAuth() {
                ResponseCookie cookie = ResponseCookie
                                .from("refreshToken", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();
                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(null);

        }

        /**
         * 📌 8. API: Lấy người dùng bằng token
         */
        @GetMapping("/account")
        public ResponseEntity<UserDto> getUserByToken(
                        @CookieValue(name = "refreshToken", defaultValue = "") String tonKen) {
                return ResponseEntity.ok().body(userService.getUserByToken(tonKen));
        }

        /**
         * 📌 9. API: Lấy thông tin người dùng theo email
         */
        @GetMapping("/email")
        public ResponseEntity<UserDto> getUserByEmail(@RequestBody String email) {
                return ResponseEntity.ok().body(userService.getUserByEmail(email));
        }

        /**
         * 📌 10. API: Lấy thông tin người dùng theo idUser
         */
        @GetMapping("/{id}")
        public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
                return ResponseEntity.ok().body(userService.getUserById(id));
        }

        /**
         * 📌 11. API: Cập nhật tài khoản
         */
        // @PostMapping("/account/updated")
        // public ResponseEntity<UserDto> updateAccount(
        // @RequestParam("file") MultipartFile file,
        // @CookieValue(name = "refreshToken") String token,
        // @RequestParam("fullname") String fullname,
        // @RequestParam("birthday") Instant birthday,
        // @RequestParam("gender") UserGender gender) {
        // try {
        // UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        // .email(token)
        // .fullname(fullname)
        // .birthday(birthday)
        // .gender(gender)
        // .build();
        // UserDto updatedUser = userService.updateAccount(file, userUpdateRequest);
        // return ResponseEntity.ok().body(updatedUser);
        // } catch (IOException e) {
        // return ResponseEntity.status(500).body(null);
        // } catch (AppException e) {
        // return ResponseEntity.badRequest().build();
        // }
        // }@Operation(summary = "Cập nhật tài khoản", description = "Cho phép người
        // dùng cập nhật thông tin cá nhân")

        // @PostMapping(value = "/account/updated", consumes = "multipart/form-data")
        // public ResponseEntity<UserDto> updateAccount(
        // @RequestPart(value = "file", required = false) @Schema(type = "string",
        // format = "binary") MultipartFile file,
        // @RequestParam("fullname") String fullname,
        // @RequestParam("birthday") @Schema(type = "string", format = "date", example =
        // "2024-08-20") String birthdayString,
        // @RequestParam("gender") UserGender gender) {
        // try {
        // // ✅ Chuyển đổi `birthdayString` thành `LocalDate`
        // LocalDate birthday;
        // try {
        // birthday = LocalDate.parse(birthdayString, DateTimeFormatter.ISO_DATE);
        // } catch (DateTimeParseException e) {
        // return ResponseEntity.badRequest().body(null);
        // }

        // // ✅ Chuyển `LocalDate` sang `Instant`
        // Instant birthdayInstant =
        // birthday.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

        // // ✅ Tạo request object
        // UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        // .fullname(fullname)
        // .birthday(birthdayInstant)
        // .gender(gender)
        // .build();

        // // ✅ Cập nhật thông tin người dùng
        // UserDto updatedUser = userService.updateAccount(file, userUpdateRequest);
        // return ResponseEntity.ok().body(updatedUser);

        // } catch (IOException e) {
        // return ResponseEntity.status(500).body(null);
        // } catch (AppException e) {
        // return ResponseEntity.badRequest().build();
        // }
        // }

        @PostMapping(value = "/account/updated", consumes = "multipart/form-data")
        @Operation(summary = "Cập nhật thông tin tài khoản người dùng", description = "Cập nhật thông tin người dùng bao gồm tên, ngày sinh, giới tính và hình ảnh")
        public ResponseEntity<UserDto> updateAccount(
                        @RequestPart(value = "file", required = false) @Schema(type = "string", format = "binary") MultipartFile file,

                        @RequestParam("fullname") @Schema(description = "Tên đầy đủ của người dùng") String fullname,

                        @RequestParam("birthday") @Schema(type = "string", format = "date", example = "2024-08-20") String birthdayString,

                        @RequestParam("gender") @Schema(description = "Giới tính của người dùng") UserGender gender) {

                try {
                        // Chuyển đổi `birthdayString` thành `LocalDate`
                        LocalDate birthday;
                        try {
                                birthday = LocalDate.parse(birthdayString, DateTimeFormatter.ISO_DATE);
                        } catch (DateTimeParseException e) {
                                return ResponseEntity.badRequest().body(null);
                        }

                        // Chuyển `LocalDate` sang `Instant`
                        Instant birthdayInstant = birthday.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

                        // Tạo request object
                        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                                        .fullname(fullname)
                                        .birthday(birthdayInstant)
                                        .gender(gender)
                                        .build();

                        // Cập nhật thông tin người dùng
                        UserDto updatedUser = userService.updateAccount(file, userUpdateRequest);
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
        @PostMapping("/account/reset-password")
        public ResponseEntity<String> resetPassword(@CookieValue(name = "refreshToken") String token,
                        @Valid @RequestBody ResetPasswordRequest request) {
                userService.resetPassword(token, request);
                return ResponseEntity.ok().body("Cập nhật thành công");
        }

        /**
         * 📌 13. API: Tạo địa chỉ giao hàng
         */
        @PostMapping("/address")
        public ResponseEntity<AddressDto> createAddress(@CookieValue(name = "refreshToken") String token,
                        @Valid @RequestBody AddressDto request) {
                return ResponseEntity.ok().body(userService.createAddress(token, request));
        }

        /**
         * 📌 14. API: Thay đổi trạng thái địa chỉ (true, false)
         */
        @PostMapping("/address/active/{addressId}")
        public ResponseEntity<Void> changeAddressStatus(
                        @CookieValue(name = "refreshToken") String token,
                        @PathVariable String addressId) {
                userService.changeAddressStatus(token, addressId);
                return ResponseEntity.ok().body(null);
        }

        /**
         * 📌 15. API: Cập nhật địa chỉ giao hàng bằng gmail
         */
        @GetMapping("/address/updated")
        public ResponseEntity<AddressDto> updateAddressByGmail(@CookieValue(name = "refreshToken") String token,
                        @RequestBody AddressDto addressDto) {
                return ResponseEntity.ok().body(userService.updateAddress(token, addressDto));
        }

        /**
         * 📌 16. API: Xóa địa chỉ giao hàng
         */
        @DeleteMapping("/address/delete/{addressId}")
        public ResponseEntity<Void> deleteAddress(@CookieValue(name = "refreshToken") String token,
                        @PathVariable String addressId) {
                userService.deleteAddress(token, addressId);
                return ResponseEntity.ok().body(null);
        }

        /**
         * 📌 17. API: Tạo tài khoản người dùng, không cần kích hoạt
         */
        @PostMapping("/admin/account")
        public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto request) {
                return ResponseEntity.ok().body(userService.createUser(request));
        }

        /**
         * 📌 18. API: Thay đổi vai trò người dùng
         */
        @PostMapping("/admin/account/role")
        public ResponseEntity<Void> changeRole(@Valid @RequestBody UserDto request) {
                userService.changeRole(request);
                return ResponseEntity.ok().body(null);
        }

        /**
         * 📌 19. API: Thay đổi trạng thái người dùng
         */
        @PostMapping("/admin/account/active")
        public ResponseEntity<Void> changeStatus(@Valid @RequestBody UserDto request) {
                userService.changeStatus(request);
                return ResponseEntity.ok().body(null);
        }

        /**
         * 📌 20. API: Lấy ra danh sách người dùng
         */
        @GetMapping("/admin/account")
        public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
                return ResponseEntity.ok().body(userService.getAllUsers(pageable));
        }

        /**
         * 📌 21. API: Xóa người dùng
         */
        @DeleteMapping("/admin/account/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable String id) {
                userService.deleteUser(id);
                return ResponseEntity.ok().body(null);
        }

}
