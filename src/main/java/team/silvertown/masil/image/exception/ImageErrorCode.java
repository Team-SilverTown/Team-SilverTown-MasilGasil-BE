package team.silvertown.masil.image.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum ImageErrorCode implements ErrorCode {
    NOT_SUPPORTED_CONTENT(16000, "지원하지 않는 파일 형식 입니다");

    private static final int PREFIX = 500_00000;

    private final int code;
    private final String message;

    ImageErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return PREFIX + this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
