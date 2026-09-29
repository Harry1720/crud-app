package com.harrydev.ticket_management_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL) //Không trả về các giá trị null
@Getter @Setter
public class ApiResponse<T> {
    //Nơi chứa các trường mình muốn cho API
    private int code; //Mã lỗi/ thành công
    private String message;
    private T result;    //Kết quả trả về (có thể là thông tin của người dùng)
    //Kiểu trả về phụ thuộc từng API -> Kiểu Generic T
}
