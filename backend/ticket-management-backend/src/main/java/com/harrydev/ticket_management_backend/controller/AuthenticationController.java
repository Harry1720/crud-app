package com.harrydev.ticket_management_backend.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import com.harrydev.ticket_management_backend.dto.request.AuthenticationRequest;
import com.harrydev.ticket_management_backend.dto.response.AuthenticationResponse;
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
        boolean isAuthenticated = authenticationService.authenticate(request);

        // 2. Trả về đúng format
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .isAuthenticated(isAuthenticated) // Build object bên trong
                        .build())
                .code(2000)
                .message("Đăng nhập thành công!")
                .build(); // Build object bao bọc bên ngoài
    }
}
