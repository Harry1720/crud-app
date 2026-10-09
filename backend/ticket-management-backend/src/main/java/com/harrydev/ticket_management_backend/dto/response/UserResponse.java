package com.harrydev.ticket_management_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserResponse {
    Long id;
    String keycloakId;
    String username;
    String fullName;
    String status;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    LocalDate dateOfBirth;
    String gender;
    String role;
}
