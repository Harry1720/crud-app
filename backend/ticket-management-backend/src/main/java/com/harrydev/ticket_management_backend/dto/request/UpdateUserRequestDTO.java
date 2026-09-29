package com.harrydev.ticket_management_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

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
