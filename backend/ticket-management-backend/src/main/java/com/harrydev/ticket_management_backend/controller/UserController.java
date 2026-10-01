package com.harrydev.ticket_management_backend.controller;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.response.UserResponse;
import com.harrydev.ticket_management_backend.entity.User;
import com.harrydev.ticket_management_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin(origins = "http://localhost:4200") // ? Công dụng ==> chuyển qua config
@RestController
@RequestMapping("/api/users") // Khai báo cái này thì các phương thức ở dưới không cần khai báo enpoint nữa,
                              // chỉ viết phương thức
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)

public class UserController {
    UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid CreateUserRequestDTO request) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.createUser(request));
        apiResponse.setCode(1000);
        apiResponse.setMessage("Thêm user thành công");

        return apiResponse;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequestDTO request) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.updateUser(userId, request));
        apiResponse.setCode(1010);
        apiResponse.setMessage("Cập nhật user thành công");

        return apiResponse;
    }

    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1009);
        apiResponse.setMessage("Delete user successfully!");
        return apiResponse;
    }
}
