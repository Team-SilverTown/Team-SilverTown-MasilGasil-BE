package team.silvertown.masil.user.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
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
