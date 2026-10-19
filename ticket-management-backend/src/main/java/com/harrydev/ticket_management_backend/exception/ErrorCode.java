package com.harrydev.ticket_management_backend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_EXISTED(1001, "Username existed."),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_USERNAME(1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),

    INVALID_PHONE_NUMBER(1009, "Số điện thoại không đúng định dạng"),

    UNAUTHENTICATED(1005, "Authentication failed"),

    EMAIL_EXISTED(1007, "Email đã được sử dụng"),

    DUPLICATE_DATA(1008, "Username hoặc email đã được sử dụng"),

    INVALID_STAFF_ROLE(1010, "Role nhân viên chỉ được là MAKER hoặc CHECKER"),

    INVALID_MESSAGE_KEY(1111, "Invalid message key");

    private int code;
    private String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
