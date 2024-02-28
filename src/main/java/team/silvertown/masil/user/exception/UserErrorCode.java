package team.silvertown.masil.user.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    INVALID_ALLOWING_MARKETING(10010000, "올바르지 않은 형식의 마케팅 동의입니다."),
    INVALID_PERSONAL_INFO_CONSENTED(10010001, "올바르지 않은 형식의 개인 정보 이용 동의입니다."),
    INVALID_LOCATION_INFO_CONSENTED(10010002, "올바르지 않은 형식의 위치 정보 이용 동의 동의입니다."),
    INVALID_UNDER_AGE_CONSENTED(10010003, "올바르지 않은 형식의 이용 영령 동의입니다."),
    INVALID_NICKNAME(10011000, "올바르지 않은 형식의 닉네임 정보입니다."),
    INVALID_SEX(10011001, "올바르지 않은 형식의 성별 정보입니다."),
    INVALID_EXERCISE_INTENSITY(10011002, "올바르지 않은 형식의 운동강도 정보입니다."),
    INVALID_HEIGHT(10012000, "올바르지 않은 형식의 키 정보입니다."),
    INVALID_WEIGHT(10012001, "올바르지 않은 형식의 몸무 정보입니다."),
    INVALID_BIRTH_DATE(10014000, "올바라즤 않은 형식의 생일 정보입니다"),
    USER_NOT_FOUND(10020401, "해당 유저를 찾을 수 없습니다."),
    AUTHORITY_NOT_FOUND(10090400, "해당 유저의 권한을 찾을 수 없습니다."),
    INVALID_OAUTH2_TOKEN(10030000, "소셜로그인 과정에서 문제가 발생했습니다."),
    INVALID_PROVIDER(10030001, "유효하지 않은 서비스 제공자입니다."),
    INVALID_JWT_TOKEN(10030100,
        "Access Denied: Authentication token was either missing or invalid.");

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
