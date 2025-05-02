package com.nguyensao.notification_service.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nguyensao.notification_service.constant.EmailConstant;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Async
    public void sendVerificationEmail(String fullName, String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực");

            long otpTokenExpirationNew = EmailConstant.EXPIRATION_OTP / 60;

            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile gửi đến quý khách mã xác thực tài khoản.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>MÃ XÁC THỰC</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Mã xác thực của bạn là: <strong style='font-size: 20px; color: #d70018;'>"
                    + code + "</strong></p>" +
                    "<p style='font-size: 14px;'>Mã này sẽ hết hạn sau " + otpTokenExpirationNew
                    + " phút. Vui lòng nhập mã trong thời gian quy định.</p>" +
                    "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email xác thực: " + e.getMessage());
        }
    }

    @Async
    public void sendVerificationPassword(String fullName, String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Cập nhật mật khẩu mới");
            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile gửi đến quý kháchmật khẩu mới để đăng nhập tài khoản.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>MẬT KHẨU MỚI</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Mật khẩu mới của bạn là: <strong style='font-size: 20px; color: #d70018;'>"
                    + newPassword + "</strong></p>" +
                    "<p style='font-size: 14px;'>Vui lòng đổi mật khẩu mới để đảm bảo an toàn hơn.</p>" +
                    "<p style='font-size: 12px; color: #888;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                    +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email mật khẩu mới: " + e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetConfirmation(String fullName, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt lại mật khẩu thành công");

            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f9f9f9; line-height: 1.6; margin: 0; padding: 20px; }"
                    +
                    ".header { background-color: #d70018; padding: 10px; text-align: center; }" +
                    ".container { max-width: 800px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 20px; }"
                    +
                    ".fotterne { background-color: #d70018; padding: 10px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div style='max-width:800px;margin: auto;'>" +
                    "<div class='header'>" +
                    "<h1 style='color: #FFF'>MinhTuan <span style='color:#000'>Mobile</span></h1>" +
                    "</div>" +
                    "<div class='container'>" +
                    "<p>Kính chào " + fullName
                    + ",<br>MinhTuan Mobile thông báo quý khách đã đặt lại mật khẩu thành công.</p>" +
                    "<div style='margin-bottom: 30px;'>" +
                    "<div style='border-bottom: 3px solid #d70018;'>" +
                    "<h3 style='color: #d70018;'>XÁC NHẬN ĐẶT LẠI MẬT KHẨU</h3>" +
                    "</div>" +
                    "<p style='font-size: 16px;'>Bạn đã đặt lại mật khẩu thành công!</p>" +
                    "<p style='font-size: 14px;'>Vui lòng đăng nhập bằng mật khẩu mới và thay đổi mật khẩu nếu cần.</p>"
                    +
                    "<p style='font-size: 14px;'>Nếu bạn không thực hiện thao tác này, hãy liên hệ ngay với bộ phận hỗ trợ qua số <span style='color:#d70018;'>0392445255</span>.</p>"
                    +
                    "<p style='font-size: 12px; color: #888;'>Email này được gửi tự động, vui lòng không trả lời.</p>" +
                    "</div>" +
                    "<div style='text-align: center; margin-top: 40px'>" +
                    "<p>Chúc bạn luôn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ tại MinhTuan Mobile.</p>" +
                    "<p>Tổng đài hỗ trợ miễn phí: <span style='color:#d70018;'>0392445255</span></p>" +
                    "<p>MinhTuan Mobile cảm ơn quý khách.</p>" +
                    "</div>" +
                    "</div>" +
                    "<div class='fotterne'></div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email xác nhận đặt lại mật khẩu: " + e.getMessage());
        }
    }
}