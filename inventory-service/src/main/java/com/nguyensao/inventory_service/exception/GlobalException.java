package com.nguyensao.inventory_service.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nguyensao.inventory_service.dto.response.DataResponse;

@RestControllerAdvice
public class GlobalException {

    // Lỗi not tồn tại or check tồn tại
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<DataResponse<Object>> handleAppException(AppException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("App exception");
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Lỗi thiếu trường @Vail
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(exception.getBody().getDetail());

        BindingResult bindingResult = exception.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        response.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Tất cả các lỗi
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<DataResponse<Object>> handleException(RuntimeException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError("RuntimeException exception");
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Lỗi sai đường dẫn api
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<DataResponse<Object>> handleNoResourceFoundException(NoResourceFoundException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError("Không tồn tại đường dẫn này");
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Lỗi cookies
    @ExceptionHandler(value = MissingRequestCookieException.class)
    public ResponseEntity<DataResponse<Object>> handleMissingRequestCookieException(
            MissingRequestCookieException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("MissingRequestCookieException");
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Kiểm tra dữ liệu đầu vào
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<DataResponse<Object>> handleNullPointerException(NullPointerException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("NullPointerException");
        response.setMessage("Dữ liệu đầu vào bị thiếu hoặc không hợp lệ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Lỗi Enum
    @ExceptionHandler(value = JsonProcessingException.class)
    public ResponseEntity<DataResponse<Object>> handleJsonProcessingException(JsonProcessingException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("JSON parse error");
        response.setMessage("Không thể chuyển đổi chuỗi rỗng thành giá trị enum");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
