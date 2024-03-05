package team.silvertown.masil.mate.exception;

import team.silvertown.masil.common.exception.ErrorCode;

public enum MateErrorCode implements ErrorCode {
    BLANK_TITLE(30011000, "메이트 모집 제목이 입력되지 않았습니다"),
    TITLE_TOO_LONG(30011001, "메이트 모집 제목 길이가 제한을 초과했습니다"),
    BLANK_CONTENT(30011002, "메이트 모집 내용이 입력되지 않았습니다"),
    BLANK_DETAIL(30011003, "메이트 모집 장소 상세정보가 입력되지 않았습니다"),
    DETAIL_TOO_LONG(30011004, "메이트 모집 장소 상세정보 길이가 제한을 초과했습니다"),
    BLANK_STATUS(30011005, "메이트 참여 상태가 입력되지 않았습니다"),
    INVALID_STATUS(30011005, "올바르지 않은 메이트 참여 상태입니다"),

    NULL_CAPACITY(30012000, "메이트 모집 인원을 확인할 수 없습니다"),
    NON_POSITIVE_CAPACITY(30012001, "메이트 모집 인원은 양수여야 합니다"),
    CAPACITY_TOO_LARGE(30012002, "메이트 모집 인원이 너무 많습니다"),

    GATHER_AT_PAST(30014000, "메이트 모집 시간은 현재 이후여야 합니다"),

    NULL_MATE(30020001, "해당 참여자의 메이트를 확인할 수 없습니다"),

    NULL_POST(30030000, "메이트 모집의 산책로 포스트를 확인할 수 없습니다"),

    NULL_AUTHOR(30090000, "메이트 모집의 작성자를 확인할 수 없습니다"),
    NULL_USER(30090001, "해당 메이트 참여자를 확인할 수 없습니다");

    private final int code;
    private final String message;

    MateErrorCode(int code, String message) {
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
