package team.silvertown.masil.mate.exception;

import lombok.RequiredArgsConstructor;
import team.silvertown.masil.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum MateErrorCode implements ErrorCode {
    BLANK_TITLE(11000, "메이트 모집 제목이 입력되지 않았습니다"),
    TITLE_TOO_LONG(11001, "메이트 모집 제목 길이가 제한을 초과했습니다"),
    BLANK_CONTENT(11002, "메이트 모집 내용이 입력되지 않았습니다"),
    BLANK_DETAIL(11003, "메이트 모집 장소 상세정보가 입력되지 않았습니다"),
    DETAIL_TOO_LONG(11004, "메이트 모집 장소 상세정보 길이가 제한을 초과했습니다"),
    BLANK_STATUS(11005, "메이트 참여 상태가 입력되지 않았습니다"),
    INVALID_STATUS(11006, "올바르지 않은 메이트 참여 상태입니다"),
    MESSAGE_TOO_LONG(11007, "메세지가 너무 깁니다"),

    NULL_CAPACITY(12000, "메이트 모집 인원을 확인할 수 없습니다"),
    NON_POSITIVE_CAPACITY(12001, "메이트 모집 인원은 양수여야 합니다"),
    CAPACITY_TOO_LARGE(12002, "메이트 모집 인원이 너무 많습니다"),

    GATHER_AT_PAST(14000, "메이트 모집 시간은 현재 이후여야 합니다"),

    NULL_MATE(20000, "해당 참여자의 메이트를 확인할 수 없습니다"),
    MATE_NOT_FOUND(20400, "해당 아이디의 메이트가 존재하지 않습니다"),

    PARTICIPATING_AROUND_SIMILAR_TIME(30000, "비슷한 시간대에 참여하는 메이트가 있습니다"),

    NULL_POST(40000, "메이트 모집의 산책로 포스트를 확인할 수 없습니다"),
    POST_NOT_FOUND(40400, "기존의 산책로 포스트를 찾을 수 없습니다"),

    NULL_AUTHOR(90000, "메이트 모집의 작성자를 확인할 수 없습니다"),
    NULL_USER(90001, "해당 메이트 참여자를 확인할 수 없습니다"),
    USER_NOT_FOUND(90400, "사용자가 존재하지 않습니다");

    private static final int MATE_ERROR_CODE_PREFIX = 300_00000;

    private final int code;
    private final String message;

    @Override
    public int getCode() {
        return MATE_ERROR_CODE_PREFIX + this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
