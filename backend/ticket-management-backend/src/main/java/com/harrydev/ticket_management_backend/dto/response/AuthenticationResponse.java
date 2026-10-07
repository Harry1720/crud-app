package com.harrydev.ticket_management_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthenticationResponse {
    String token;
    boolean isAuthenticated;
    Date expiryTime;
}
