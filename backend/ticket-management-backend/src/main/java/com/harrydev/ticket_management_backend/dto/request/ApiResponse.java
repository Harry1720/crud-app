package com.harrydev.ticket_management_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)

@JsonInclude(JsonInclude.Include.NON_NULL) // Không trả về các giá trị null

public class ApiResponse<T> {
    // Nơi chứa các trường mình muốn cho API
    int code; // Mã lỗi/ thành công
    String message;
    T result; // Kết quả trả về (có thể là thông tin của người dùng)
    // Kiểu trả về phụ thuộc từng API -> Kiểu Generic T
}
