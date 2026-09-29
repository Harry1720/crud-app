package com.harrydev.ticket_management_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UpdateUserRequestDTO {

    private String password;
    private String firstName;
    private String lastName;
    @Email
    @NotBlank()
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String role;
}
