package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.response.UserResponse;
import com.harrydev.ticket_management_backend.entity.User;
import com.harrydev.ticket_management_backend.exception.AppException;
import com.harrydev.ticket_management_backend.exception.ErrorCode;
import com.harrydev.ticket_management_backend.mapper.UserMapper;
import com.harrydev.ticket_management_backend.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class UserService {
    UserRepository userRepository;

    //Cách 2 truyển data từ request vào Entity (1)
    UserMapper userMapper;

    public User createUser(CreateUserRequestDTO request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Cách 1
        
        // User user = new User();
        
        // user.setUsername(request.getUsername());
        // user.setPassword(request.getPassword());
        // user.setFirstName(request.getFirstName());
        // user.setLastName(request.getLastName());
        // user.setEmail(request.getEmail());
        // user.setPhoneNumber(request.getPhoneNumber());
        // user.setRole(request.getRole());
        // user.setDateOfBirth(request.getDateOfBirth());
        // user.setGender(request.getGender());

        //Cách 2 truyển data từ request vào Entity (2)
        User user = userMapper.toUser(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); //số càng lớn => MK càng khó giải mã. Nếu quá lớn thì ảnh hưởng performance của hệ thống.
        //Tùy chỉnh số cho phù hợp với yêu cầu hệ thống (Vd: mã hóa dưới 1s)
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public UserResponse updateUser(String userId, UpdateUserRequestDTO request) {
       
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        //Cách 1
        // user.setPassword(request.getPassword());
        // user.setFirstName(request.getFirstName());
        // user.setLastName(request.getLastName());
        // user.setEmail(request.getEmail());
        // user.setPhoneNumber(request.getPhoneNumber());
        // user.setRole(request.getRole());
        // user.setDateOfBirth(request.getDateOfBirth());
        // user.setGender(request.getGender());

        //Cách 2
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
