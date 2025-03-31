package com.nguyensao.user_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.user_service.constant.GithubConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.ResetPasswordRequest;
import com.nguyensao.user_service.dto.request.UserLoginRequest;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.dto.response.UserLoginResponse;
import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserStatus;
import com.nguyensao.user_service.exception.AppException;
import com.nguyensao.user_service.mapper.UserMapper;
import com.nguyensao.user_service.model.Address;
import com.nguyensao.user_service.model.User;
import com.nguyensao.user_service.repository.AddressRepository;
import com.nguyensao.user_service.repository.UserRepository;
import com.nguyensao.user_service.utils.FileValidation;
import com.nguyensao.user_service.utils.JwtUtil;

@Service
public class UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserService(UserMapper mapper, UserRepository userRepository, AddressRepository addressRepository,
            PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtUtil jwtUtil, EmailService emailService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;

    }

    // User
    // 1. Đăng kí tài khoản chưa kích hoạt
    public void registerUser(UserRegisterRequest userDto, String verificationCode) {
        validateEmailRegister(userDto.getEmail());
        User user = mapper.toUserRegisterRequest(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setStatus(UserStatus.INACTIVE);
        user.setRole(RoleAuthorities.USER);
        userRepository.save(user);
    }

    private Map<String, Integer> otpAttempts = new HashMap<>();

    // 2.Xác thực OTP đẻ kích hoạt tài khoản
    public void verifyOTP(String email, String code, String cookieCode) {
        int attempts = otpAttempts.getOrDefault(email, 0);
        if (cookieCode.isEmpty()) {
            throw new AppException("Không tim thấy mã OTP.");
        }
        if (cookieCode.equals(code)) {
            User existingUser = checkUserByEmail(email);
            existingUser.setStatus(UserStatus.ACTIVE);
            userRepository.save(existingUser);
        } else {
            otpAttempts.put(email, attempts + 1);
            if (attempts >= 3) {
                throw new AppException("Số lần nhập quá nhiều. Vui lòng kích hoạt lại tài khoản.");
            }
            throw new AppException("Mã OTP không đúng. Vui lòng nhập lại");
        }
    }

    // 3.Xác thực OTP để nhận mật khẩu mới
    public void forgotPassword(String email, String code, String cookieCode, String newPassword) {
        int attempts = otpAttempts.getOrDefault(email, 0);
        if (cookieCode.isEmpty()) {
            throw new AppException("Không tim thấy mã OTP.");
        }
        if (cookieCode.equals(code)) {
            User existingUser = checkUserByEmail(email);
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(existingUser);
        } else {
            otpAttempts.put(email, attempts + 1);
            if (attempts >= 3) {
                throw new AppException("Số lần nhập quá nhiều. Vui lòng kích hoạt lại tài khoản.");
            }
            throw new AppException("Mã OTP không đúng. Vui lòng nhập lại");
        }
    }

    public void validateEmailRegister(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new AppException(
                        "Tài khoản đã tồn tại nhưng chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt hoặc yêu cầu gửi lại OTP.");
            }
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new AppException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ để được trợ giúp.");
            }
            throw new AppException("Email đã được sử dụng. Vui lòng chọn email khác.");
        }
    }

    // Check Email có trả về
    public User checkUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("Email không tồn tại"));
    }

    // Check Email k trả về
    public void checkExistsEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new AppException("Email không tồn tại");
        }
    }

    // 5.Login
    public UserLoginResponse loginUser(UserLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access_token = jwtUtil.createAccessToken(request.getEmail());
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(access_token);
        userLoginResponse.setEmail(request.getEmail());
        return userLoginResponse;
    }

    public String refreshToken(String email) {
        String refreshToken = jwtUtil.createRefreshToken(email);
        return refreshToken;
    }

    // ------------------------
    // 8
    public UserDto getUserByToken(String token) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);
        return mapper.userToDto(user);
    }

    // 9.
    public UserDto getUserByEmail(String email) {
        User user = checkUserByEmail(email);
        return mapper.userToDto(user);
    }

    // 10.
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException("Id không tồn tại"));
        return mapper.userToDto(user);
    }

    // 11.
    public UserDto updateAccount(MultipartFile file, UserUpdateRequest request) throws IOException {
        String email = jwtUtil.decodedToken(request.getEmail());
        User user = checkUserByEmail(email);
        GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
        GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String imagePath = "microservice/users/" + timestamp + "_" + file.getOriginalFilename();
        if (!FileValidation.isValidImage(file)) {
            throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
        }
        repository.createContent()
                .content(file.getBytes())
                .path(imagePath)
                .message("Tải ảnh người dùng: " + file.getOriginalFilename())
                .branch(GithubConstant.BRANCH)
                .commit();
        String imageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/" + GithubConstant.BRANCH
                + "/" + imagePath;
        mapper.toUserUpdatedRequest(request);
        user.setProfileImageUrl(imageUrl);
        return mapper.userToDto(user);
    }

    // 12
    public void resetPassword(String token, ResetPasswordRequest request) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        emailService.sendPasswordResetConfirmation(email);
    }

    // 13
    public AddressDto createAddress(String token, AddressDto dto) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);
        Address address = mapper.addressToEntity(dto);
        address.setUser(user);
        address.setActive(true);
        addressRepository.save(address);
        return mapper.addressToDto(address);
    }

    // 14
    public void changeAddressStatus(String token, String addressId) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);

        Address selectedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException("Address not found"));

        if (!selectedAddress.getUser().getId().equals(user.getId())) {
            throw new AppException("Address does not belong to the user");
        }
        user.getAddresses().forEach(address -> address.setActive(false));
        selectedAddress.setActive(true);

        addressRepository.saveAll(user.getAddresses());
    }

    // 15
    public AddressDto updateAddress(String token, AddressDto addressDto) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);
        Address address = addressRepository.findById(addressDto.getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to the user");
        }

        mapper.addressToEntity(addressDto);
        if (addressDto.getActive() != null && addressDto.getActive()) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.saveAll(user.getAddresses());

        // Trả về dữ liệu sau khi cập nhật
        return mapper.addressToDto(address);
    }

    // 16
    public void deleteAddress(String token, String addressId) {
        String email = jwtUtil.decodedToken(token);
        User user = checkUserByEmail(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException("Address does not belong to the user");
        }
        addressRepository.delete(address);
    }

    // 17
    public UserDto createUser(UserDto userDto) {
        validateEmailRegister(userDto.getEmail());
        User user = mapper.userToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setStatus(UserStatus.INACTIVE);
        user.setRole(RoleAuthorities.USER);
        userRepository.save(user);
        return mapper.userToDto(user);
    }

    // 18
    public void changeRole(UserDto reques) {
        User user = userRepository.findById(reques.getId()).orElseThrow(() -> new AppException("User not found"));
        user.setRole(reques.getRole());
        userRepository.save(user);

    }

    // 19
    public void changeStatus(UserDto dto) {
        User user = userRepository.findById(dto.getId()).orElseThrow(() -> new AppException("User not found"));
        user.setStatus(dto.getStatus());
        userRepository.save(user);
    }

    // 20
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::userToDto);
    }

    // 21
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
