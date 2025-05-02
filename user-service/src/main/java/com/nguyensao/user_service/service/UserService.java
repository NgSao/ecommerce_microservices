package com.nguyensao.user_service.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.user_service.constant.GithubConstant;
import com.nguyensao.user_service.constant.UserConstant;
import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.AddressCreateRequest;
import com.nguyensao.user_service.dto.request.AddressUpdateRequest;
import com.nguyensao.user_service.dto.request.AdminRegisterRequest;
import com.nguyensao.user_service.dto.request.EmailRequest;
import com.nguyensao.user_service.dto.request.ResetPasswordRequest;
import com.nguyensao.user_service.dto.request.RoleChangeRequest;
import com.nguyensao.user_service.dto.request.StatusChangeRequest;
import com.nguyensao.user_service.dto.request.UserLoginRequest;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.dto.request.VerifyRequest;
import com.nguyensao.user_service.dto.response.UserCustomerResponse;
import com.nguyensao.user_service.dto.response.UserLoginResponse;
import com.nguyensao.user_service.enums.Provider;
import com.nguyensao.user_service.enums.RoleAuthorities;
import com.nguyensao.user_service.enums.UserStatus;
import com.nguyensao.user_service.exception.AppException;
import com.nguyensao.user_service.kafka.EventEnum;
import com.nguyensao.user_service.kafka.OtpEvent;
import com.nguyensao.user_service.mapper.UserMapper;
import com.nguyensao.user_service.model.Address;
import com.nguyensao.user_service.model.Otp;
import com.nguyensao.user_service.model.User;
import com.nguyensao.user_service.repository.AddressRepository;
import com.nguyensao.user_service.repository.OtpRepository;
import com.nguyensao.user_service.repository.UserRepository;
import com.nguyensao.user_service.utils.FileValidation;
import com.nguyensao.user_service.utils.GenerateOTP;
import com.nguyensao.user_service.utils.GeneratePassword;
import com.nguyensao.user_service.utils.JwtUtil;
import com.nguyensao.user_service.utils.PasswordValidator;

