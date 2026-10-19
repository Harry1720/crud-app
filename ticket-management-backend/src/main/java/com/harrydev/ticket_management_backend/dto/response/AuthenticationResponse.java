package com.harrydev.ticket_management_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthenticationResponse {
    @JsonProperty("access_token")
    String accessToken;
    String refreshToken;
    boolean isAuthenticated;
    Date expiryTime;
}
