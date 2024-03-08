package team.silvertown.masil.post.exception;

import lombok.Getter;
import team.silvertown.masil.common.exception.ErrorCode;

@Getter
public enum PostErrorCode implements ErrorCode {
    THUMBNAIL_URL_TOO_LONG(20211000, "산책로 포스트썸네일 URL 주소 길이가 제한을 초과했습니다"),
    TITLE_TOO_LONG(20211001, "산책로 포스트 제목 길이가 제한을 초과했습니다"),
    BLANK_TITLE(20211002, "산책로 포스트 제목이 입력되지 않았습니다"),

    INVALID_DISTANCE(20212000, "산책로 포스트의 산책 거리는 양수여야 합니다"),
    INVALID_TOTAL_TIME(20212001, "산책로 포스트의 산책 시간은 양수여야 합니다"),

    NULL_MASIL(20230000, "핀의 산책로 포스트를 확인할 수 없습니다"),
    PIN_OWNER_NOT_MATCHING(20230300, "산책로 포스트의 사용자와 핀의 사용자가 다릅니다"),

    NULL_USER(20290000, "산책로 포스트 사용자를 확인할 수 없습니다");

    private final int code;
    private final String message;

    PostErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}