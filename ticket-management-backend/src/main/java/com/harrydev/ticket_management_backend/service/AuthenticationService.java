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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    JwtDecoder jwtDecoder;

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

    // Phương thức dùng trả về trạng thái đăng nhập + cấp access token
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        RestTemplate restTemplate = new RestTemplate(); // Khởi tạo một công cụ của Spring dùng để gọi API (gửi HTTP
                                                        // request) sang một máy chủ khác (ở đây là máy chủ Keycloak).

        // URL để lấy token
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        // đường dẫn của Keycloak chuyên dùng để xin cấp token.

        // ==================================================================================
        // Headers: Content-Type: application/x-www-form-urlencoded
        // Tạo header cho request, khai báo định dạng dữ liệu gửi đi là form
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // URL Encode là gì?

        // ==================================================================================
        // Body chứa các thông tin grant_type, client_id, client_secret, username,
        // password
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // Khởi tạo một cấu trúc dữ liệu dạng Key-Value (có thể chứa nhiều value cho 1
        // key) để làm phần Body (thân) của request.
        map.add("grant_type", "password"); // Báo cho Keycloak biết luồng xác thực đang dùng là điền trực tiếp username
                                           // và password (Resource Owner Password Credentials Grant).

        map.add("client_id", clientId);
        map.add("client_secret", clientSecret); // Bắt buộc vì ticket-app là Confidential Client
        // Cung cấp danh tính của ứng dụng backend (ticket-app) cho Keycloak để Keycloak
        // biết ai đang xin token. client_secret bắt buộc vì app được cấu hình tính bảo
        // mật cao (Confidential Client).

        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        // ===============================================
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        // Đóng gói phần Headers và Body lại với nhau thành một gói chuẩn bị đem gửi.

        // ===============================================
        try {
            // Gửi request POST đến Keycloak
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            // Thực thi việc gọi API (method POST) sang Keycloak bằng URL và gói dữ liệu
            // entity đã chuẩn bị. Đọc kết quả Keycloak trả về dưới dạng một Map.

            /*
             * ResponseEntity là gì và tại sao lại dùng Map?
             * ResponseEntity là một lớp (class) do Spring cung cấp để đại diện cho toàn bộ
             * một gói tin phản hồi HTTP (HTTP Response) từ máy chủ khác trả về.
             * => Một phản hồi HTTP không chỉ có dữ liệu, mà nó bao gồm 3 phần chính.
             * ResponseEntity giúp bạn lấy được cả 3:
             * + Status Code (Mã trạng thái): Ví dụ 200 (Thành công), 400 (Lỗi request), 500
             * (Lỗi server). Bạn có thể lấy bằng response.getStatusCode().
             * + Headers: Các thông tin siêu dữ liệu (ví dụ: loại nội dung trả về là gì).
             * + Body (Thân chứa dữ liệu): Chính là dữ liệu thực tế máy chủ trả về.
             * 
             * Tại sao dữ liệu lại dùng Map (ResponseEntity<Map>)? Khi gọi thành công,
             * Keycloak sẽ trả về dữ liệu dưới dạng JSON.
             * =? JSON bản chất là cấu trúc Key-Value (Khóa - Giá trị). Trong Java, cấu trúc
             * dữ liệu hoàn hảo nhất để chứa các cặp Key-Value ngẫu nhiên chính là Map.
             * => Dùng Map giúp bạn lấy ngay được dữ liệu (như body.get("access_token")) mà
             * không tốn công phải tạo thêm một class DTO (Data Transfer Object) mới chỉ để
             * hứng dữ liệu từ Keycloak.
             */

            /*
             * Map.class là gì?
             * Trong Java, chữ .class đặt sau tên một kiểu dữ liệu được gọi là Class
             * Literal. Nó là cách để bạn truyền "khái niệm về kiểu dữ liệu đó" như một tham
             * số. Map.class đại diện cho kiểu dữ liệu Map.
             * 
             * Tại sao bắt buộc phải truyền vào postForEntity?
             * Khi RestTemplate nhận được phản hồi từ Keycloak, dữ liệu đó ban đầu chỉ là
             * một chuỗi văn bản thô (raw JSON string). RestTemplate không đủ thông minh để
             * tự đoán xem bạn muốn biến chuỗi văn bản đó thành cái gì trong Java.
             * 
             * Việc bạn truyền tham số thứ 3 là Map.class giống như một lời chỉ thị cho
             * RestTemplate (và thư viện chuyển đổi JSON Jackson ngầm bên dưới):
             * "Này RestTemplate, hãy đọc chuỗi JSON trả về và tự động ép kiểu (convert/deserialize) nó thành một đối tượng Map của Java cho tôi nhé."
             * 
             * Nếu bạn có một class tự định nghĩa tên là KeycloakResponseDTO, bạn hoàn toàn
             * có thể thay dòng đó thành:
             * restTemplate.postForEntity(tokenUrl, entity, KeycloakResponseDTO.class);
             * Khi đó Spring sẽ tự động "nhồi" JSON vào class của bạn thay vì dùng Map.
             */

            // Nếu thành công, lấy access_token từ body của response
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                String accessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in"); // Keycloak trả về số giây

                Date expiryTime = null;
                if (expiresIn != null) {
                    // Thời gian hiện tại + số mili-giây
                    expiryTime = new Date(System.currentTimeMillis() + (expiresIn * 1000L));
                }

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .isAuthenticated(true)
                        .expiryTime(expiryTime)
                        .build();
            }
        } catch (HttpClientErrorException e) { // Bắt lỗi nếu gọi lên Keycloak bị từ chối (ví dụ: HTTP Status 401 do sai
                                               // mật khẩu).
            log.error("Lỗi xác thực với Keycloak: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);

            // HttpClientErrorException? Đây là một ngoại lệ do RestTemplate của Spring ném
            // ra khi nó gọi một API (ở đây là gọi lên Keycloak) và máy chủ đó trả về mã lỗi
            // HTTP đầu 4xx (Client Error).

        } catch (Exception e) { // Bắt các lỗi mạng khác (ví dụ: sập Keycloak, đứt mạng).
            log.error("Lỗi khi kết nối Keycloak: {}", e.getMessage());
            throw new RuntimeException("Lỗi máy chủ khi đăng nhập");
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    // Xác thực token cục bộ bằng chữ ký và các claim qua JwtDecoder.
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            jwtDecoder.decode(request.getToken()); // <==================
            return IntrospectResponse
                    .builder()
                    .valid(true)
                    .build();

        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT không hợp lệ: {}", e.getMessage());
            return IntrospectResponse.builder().valid(false).build();
        }
        // Hàm decode sẽ phân tích chuỗi token JWT, kiểm tra thời hạn (exp claim), và
        // quan trọng nhất là kiểm tra chữ ký số (signature) xem token có bị giả mạo hay
        // không. Nếu mọi thứ hợp lệ, nó trả về đối tượng Jwt. Nếu có bất kỳ lỗi gì, nó
        // sẽ ném ra ngoại lệ (Exception) ngay tại dòng này.

        /*
         * IllegalArgumentException : dịch sát nghĩa là
         * "Ngoại lệ do đối số truyền vào không hợp lệ". Nó xảy ra khi bạn ném cho một
         * hàm một tham số sai hoàn toàn về mặt vật lý/cấu trúc mà hàm đó không thể xử
         * lý được.
         */
    }

    /*
     * Xác thực Token (introspect): Là KIỂM TRA OFFLINE (Cục bộ / Local)
     * - Sau khi đăng nhập thành công và Keycloak đã cấp JWT (JSON Web Token), người
     * dùng sẽ dùng token này để truy cập các API.
     * - Khi cần kiểm tra xem token này là thật hay giả, còn hạn hay không, hàm
     * introspect sử dụng JwtDecoder để giải mã và kiểm tra chữ ký số ngay tại RAM
     * của server Backend.
     * - Lợi ích: Nó không cần gửi request qua mạng lên Keycloak để hỏi nữa. Điều
     * này giúp hệ thống chạy rất nhanh và giảm tải cho máy chủ Keycloak.
     */

    /*
     * JWKS (JSON Web Key Set) được sử dụng ở đâu? => Bạn sẽ không nhìn thấy chữ
     * JWKS nào trong đoạn code này, bởi vì Spring Security đã làm giúp bạn ở bên
     * dưới (under the hood).
     * - JWKS được sử dụng chính xác tại dòng:
     * jwtDecoder.decode(request.getToken());
     * 
     * - Cơ chế hoạt động ngầm:
     * + Trong file cấu hình (application.properties), cấu hình đường dẫn đến
     * Keycloak (ví dụ: spring.security.oauth2.resourceserver.jwt.issuer-uri=...).
     * + Nhờ cấu hình đó, Spring Boot tự động tạo ra bean JwtDecoder (thường là
     * NimbusJwtDecoder). Lúc khởi động hoặc khi nhận request đầu tiên, nó sẽ tự
     * động gọi lên Keycloak (đường dẫn /certs) để tải JWKS về và lưu vào bộ nhớ tạm
     * (cache). JWKS này chứa các Public Key (khóa công khai).
     * + Khi dòng jwtDecoder.decode(...) chạy: Nó đọc phần Header của JWT để lấy kid
     * (Key ID); Tìm trong JWKS vừa lưu ở trên xem có Public Key nào khớp với kid
     * này không.
     * Nếu có, nó dùng Public Key đó để giải mã chữ ký (Signature) ở phần cuối của
     * token. Nếu kết quả khớp với phần dữ liệu (Payload), token được xác nhận là do
     * chính Keycloak tạo ra và chưa bị ai chỉnh sửa.
     */
}
