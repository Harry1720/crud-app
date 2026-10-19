package com.harrydev.ticket_management_backend.exception;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;


@ControllerAdvice
@Slf4j

public class GlobalExceptionHandler {

    // @ExceptionHandler(value = RuntimeException.class) //Khi có một exception kiểu
    // RuntimeException ở bất cứ nơi nào trong hệ thống thì đều tập trung về đây và
    // ta sẽ xử lý
    // ResponseEntity<String> handlingRuntimeException(RuntimeException exception){
    // return ResponseEntity.badRequest().body(exception.getMessage());
    // }

    // @ExceptionHandler(value = RuntimeException.class)
    // ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
    //     ApiResponse apiResponse = new ApiResponse();
    //     apiResponse.setCode(1001);
    //     apiResponse.setMessage(exception.getMessage());
    //     return ResponseEntity.badRequest().body(apiResponse);
    // }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        if (errorCode == ErrorCode.DUPLICATE_DATA
                || errorCode == ErrorCode.USERNAME_EXISTED
                || errorCode == ErrorCode.EMAIL_EXISTED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        }

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // @ExceptionHandler(value = MethodArgumentNotValidException.class)
    // ResponseEntity<String> handlingValidation(MethodArgumentNotValidException
    // exception){
    // return
    // ResponseEntity.badRequest().body(exception.getFieldError().getDefaultMessage());
    // }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            errorCode = ErrorCode.INVALID_MESSAGE_KEY;
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //Bắt lỗi ràng buộc DB để dự phòng
    //Kiểm tra trước không loại bỏ hoàn toàn lỗi trùng: hai request đồng thời có thể cùng vượt qua bước kiểm tra.
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse> handlingDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        ApiResponse response = new ApiResponse();
        response.setCode(ErrorCode.DUPLICATE_DATA.getCode());
        response.setMessage(ErrorCode.DUPLICATE_DATA.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Cấu hình để nhận các Exception không thuộc các loại trên
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingUncategorizedException(Exception exception) {
        log.error("Lỗi không được xử lý khi gọi API", exception);

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
