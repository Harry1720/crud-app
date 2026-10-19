package com.harrydev.ticket_management_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStaffUserRequestDTO extends CreateUserRequestDTO {

    @NotNull(message = "INVALID_STAFF_ROLE")
    StaffRole role;
}
