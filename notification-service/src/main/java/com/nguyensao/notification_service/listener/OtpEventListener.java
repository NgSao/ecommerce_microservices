package com.nguyensao.notification_service.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nguyensao.notification_service.kafka.OtpEvent;
import com.nguyensao.notification_service.service.EmailService;

@Component
public class OtpEventListener {
    private final EmailService emailService;

    public OtpEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "otp-events", groupId = "otp-group")
    public void listenOtpEvents(OtpEvent event) {
        switch (event.getEventType()) {
            case REGISTER_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case VERIFY_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case FORGOT_PASSWORD:
                emailService.sendVerificationPassword(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case RESET_PASSWORD:
                emailService.sendPasswordResetConfirmation(event.getFullName(), event.getEmail());
                break;
            default:
                break;
        }
    }
}
