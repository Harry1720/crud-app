package com.harrydev.ticket_management_backend.exception;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

    // Cấu hình để nhận các Exception không thuộc các loại trên
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingUncategorizedException() {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
