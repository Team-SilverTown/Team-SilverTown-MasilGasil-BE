package team.silvertown.masil.masil.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum MasilErrorCode implements ErrorCode {
    NULL_TITLE(20111000, "제목이 입력되지 않았습니다"),
    TITLE_TOO_LONG(20111001, "제목 길이가 제한을 초과했습니다"),
    THUMBNAIL_URL_TOO_LONG(20111002, "썸네일 URL 주소 길이가 제한을 초과했습니다"),

    INVALID_DISTANCE(20112000, "산책 거리는 양수여야 합니다"),
    INVALID_TOTAL_TIME(20112001, "산책 시간은 양수여야 합니다"),

    NULL_MASIL(20130000, "핀의 마실 기록을 확인할 수 없습니다"),

    NULL_USER(20190000, "마실 기록 사용자를 확인할 수 없습니다"),
    USER_NOT_FOUND(20190400, "마실 기록 사용자가 존재하지 않습니다");

    private final int code;
    private final String message;

    MasilErrorCode(int code, String message) {
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
