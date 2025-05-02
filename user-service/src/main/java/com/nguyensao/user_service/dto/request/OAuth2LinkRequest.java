package com.nguyensao.user_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2LinkRequest {
    private String email;
    private Long id;
}
