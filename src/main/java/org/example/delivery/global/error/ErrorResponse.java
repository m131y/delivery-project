package org.example.delivery.global.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.name())
                .message(message)
                .build();
    }
}
