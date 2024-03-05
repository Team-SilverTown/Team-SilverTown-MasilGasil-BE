package team.silvertown.masil.user.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    INVALID_OAUTH2_TOKEN(10030000, "소셜로그인 과정에서 문제가 발생했습니다."),
    INVALID_PROVIDER(10030001, "유효하지 않은 서비스 제공자입니다."),
    AUTHORITY_NOT_FOUND(10090400, "해당 유저의 권한을 찾을 수 없습니다."),
    INVALID_JWT_TOKEN(10030100, "Access Denied: Authentication token was either missing or invalid."),
    DUPLICATED_NICKNAME(10020900, "이미 존재하는 닉네임입니다.");

    private final int code;
    private final String message;

    UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
