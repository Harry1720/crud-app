package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.AuthenticationRequest;
import com.harrydev.ticket_management_backend.exception.AppException;
import com.harrydev.ticket_management_backend.exception.ErrorCode;
import com.harrydev.ticket_management_backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuthenticationService {
    UserRepository userRepository;

    public boolean authenticate(AuthenticationRequest request) {
        
        //Lấy thông tin user từ DB => truy xuất password để so sánh với password người dùng cung cấp khi đăng nhập
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