@Service
public class UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, OtpEvent> kafkaTemplate;
    private final TokenBlacklistService tokenBlacklistService;

    public UserService(UserMapper mapper, UserRepository userRepository, AddressRepository addressRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtUtil jwtUtil,
            KafkaTemplate<String, OtpEvent> kafkaTemplate,
            TokenBlacklistService tokenBlacklistService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.kafkaTemplate = kafkaTemplate;
        this.tokenBlacklistService = tokenBlacklistService;

    }

    // Auth
    @Scheduled(fixedRate = 180000)
    public void deleteExpiredOtps() {
        Instant now = Instant.now();
        otpRepository.deleteAllByExpiresAtBefore(now);
    }

    public void registerUser(UserRegisterRequest userDto) {
        validateEmailRegister(userDto.getEmail());
        if (!PasswordValidator.isStrongPassword(userDto.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_REQUIREMENTS_MESSAGE);
        }
        String verificationCode = GenerateOTP.generate();
        User user = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .status(UserStatus.INACTIVE)
                .role(RoleAuthorities.CUSTOMER)
                .provider(Provider.LOCAL)
                .build();
        userRepository.save(user);
        Otp otp = Otp.builder()
                .email(userDto.getEmail())
                .otp(verificationCode)
                .expiresAt(Instant.now().plus(UserConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventEnum.REGISTER_OTP)
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .otp(verificationCode)
                .build();
        kafkaTemplate.send(UserConstant.KAFKA_EVENT, otpEvent);

    }

    public void verifyUser(VerifyRequest request) {
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.OTP_NOT_FOUND));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(UserConstant.OTP_EXPIRED);
        }

        if (!otp.getOtp().equals(request.getOtp())) {
            int newAttempts = otp.getAttempts() + 1;
            otp.setAttempts(newAttempts);

            if (newAttempts >= 3) {
                otpRepository.delete(otp);
                throw new AppException(UserConstant.OTP_RETRY_LIMIT_EXCEEDED);
            }
            otpRepository.save(otp);
            throw new AppException(String.format(UserConstant.OTP_INVALID_REMAINING_ATTEMPTS, (3 - newAttempts)));
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        otpRepository.delete(otp);
    }

    public void sendOtp(EmailRequest request) {
        User user = checkExistsEmail(request.getEmail());
        String optVery = GenerateOTP.generate();
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otp(optVery)
                .expiresAt(Instant.now().plus(UserConstant.EXPIRATION_OTP, ChronoUnit.SECONDS))
                .build();
        otpRepository.save(otp);
        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventEnum.VERIFY_OTP)
                .fullName(user.getFullName())
                .email(request.getEmail())
                .otp(optVery)
                .build();
        kafkaTemplate.send(UserConstant.KAFKA_EVENT, otpEvent);
    }

    public void forgotPassword(VerifyRequest request) {
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.OTP_NOT_FOUND));
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(UserConstant.OTP_EXPIRED);
        }
        if (!otp.getOtp().equals(request.getOtp())) {
            int newAttempts = otp.getAttempts() + 1;
            otp.setAttempts(newAttempts);

            if (newAttempts >= 3) {
                otpRepository.delete(otp);
                throw new AppException(UserConstant.OTP_RETRY_LIMIT_EXCEEDED);
            }
            otpRepository.save(otp);
            throw new AppException(String.format(UserConstant.OTP_INVALID_REMAINING_ATTEMPTS, (3 - newAttempts)));
        }
        User existingUser = checkUserByEmailNotActive(request.getEmail());
        String newPassword = GeneratePassword.generate();
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        otpRepository.delete(otp);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventEnum.FORGOT_PASSWORD)
                .fullName(existingUser.getFullName())
                .email(request.getEmail())
                .otp(newPassword)
                .build();
        kafkaTemplate.send(UserConstant.KAFKA_EVENT, otpEvent);
    }

    public void validateEmailRegister(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new AppException(UserConstant.ACCOUNT_INACTIVE);
            }
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new AppException(UserConstant.ACCOUNT_LOCKED);
            }
            throw new AppException(UserConstant.EMAIL_ALREADY_USED);
        }
    }

    // CheckEmail ch kích hoạt
    public User checkUserByEmailNotActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    // Check Email có trả về
    public User checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AppException(UserConstant.ACCOUNT_INACTIVE);
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    // Check Email trả về
    public User checkExistsEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(UserConstant.EMAIL_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AppException(UserConstant.ACCOUNT_ACTIVE);
        }
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(UserConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    // Check uuid
    public User findUserById(String uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
    }

    public UserLoginResponse loginUser(UserLoginRequest request) {
        User user = checkUserByEmail(request.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access_token = jwtUtil.createAccessToken(request.getEmail());

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(access_token);
        userLoginResponse.setEmail(request.getEmail());

        user.setLastLoginDate(Instant.now());
        userRepository.save(user);

        return userLoginResponse;
    }

    public String refreshToken(String token) {
        String email = jwtUtil.decodedToken(token);
        String refreshToken = jwtUtil.createRefreshToken(email);
        return refreshToken;
    }

    public void logOut(String token) {
        tokenBlacklistService.blacklist(token);
    }

    // Admin
    public void createUser(AdminRegisterRequest request) {
        validateEmailRegister(request.getEmail());
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.INACTIVE)
                .build();
        userRepository.save(user);
    }

    public void changeRole(RoleChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setRole(request.getRoleAuthorities());
        userRepository.save(user);

    }

    public void changeStatus(StatusChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserConstant.USER_NOT_FOUND));
        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    public Page<UserDto> getAllUsers(Pageable pageable, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(pageable)
                    .map(mapper::userToDto);
        }
        return userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, pageable)
                .map(mapper::userToDto);
    }

    public void deleteUsers(List<String> userIds) {
        List<String> existingUserIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (existingUserIds.isEmpty()) {
            throw new AppException(UserConstant.DELETE_NOT_USERS);
        }
        userRepository.deleteAllById(existingUserIds);
    }

    // ------------------------
    public UserCustomerResponse getUserByToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);
        return mapper.toUserCustomerResponse(user);
    }

    public UserCustomerResponse updateAccount(MultipartFile file, UserUpdateRequest request) throws IOException {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwt.getSubject();
        User user = checkUserByEmail(token);
        if (user == null) {
            throw new AppException(UserConstant.USER_NOT_FOUND);
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

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());

        if (imageUrl != null) {
            user.setProfileImageUrl(imageUrl);
        }
        userRepository.save(user);
        return mapper.toUserCustomerResponse(user);
    }

    public void resetPassword(ResetPasswordRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getSubject();
        User user = checkUserByEmail(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_OLD_INCORRECT);
        }

        if (!PasswordValidator.isStrongPassword(request.getNewPassword())) {
            throw new AppException(UserConstant.PASSWORD_REQUIREMENTS_MESSAGE);

        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(UserConstant.PASSWORD_NEW_SAME_AS_OLD);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(UserConstant.PASSWORD_NEW_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventEnum.RESET_PASSWORD)
                .fullName(user.getFullName())
                .email(email)
                .otp(null)
                .build();
        kafkaTemplate.send(UserConstant.KAFKA_EVENT, otpEvent);

    }

    // 13
    public AddressDto createAddress(AddressCreateRequest addressCreateRequest) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
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

    public List<AddressDto> getAllAddress() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        List<Address> addressPage = addressRepository.findAllByUserId(uuid);
        return addressPage.stream()
                .map(mapper::addressToDto)
                .collect(Collectors.toList());
    }

    public void changeAddressStatus(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);

        Address selectedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));

        if (!selectedAddress.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER);
        }
        user.getAddresses().forEach(address -> address.setActive(false));
        selectedAddress.setActive(true);

        addressRepository.saveAll(user.getAddresses());
    }

    public AddressDto updateAddress(AddressUpdateRequest addressDto) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);

        Address address = addressRepository.findById(addressDto.getId())
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));

        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER_OR_USER_NOT_LINKED);
        }

        address = mapper.toAddresUpdatedRequest(addressDto, user);

        if (Boolean.TRUE.equals(addressDto.getActive())) {
            user.getAddresses().forEach(a -> a.setActive(false));
            address.setActive(true);
        }

        addressRepository.save(address);

        return mapper.addressToDto(address);
    }

    public void deleteAddress(String addressId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);
        User user = findUserById(uuid);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(UserConstant.ADDRESS_NOT_FOUND));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException(UserConstant.ADDRESS_NOT_BELONG_TO_USER);
        }
        addressRepository.delete(address);
    }

    // public void unlinkOAuth2Account(OAuth2LinkRequest request) {
    // User user = userRepository.findByEmail(request.getEmail())
    // .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));

    // UserProvider provider = userProviderRepository.findById(request.getId())
    // .orElseThrow(() -> new AppException("Không tìm thấy liên kết OAuth2"));

    // // Optional: check xem provider này có thuộc về user đó không
    // if (!provider.getUser().getId().equals(user.getId())) {
    // throw new AppException("Liên kết không thuộc người dùng này");
    // }
    // userProviderRepository.delete(provider);
    // }

}
