package com.nguyensao.user_service.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nguyensao.user_service.constant.SecurityConstant;
import com.nguyensao.user_service.exception.AppException;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Async
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực đăng ký");
            long otpTokenExpirationNew = SecurityConstant.EXPIRATION_OTP / 60;

            String htmlContent = "<html><body>"
                    + "<h2 style='color: #4CAF50;'>Mã xác thực đăng ký</h2>"
                    + "<p style='font-size: 16px;'>Mã xác thực của bạn là: <strong style='font-size: 20px; color: #FF5722;'>"
                    + code + "</strong></p>"
                    + "<p style='font-size: 14px;'>Mã này sẽ hết hạn sau " + otpTokenExpirationNew
                    + " phút. Vui lòng nhập mã trong thời gian quy định.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {

        }
    }

    @Async
    public void sendVerificationPassword(String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Cập nhật mật khẩu mới");
            String htmlContent = "<html><body>"
                    + "<h2 style='color: #4CAF50;'>Mật khẩu mới của bạn</h2>"
                    + "<p style='font-size: 16px;'>Mật khẩu mới của bạn là: <strong style='font-size: 20px; color: #FF5722;'> "
                    + newPassword + " </strong></p>"
                    + "<p style='font-size: 14px;'>Vui lòn đổi mã pin mới để an toàn hơn.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {

        }
    }

    @Async
    public void sendPasswordResetConfirmation(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt lại mật khẩu thành công");

            String htmlContent = "<html><body>"
                    + "<h2 style='color: #4CAF50;'>Bạn đã đặt lại mật khẩu thành công!</h2>"
                    + "<p style='font-size: 16px;'>Nếu bạn không thực hiện thao tác này, hãy liên hệ ngay với bộ phận hỗ trợ.</p>"
                    + "<p style='font-size: 14px;'>Vui lòng đăng nhập bằng mật khẩu mới và thay đổi mật khẩu nếu cần.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #888;'>Email này được gửi tự động, vui lòng không trả lời.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppException("Lỗi khi gửi email xác nhận đặt lại mật khẩu.");
        }
    }

}
