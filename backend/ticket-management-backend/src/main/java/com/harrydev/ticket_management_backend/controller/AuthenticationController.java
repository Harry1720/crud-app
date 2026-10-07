package com.harrydev.ticket_management_backend.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import com.harrydev.ticket_management_backend.dto.request.AuthenticationRequest;
import com.harrydev.ticket_management_backend.dto.request.IntrospectRequest;
import com.harrydev.ticket_management_backend.dto.response.AuthenticationResponse;
import com.harrydev.ticket_management_backend.dto.response.IntrospectResponse;
import com.harrydev.ticket_management_backend.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        // 1. Lấy kết quả true/false từ Service
        AuthenticationResponse authenticated = authenticationService.authenticate(request);

        // 2. Trả về đúng format
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticated)
                .code(2000)
                .message("Đăng nhập thành công!")
                .build(); // Build object bao bọc bên ngoài
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {

        // Gọi Service để kiểm tra token có hợp lệ không
        IntrospectResponse result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(2000)
                .message(result.isValid() ? "Token hợp lệ" : "Token không hợp lệ hoặc đã hết hạn")
                .build();
    }
}
