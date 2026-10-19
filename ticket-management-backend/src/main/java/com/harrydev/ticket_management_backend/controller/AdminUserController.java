package com.harrydev.ticket_management_backend.controller;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import com.harrydev.ticket_management_backend.dto.request.CreateStaffUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.response.UserResponse;
import com.harrydev.ticket_management_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createStaffUser(@RequestBody @Valid CreateStaffUserRequestDTO request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createStaffUser(request))
                .code(2000)
                .message("Tạo tài khoản nhân viên thành công")
                .build();
    }
}
