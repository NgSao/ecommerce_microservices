package com.nguyensao.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyensao.user_service.dto.response.DataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        DataResponse<Object> res = new DataResponse<>();
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setMessage("Bạn không có quyền truy cập tài nguyên này.");
        res.setError(accessDeniedException.getMessage());
        response.getWriter().write(mapper.writeValueAsString(res));
    }
}
