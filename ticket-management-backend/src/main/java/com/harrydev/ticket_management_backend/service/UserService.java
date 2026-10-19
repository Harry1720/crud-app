package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.CreateStaffUserRequestDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper;

    @NonFinal
    @Value("${keycloak.realm}")
    String realm;

    Keycloak keycloak;

    public UserResponse createUser(CreateUserRequestDTO request) {
        // Public signup never accepts a role from the request.
        return createUserWithRealmRole(request, "CUSTOMER");
    }

    public UserResponse createStaffUser(CreateStaffUserRequestDTO request) {
        // StaffRole is restricted to MAKER and CHECKER; ADMIN cannot be supplied here.
        return createUserWithRealmRole(request, request.getRole().name());
    }

    private UserResponse createUserWithRealmRole(CreateUserRequestDTO request, String roleName) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // 1. Tạo thông tin User cho Keycloak
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false); // Quan trọng: Tắt temporary để không bắt đổi mật khẩu

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(request.getUsername());
        keycloakUser.setFirstName(request.getFirstName());
        keycloakUser.setLastName(request.getLastName());
        keycloakUser.setEmail(request.getEmail());
        keycloakUser.setEnabled(true);
        keycloakUser.setCredentials(Collections.singletonList(credential));

        var users = keycloak.realm(realm).users();
        String keycloakId = null;

        try {
            try (Response response = users.create(keycloakUser)) {
                if (response.getStatus() == 409) {
                    throw new AppException(ErrorCode.DUPLICATE_DATA);
                }
                if (response.getStatus() != 201) {
                    throw new RuntimeException(
                            "Lỗi tạo user trên Keycloak: " + response.getStatusInfo().getReasonPhrase());
                }
                keycloakId = CreatedResponseUtil.getCreatedId(response);
            }

            RoleRepresentation realmRole = keycloak.realm(realm)
                    .roles()
                    .get(roleName)
                    .toRepresentation();
            users.get(keycloakId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(realmRole));

            User user = userMapper.toUser(request);
            user.setKeycloakId(keycloakId);
            user.setStatus("ACTIVE");
            return userMapper.toUserResponse(userRepository.save(user));
        } catch (RuntimeException exception) {
            // Avoid leaving an account in Keycloak without its role or local profile.
            if (keycloakId != null) {
                try {
                    users.get(keycloakId).remove();
                } catch (RuntimeException cleanupException) {
                    log.error("Không thể dọn user {} khỏi Keycloak sau khi tạo thất bại", keycloakId,
                            cleanupException);
                }
            }
            throw exception;
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUser(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequestDTO request) {
       
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        //Cách 1
        // user.setPassword(request.getPassword());
        // user.setFirstName(request.getFirstName());
        // user.setLastName(request.getLastName());
        // user.setEmail(request.getEmail());
        // user.setPhoneNumber(request.getPhoneNumber());
        // user.setDateOfBirth(request.getDateOfBirth());
        // user.setGender(request.getGender());

        //Cách 2
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
