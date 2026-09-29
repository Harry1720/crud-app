package com.harrydev.ticket_management_backend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed."),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_USERNAME(1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),

    INVALID_MESSAGE_KEY(1111, "Invalid message key");

    private int code;
    private String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
