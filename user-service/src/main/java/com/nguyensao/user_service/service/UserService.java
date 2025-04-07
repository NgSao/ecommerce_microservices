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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.user_service.constant.GithubConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.AddressCreateRequest;
import com.nguyensao.user_service.dto.request.AddressUpdateRequest;
import com.nguyensao.user_service.dto.request.ResetPasswordRequest;
import com.nguyensao.user_service.dto.request.RoleChangeRequest;
import com.nguyensao.user_service.dto.request.StatusChangeRequest;
import com.nguyensao.user_service.dto.request.UserLoginRequest;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.dto.response.UserCustomerResponse;
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
import com.nguyensao.user_service.utils.PasswordValidator;

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
        if (!PasswordValidator.isStrongPassword(userDto.getPassword())) {
            throw new AppException(
                    "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }

        if (!userDto.getPassword().equals(userDto.getPasswordAgain())) {
            throw new AppException("Mật khẩu và xác nhận mật khẩu không khớp.");
        }
        User user = User.builder()
                .fullname(userDto.getFullname())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .status(UserStatus.INACTIVE)
                .role(RoleAuthorities.CUSTOMER)
                .build();

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
            User existingUser = checkUserByEmailNotActive(email);
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
            User existingUser = checkUserByEmailNotActive(email);
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
                throw new AppException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
            }
            throw new AppException("Email đã được sử dụng. Vui lòng chọn email khác.");
        }
    }

    // CheckEmail ch kích hoạt
    public User checkUserByEmailNotActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Email không tồn tại"));
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
        }

        return user;
    }

    // Check Email có trả về
    public User checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Email không tồn tại"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AppException("Tài khoản chưa được xác thực.");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
        }

        return user;
    }

    // Check Email k trả về
    public void checkExistsEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Email không tồn tại"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AppException("Tài khoản đã được xác thực trước đó.");
        }
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
        }
    }

    // Check uuid
    public User findUserById(String uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> new AppException("UUID không tồn tại"));
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
    public UserCustomerResponse getUserByToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        User user = findUserById(uuid);
        return mapper.toUserCustomerResponse(user);
    }

    // 9.
    public UserCustomerResponse getUserByEmail(String email) {
        User user = checkUserByEmail(email);
        return mapper.toUserCustomerResponse(user);
    }

    // 11.
    public UserCustomerResponse updateAccount(MultipartFile file, UserUpdateRequest request) throws IOException {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwt.getSubject();
        User user = checkUserByEmail(token);
        if (user == null) {
            throw new AppException("User not found");
        }
        String imageUrl = null;
        if (file != null) {
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

            imageUrl = "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/" + GithubConstant.BRANCH
                    + "/" + imagePath;
        }

        user.setFullname(request.getFullname());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());

        if (imageUrl != null) {
            user.setProfileImageUrl(imageUrl);
        }
        userRepository.save(user);
        return mapper.toUserCustomerResponse(user);
    }

    // 12
    public void resetPassword(ResetPasswordRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getSubject();
        User user = checkUserByEmail(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException("Mật khẩu cũ không chính xác.");
        }

        if (!PasswordValidator.isStrongPassword(request.getNewPassword())) {
            throw new AppException(
                    "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException("Mật khẩu mới không khớp.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        emailService.sendPasswordResetConfirmation(email);
    }

    // 13
    public AddressDto createAddress(AddressCreateRequest addressCreateRequest) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        User user = findUserById(uuid);
        Address address = mapper.toAddressCreateRequest(addressCreateRequest);
        address.setUser(user);
        if (Boolean.TRUE.equals(addressCreateRequest.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.save(address);
        return mapper.addressToDto(address);
    }

    public Page<AddressDto> getAllAddress(Pageable pageable) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        Page<Address> addressPage = addressRepository.findAllByUserId(uuid, pageable);

        return addressPage.map(mapper::addressToDto);

    }

    // 14
    public void changeAddressStatus(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        User user = findUserById(uuid);

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
    public AddressDto updateAddress(AddressUpdateRequest addressDto) {
        // Lấy thông tin người dùng từ JWT
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        User user = findUserById(uuid);

        // Tìm địa chỉ cần cập nhật
        Address address = addressRepository.findById(addressDto.getId())
                .orElseThrow(() -> new AppException("Address not found"));

        // Kiểm tra xem địa chỉ có phải của người dùng hiện tại không
        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new AppException("Address does not belong to the user or user not linked");
        }

        address = mapper.toAddresUpdatedRequest(addressDto, user);

        if (Boolean.TRUE.equals(addressDto.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.save(address);

        return mapper.addressToDto(address);
    }

    // 16
    public void deleteAddress(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString("uuid");
        User user = findUserById(uuid);
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
        userRepository.save(user);
        return mapper.userToDto(user);
    }

    // 18
    public void changeRole(RoleChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found"));
        user.setRole(request.getRoleAuthorities());
        userRepository.save(user);

    }

    // 19
    public void changeStatus(StatusChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found"));
        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    // 20
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::userToDto);
    }

    // 21
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found"));
        userRepository.deleteById(user.getId());
    }

}
