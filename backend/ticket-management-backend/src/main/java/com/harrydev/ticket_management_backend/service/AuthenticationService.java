package com.harrydev.ticket_management_backend.service;

import com.harrydev.ticket_management_backend.dto.request.AuthenticationRequest;
import com.harrydev.ticket_management_backend.dto.request.IntrospectRequest;
import com.harrydev.ticket_management_backend.dto.response.AuthenticationResponse;
import com.harrydev.ticket_management_backend.dto.response.IntrospectResponse;
import com.harrydev.ticket_management_backend.exception.AppException;
import com.harrydev.ticket_management_backend.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    @NonFinal
    @Value("${keycloak.auth-server-url}")
    String serverUrl;

    @NonFinal
    @Value("${keycloak.realm}")
    String realm;

    @NonFinal
    @Value("${keycloak.client-id}")
    String clientId;

    @NonFinal
    @Value("${keycloak.client-secret}")
    String clientSecret;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        
        RestTemplate restTemplate = new RestTemplate();
        
        // URL để lấy token
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        // Headers: Content-Type: application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Body chứa các thông tin grant_type, client_id, client_secret, username, password
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret); // Bắt buộc vì ticket-app là Confidential Client
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            // Gửi request POST đến Keycloak
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            
            // Nếu thành công, lấy access_token từ body của response
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                String accessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in"); // Keycloak trả về số giây

                java.util.Date expiryTime = null;
                if (expiresIn != null) {
                    // Thời gian hiện tại + số mili-giây
                    expiryTime = new java.util.Date(System.currentTimeMillis() + (expiresIn * 1000L));
                }

                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .isAuthenticated(true)
                        .expiryTime(expiryTime)
                        .build();
            }
        } catch (HttpClientErrorException e) {
            log.error("Lỗi xác thực với Keycloak: {}", e.getMessage());
            // Lỗi mật khẩu hoặc username sai sẽ ném ra 401 Unauthenticated
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } catch (Exception e) {
            log.error("Lỗi khi kết nối Keycloak: {}", e.getMessage());
            throw new RuntimeException("Lỗi máy chủ khi đăng nhập");
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    // Xác thực Token: Gọi Keycloak Introspection Endpoint để kiểm tra token có hợp lệ không
    public IntrospectResponse introspect(IntrospectRequest request) {
        
        RestTemplate restTemplate = new RestTemplate();

        // URL Introspection chuẩn OAuth2 của Keycloak
        String introspectUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Body: gửi client_id, client_secret và token cần kiểm tra
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("token", request.getToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            // Gọi sang Keycloak để kiểm tra token
            ResponseEntity<Map> response = restTemplate.postForEntity(introspectUrl, entity, Map.class);

            Map<String, Object> body = response.getBody();
            if (body != null) {
                // Keycloak trả về trường "active": true nếu token hợp lệ và chưa hết hạn
                Boolean active = (Boolean) body.get("active");
                return IntrospectResponse.builder()
                        .valid(active != null && active)
                        .build();
            }
        } catch (Exception e) {
            log.error("Lỗi khi introspect token với Keycloak: {}", e.getMessage());
        }

        // Mặc định trả về invalid nếu có lỗi
        return IntrospectResponse.builder()
                .valid(false)
                .build();
    }
}
