package org.example.delivery.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    OWNER_ONLY(HttpStatus.FORBIDDEN, "사장님만 이용할 수 있습니다."),
    CUSTOMER_ONLY(HttpStatus.FORBIDDEN, "고객님만 이용할 수 있습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 경로를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "로그인 정보를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 아이디 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임 입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 ID와 비밀번호가 유효하지 않습니다."),

    // 메뉴
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    MENU_DELETED(HttpStatus.NOT_FOUND, "삭제된 메뉴 입니다."),
    MENU_NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 메뉴만 수정, 삭제할 수 있습니다."),

    // 주문
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 주문만 처리할 수 있습니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.CONFLICT, "주문 요청 상태일 때만 취소가 가능합니다."),
    ORDER_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.CONFLICT, "결제 완료, 주문 수락 상태일 때만 변경이 가능합니다."),

    // 결제
    PAYMENT_NOT_ALLOWED(HttpStatus.CONFLICT, "주문 요청 상태일 때만 결제가 가능합니다."),
    UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "현재 결제는 카드만 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
