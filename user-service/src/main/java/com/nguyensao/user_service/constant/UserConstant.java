package com.nguyensao.user_service.constant;

public class UserConstant {

    // OTP
    public static final long EXPIRATION_OTP = 3 * 60;
    public static final String VERIFY_CODE_SENT = "Vui lòng kiểm tra mã xác thực trong Gmail.";
    public static final String VERIFY_SUCCESS = "Xác thực thành công! Tài khoản đã được kích hoạt.";
    public static final String OTP_NOT_FOUND = "Không tìm thấy mã OTP cho email này.";
    public static final String OTP_EXPIRED = "Mã OTP đã hết hạn.";
    public static final String OTP_INVALID_REMAINING_ATTEMPTS = "Mã OTP không đúng. Bạn còn %d lần thử.";
    public static final String OTP_RETRY_LIMIT_EXCEEDED = "Bạn đã nhập sai quá 3 lần. Vui lòng kích hoạt lại tài khoản.";
    public static final String USER_NOT_FOUND = "Không tìm thấy người dùng.";

    // Password
    public static final String PASSWORD_REQUIREMENTS_MESSAGE = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.";
    public static final String VERIFY_PASSWORD_RESET_SUCCESS = "Xác thực thành công! Vui lòng kiểm tra email để nhận mật khẩu mới.";
    public static final String PASSWORD_OLD_INCORRECT = "Mật khẩu cũ không chính xác.";
    public static final String PASSWORD_NEW_SAME_AS_OLD = "Mật khẩu mới không được trùng với mật khẩu cũ.";
    public static final String PASSWORD_NEW_MISMATCH = "Mật khẩu mới không khớp.";
    // Email
    public static final String EMAIL_ALREADY_USED = "Email đã được sử dụng. Vui lòng chọn email khác.";
    public static final String EMAIL_NOT_FOUND = "Email không tồn tại.";

    // Account
    public static final String ACCOUNT_INACTIVE = "Tài khoản đã tồn tại nhưng chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt hoặc yêu cầu gửi lại OTP.";
    public static final String ACCOUNT_LOCKED = "Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.";
    public static final String ACCOUNT_ACTIVE = "Tài khoản đã được xác thực trước đó.";

    // Register
    public static final String REGISTER_SUCCESS = "Đăng kí tài khoản thành công.";

    // Kafka
    public static final String KAFKA_EVENT = "otp-events";

    public static final String DELETE_NOT_USERS = "Không có người dùng nào để xóa.";

    // Address
    public static final String ADDRESS_NOT_FOUND = "Không tìm thấy địa chỉ.";
    public static final String ADDRESS_NOT_BELONG_TO_USER = "Địa chỉ không thuộc về người dùng.";
    public static final String ADDRESS_NOT_BELONG_TO_USER_OR_USER_NOT_LINKED = "Địa chỉ không thuộc về người dùng hoặc người dùng không được liên kết.";

    // jwt
    public static final String UUID = "uuid";

}
