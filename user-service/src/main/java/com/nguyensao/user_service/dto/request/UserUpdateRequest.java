package com.nguyensao.user_service.dto.request;

import java.time.Instant;

import com.nguyensao.user_service.enums.UserGender;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String id;
    String fullname;
    String email;
    String profileImageUrl;
    Instant birthday;
    UserGender gender;
}