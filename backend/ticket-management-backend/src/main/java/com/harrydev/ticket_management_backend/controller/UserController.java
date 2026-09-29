package com.harrydev.ticket_management_backend.controller;

import com.harrydev.ticket_management_backend.dto.request.ApiResponse;
import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.entity.User;
import com.harrydev.ticket_management_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin(origins = "http://localhost:4200") // ? Công dụng ==> chuyển qua config
@RestController
@RequestMapping("/api/users") // Khai báo cái này thì các phương thức ở dưới không cần khai báo enpoint nữa,
                              // chỉ viết phương thức
public class UserController {
    @Autowired
    private UserService userService;

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
    public User getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody UpdateUserRequestDTO request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "Delete user successfully!";
    }
}
