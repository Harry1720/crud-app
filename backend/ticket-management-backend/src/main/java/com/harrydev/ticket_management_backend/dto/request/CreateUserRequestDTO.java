package com.harrydev.ticket_management_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)

public class CreateUserRequestDTO {

    @Size(min = 3, message = "INVALID_USERNAME")
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    String firstName;
    String lastName;

    @Email
    @NotBlank
    String email;

    String phoneNumber;
    LocalDate dateOfBirth;
    String gender;
    String role;

}
