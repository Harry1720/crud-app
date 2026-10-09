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

public class UpdateUserRequestDTO {

    @Size(max = 50, message = "Tên không được vượt quá 50 ký tự")
    String firstName;

    @Size(max = 50, message = "Họ không được vượt quá 50 ký tự")
    String lastName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    String email;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    String phoneNumber;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    LocalDate dateOfBirth;

    String gender;
    String role;
}
