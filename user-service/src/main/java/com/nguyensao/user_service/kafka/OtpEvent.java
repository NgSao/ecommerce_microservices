package com.nguyensao.user_service.kafka;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpEvent {
    EventEnum eventType;
    String fullName;
    String email;
    String otp;

}
