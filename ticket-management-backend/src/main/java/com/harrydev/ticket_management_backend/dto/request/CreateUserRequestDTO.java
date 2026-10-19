package com.harrydev.ticket_management_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CreateUserRequestDTO {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, message = "INVALID_USERNAME")
    String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    @Size(max = 50, message = "Tên không được vượt quá 50 ký tự")
    String firstName;

    @Size(max = 50, message = "Họ không được vượt quá 50 ký tự")
    String lastName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    String email;

    @NotBlank(message = "INVALID_PHONE_NUMBER")
    @Pattern(regexp = "^(0[35789][0-9]{8}|\\+84[35789][0-9]{8})$", message = "INVALID_PHONE_NUMBER")
    String phoneNumber;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    LocalDate dateOfBirth;

    String gender;

}
