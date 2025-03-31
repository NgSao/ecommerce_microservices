package com.nguyensao.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponse {
    @JsonProperty("access_token")
    private String accessToken;

    private String email;
}
