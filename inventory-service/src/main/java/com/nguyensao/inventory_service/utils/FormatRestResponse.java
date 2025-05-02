package com.nguyensao.inventory_service.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nguyensao.inventory_service.annotation.AppMessage;
import com.nguyensao.inventory_service.dto.response.DataResponse;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(Object arg0, MethodParameter arg1, MediaType arg2,
            Class<? extends HttpMessageConverter<?>> arg3, ServerHttpRequest arg4, ServerHttpResponse arg5) {
        // TODO Auto-generated method stub
        HttpServletResponse response = ((ServletServerHttpResponse) arg5).getServletResponse();
        int status = response.getStatus();
        DataResponse<Object> restResponse = new DataResponse<>();
        restResponse.setStatus(status);
        String path = arg4.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return arg0;
        }

        if (arg0 instanceof String || arg0 instanceof Resource) {
            return arg0;
        }
        if (status >= 400) {
            return arg0;
        } else {
            restResponse.setData(arg0);
            AppMessage message = arg1.getMethodAnnotation(AppMessage.class);
            restResponse.setMessage(message != null ? message.value() : "API call successfully");
        }
        return restResponse;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // TODO Auto-generated method stub
        return true;
    }

}