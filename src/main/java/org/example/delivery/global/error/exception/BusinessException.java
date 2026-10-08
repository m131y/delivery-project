package org.example.delivery.global.error.exception;

import lombok.Getter;
import org.example.delivery.global.error.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 같은 ErrorCode 를 쓰되 상황에 맞는 메시지를 따로 내려주고 싶을 때 사용
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
