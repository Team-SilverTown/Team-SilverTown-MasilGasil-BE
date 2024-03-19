package team.silvertown.masil.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.silvertown.masil.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_OAUTH2_TOKEN(10130000, "소셜로그인 과정에서 문제가 발생했습니다."),
    INVALID_PROVIDER(10130001, "유효하지 않은 서비스 제공자입니다."),
    INVALID_PROVIDER_ID(10111001, "유효하지 않은 provider id 입니다."),
    REFRESH_TOKEN_NOT_FOUND(10120402, "해당 refresh token을 찾을 수 없습니다."),
    INVALID_JWT_TOKEN(10130100,
        "Access Denied: Authentication token was either missing or invalid.");

    private final int code;
    private final String message;

}
