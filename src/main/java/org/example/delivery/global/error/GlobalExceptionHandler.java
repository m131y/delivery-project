package org.example.delivery.global.error;

import lombok.extern.slf4j.Slf4j;
import org.example.delivery.global.error.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스에서 throw new BusinessException(ErrorCode.XXX) 로 던진 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {} - {}", e.getErrorCode(), e.getMessage());
        return toResponse(e.getErrorCode(), e.getMessage());
    }

    // @Valid 검증 실패: 첫 번째 필드의 메시지(DTO 에 적은 message)를 내려준다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        return toResponse(ErrorCode.INVALID_INPUT_VALUE, message);
    }

    // JSON 형식 오류, enum 에 없는 값(예: role 에 "ADMIN") 등 요청 본문을 읽을 수 없는 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadableException(HttpMessageNotReadableException e) {
        return toResponse(ErrorCode.INVALID_INPUT_VALUE, "요청 본문 형식이 올바르지 않습니다.");
    }

    // PathVariable 타입 불일치 (예: /api/menus/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return toResponse(ErrorCode.INVALID_INPUT_VALUE, e.getName() + " 값의 형식이 올바르지 않습니다.");
    }

    // @PreAuthorize 등 메서드 보안에서 거부된 경우. 없으면 아래 Exception 핸들러에 걸려 500 이 된다.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return toResponse(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
    }

    // 존재하지 않는 URL. 없으면 아래 Exception 핸들러에 걸려 500 이 된다.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        return toResponse(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
    }

    // 지원하지 않는 HTTP 메서드 (예: GET 만 있는 경로에 DELETE 요청)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return toResponse(ErrorCode.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }

    // 위에서 처리하지 못한 예외. 내부 메시지는 응답에 노출하지 않고 로그로만 남긴다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return toResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, message));
    }
}
