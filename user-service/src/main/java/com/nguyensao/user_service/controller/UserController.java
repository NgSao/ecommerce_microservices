package com.nguyensao.user_service.controller;

import com.nguyensao.user_service.annotation.AppMessage;
import com.nguyensao.user_service.constant.AppMessageConstant;
import com.nguyensao.user_service.constant.UserConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.AddressCreateRequest;
import com.nguyensao.user_service.dto.request.AddressUpdateRequest;
import com.nguyensao.user_service.dto.request.AdminRegisterRequest;
import com.nguyensao.user_service.dto.request.EmailRequest;
import com.nguyensao.user_service.dto.request.VerifyRequest;
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
import com.nguyensao.user_service.service.UserService;

import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        // Auth
        @AppMessage(AppMessageConstant.registerUser)
        @PostMapping("/public/register")
        public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterRequest userDto) {
                userService.registerUser(userDto);
                return ResponseEntity.ok().body(UserConstant.REGISTER_SUCCESS);
        }

        @AppMessage(AppMessageConstant.verifyUser)
        @PostMapping("/public/verify")
        public ResponseEntity<String> verifyUser(@Valid @RequestBody VerifyRequest request) {
                userService.verifyUser(request);
                return ResponseEntity.ok().body(UserConstant.VERIFY_SUCCESS);

        }

        @AppMessage(AppMessageConstant.sendOtp)
        @PostMapping("/public/otp")
        public ResponseEntity<String> sendOtp(@Valid @RequestBody EmailRequest request) {
                userService.sendOtp(request);
                return ResponseEntity.ok().body(UserConstant.VERIFY_CODE_SENT);
        }

        @AppMessage(AppMessageConstant.verifyPassword)
        @PostMapping("/public/forgot-password")
        public ResponseEntity<String> verifyPassword(@Valid @RequestBody VerifyRequest request) {
                userService.forgotPassword(request);
                return ResponseEntity.ok().body(UserConstant.VERIFY_PASSWORD_RESET_SUCCESS);
        }

        @AppMessage(AppMessageConstant.loginAuth)
        @PostMapping("/public/login")
        public ResponseEntity<UserLoginResponse> loginAuth(@Valid @RequestBody UserLoginRequest request) {
                UserLoginResponse authResponse = userService.loginUser(request);
                return ResponseEntity.ok().body(authResponse);
        }

        @AppMessage(AppMessageConstant.logout)
        @GetMapping("/logout")
        public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
                String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
                userService.logOut(token);
                return ResponseEntity.ok().body(AppMessageConstant.logout);
        }

        // Admin
        @GetMapping("/admin/refresh")
        public ResponseEntity<String> refeshTk(@RequestHeader("Authorization") String authHeader) {
                String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
                return ResponseEntity.ok().body(userService.refreshToken(token));
        }

        @AppMessage(AppMessageConstant.createUser)
        @PostMapping("/admin/create")
        public ResponseEntity<String> createUser(@Valid @RequestBody AdminRegisterRequest request) {
                userService.createUser(request);
                return ResponseEntity.ok().body(AppMessageConstant.createUser);
        }

        @AppMessage(AppMessageConstant.changeRole)
        @PostMapping("/admin/role")
        public ResponseEntity<String> changeRole(@Valid @RequestBody RoleChangeRequest request) {
                userService.changeRole(request);
                return ResponseEntity.ok().body(AppMessageConstant.changeRole);
        }

        @AppMessage(AppMessageConstant.changeStatus)
        @PostMapping("/admin/active")
        public ResponseEntity<String> changeStatus(@Valid @RequestBody StatusChangeRequest request) {
                userService.changeStatus(request);
                return ResponseEntity.ok().body(AppMessageConstant.changeStatus);
        }

        @AppMessage(AppMessageConstant.changeStatus)
        @GetMapping("/admin/all")
        public ResponseEntity<Page<UserDto>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String keyword) {
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok().body(userService.getAllUsers(pageable, keyword));
        }

        @AppMessage(AppMessageConstant.deleteUser)
        @DeleteMapping("/admin/delete")
        public ResponseEntity<String> deleteUsers(@RequestBody List<String> userIds) {
                userService.deleteUsers(userIds);
                return ResponseEntity.ok().body(AppMessageConstant.deleteUser);
        }

        // Customer
        @GetMapping("/customer")
        public ResponseEntity<UserCustomerResponse> getUserByToken() {
                return ResponseEntity.ok().body(userService.getUserByToken());
        }

        @PostMapping(value = "/customer/updated", consumes = "multipart/form-data")
        public ResponseEntity<UserCustomerResponse> updateAccount(
                        @RequestPart(value = "file", required = false) MultipartFile file,
                        @RequestParam("fullName") String fullName,
                        @RequestParam("phone") String phone,
                        @RequestParam("birthday") String birthdayString,
                        @RequestParam("gender") UserGender gender) {

                try {
                        LocalDate birthday;
                        try {
                                birthday = LocalDate.parse(birthdayString, DateTimeFormatter.ISO_DATE);
                        } catch (DateTimeParseException e) {
                                return ResponseEntity.badRequest().body(null);
                        }

                        Instant birthdayInstant = birthday.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

                        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                                        .fullName(fullName)
                                        .phone(phone)
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

        @PostMapping("/customer/reset-password")
        public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
                userService.resetPassword(request);
                return ResponseEntity.ok().body(AppMessageConstant.resetPassword);
        }

        @PostMapping("/customer/address")
        public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressCreateRequest request) {
                return ResponseEntity.ok().body(userService.createAddress(request));
        }

        @GetMapping("/customer/address")
        public ResponseEntity<List<AddressDto>> getAllAddress() {
                return ResponseEntity.ok().body(userService.getAllAddress());
        }

        @GetMapping("/address/active/{addressId}")
        public ResponseEntity<String> changeAddressStatus(@PathVariable String addressId) {
                userService.changeAddressStatus(addressId);
                return ResponseEntity.ok().body(AppMessageConstant.changeAddressStatus);
        }

        @PostMapping("/address/updated")
        public ResponseEntity<AddressDto> updateAddressByGmail(@Valid @RequestBody AddressUpdateRequest addressDto) {
                return ResponseEntity.ok().body(userService.updateAddress(addressDto));
        }

        @AppMessage(AppMessageConstant.deleteAddress)
        @DeleteMapping("/address/delete/{addressId}")
        public ResponseEntity<String> deleteAddress(@PathVariable String addressId) {
                userService.deleteAddress(addressId);
                return ResponseEntity.ok().body(AppMessageConstant.deleteAddress);
        }

        @GetMapping("/success")
        public ResponseEntity<String> oauth2Success() {
                return ResponseEntity.ok().body("Đăng nhập OAuth2 thành công!");
        }

        @GetMapping("/linked-already")
        public ResponseEntity<String> alreadyLinked() {
                return ResponseEntity.ok().body("Tài khoản đã được liên kết trước đó!");
        }

        @GetMapping("/linked-success")
        public ResponseEntity<String> linkedSuccess() {
                return ResponseEntity.ok().body("Liên kết tài khoản thành công!");
        }

        // @PostMapping("/unlinked")
        // public ResponseEntity<String> unLink(@RequestBody OAuth2LinkRequest request)
        // {
        // userService.unlinkOAuth2Account(request);
        // return ResponseEntity.ok("Tài khoản đã được hủy liên kết!");
        // }

}

// Test Cookies
// @GetMapping("/public/test")
// @AppMessage("Đăng nhập tài khoản thành công.")
// public void Test() {
// Jwt jwt = (Jwt)
// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
// System.out.println("Test" + jwt.getTokenValue());
// String email = jwt.getSubject();
// System.out.println("Test1" + email);
// String uuid = jwt.getClaimAsString("uuid");
// System.out.println("Test2" + uuid);

// }
