package com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER-001", "유저가 존재하지 않습니다."),
    USER_ALREADY_EXIST(HttpStatus.CONFLICT, "USER-002", "이미 존재하는 유저입니다."),
    USER_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "USER-003", "비밀번호가 틀립니다."),
    USER_PASSWORD_FORMAT_INCORRECT(HttpStatus.BAD_REQUEST, "USER-004", "비밀번호는 8~16자 사이여야 합니다."),
    USER_PASSWORD_CHECK_INCORRECT(HttpStatus.BAD_REQUEST, "USER-005", "비밀번호 확인이 틀립니다."),
    USER_AUTH_NUMBER_INCORRECT(HttpStatus.BAD_REQUEST, "USER-006", "인증번호가 틀립니다."),
    USER_JWT_INVALID(HttpStatus.BAD_REQUEST, "USER-007", "JWT가 유효하지 않습니다."),
    USER_SCOPE_FORBIDDEN(HttpStatus.FORBIDDEN, "USER-008", "스코프 제한이 있습니다."),

    ADMIN_FORBIDDEN(HttpStatus.FORBIDDEN, "ADMIN-001", "금지된 동작입니다."),
    ADMIN_IDENTICAL_UPDATE(HttpStatus.BAD_REQUEST, "ADMIN-002", "동일한 역할로 업데이트 중입니다."),
    ADMIN_ROOT_DOWNGRADE(HttpStatus.FORBIDDEN, "ADMIN-003", "ROOT권한은 하향 조정이 불가능합니다."),

    MAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL-001", "메일 전송에 실패했습니다."),

    RFID_NOT_FOUND(HttpStatus.BAD_REQUEST, "RFID-001", "카드 등록 정보가 없습니다."),
    RFID_ALREADY_EXISTS(HttpStatus.CONFLICT, "RFID-002", "이미 사용중인 카드입니다.");

    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;			// 설명
}
