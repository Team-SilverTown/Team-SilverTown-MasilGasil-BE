package team.silvertown.masil.auth.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    INVALID_OAUTH2_TOKEN(10030000, "소셜로그인 과정에서 문제가 발생했습니다."),
    INVALID_PROVIDER(10030001, "유효하지 않은 서비스 제공자입니다."),
    INVALID_PROVIDER_ID(10011001, "유효하지 않은 provider id 입니다."),
    REFRESH_TOKEN_NOT_FOUND(10020402, "해당 refresh token을 찾을 수 없습니다."),
    INVALID_JWT_TOKEN(10030100,
        "Access Denied: Authentication token was either missing or invalid.");

    private final int code;
    private final String message;

    AuthErrorCode(int code, String message) {
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
