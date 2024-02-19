package team.silvertown.masil.common.map;

import team.silvertown.masil.common.exception.ErrorCode;

public enum MapErrorCode implements ErrorCode {
    BLANK_DEPTH1(20011000, "지역 1 Depth가 입력되지 않았습니다"),
    NULL_DEPTH2(20011001, "지역 2 Depth는 Null이 될 수 없습니다"),
    BLANK_DEPTH3(20011002, "지역 3 Depth가 입력되지 않았습니다"),

    NULL_KAKAO_POINT(20016001, "입력된 포인트가 NULL이 될 수 없습니다"),
    INSUFFICIENT_PATH_POINTS(20016002, "포인트 수가 부족해 경로를 생성할 수 없습니다");

    private final int code;
    private final String message;

    MapErrorCode(int code, String message) {
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
