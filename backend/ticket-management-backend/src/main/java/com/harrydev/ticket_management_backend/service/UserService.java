package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.response.UserResponse;
import com.harrydev.ticket_management_backend.entity.User;
import com.harrydev.ticket_management_backend.exception.AppException;
import com.harrydev.ticket_management_backend.exception.ErrorCode;
import com.harrydev.ticket_management_backend.mapper.UserMapper;
import com.harrydev.ticket_management_backend.repository.UserRepository;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.experimental.NonFinal;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
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

    @NonFinal
    @Value("${keycloak.realm}")
    String realm;

    org.keycloak.admin.client.Keycloak keycloak;

    public UserResponse createUser(CreateUserRequestDTO request) {
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 1. Tạo thông tin User cho Keycloak
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(org.keycloak.representations.idm.CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false); // Quan trọng: Tắt temporary để không bắt đổi mật khẩu

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(request.getUsername());
        keycloakUser.setFirstName(request.getFirstName());
        keycloakUser.setLastName(request.getLastName());
        keycloakUser.setEmail(request.getEmail());
        keycloakUser.setEnabled(true);
        keycloakUser.setCredentials(java.util.Collections.singletonList(credential));

        // 2. Gọi API tạo User trên Keycloak
        Response response = keycloak.realm(realm).users().create(keycloakUser);
        
        if (response.getStatus() != 201) {
            throw new RuntimeException("Lỗi tạo user trên Keycloak: " + response.getStatusInfo().getReasonPhrase());
        }

        // 3. (Tuỳ chọn) Lưu vào Local DB (PostgreSQL)
        User user = userMapper.toUser(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); 
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
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
