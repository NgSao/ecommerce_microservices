package com.nguyensao.inventory_service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResponse<T> {
    int status;
    String error;
    Object message;
    T data;
}