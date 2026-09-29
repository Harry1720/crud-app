package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.entity.User;
import com.harrydev.ticket_management_backend.exception.AppException;
import com.harrydev.ticket_management_backend.exception.ErrorCode;
import com.harrydev.ticket_management_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(CreateUserRequestDTO request) {
        User user = new User();

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Cách 1
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String id) {    
        return userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateUser(String userId, UpdateUserRequestDTO request) {
        User user = getUser(userId);
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());

        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
