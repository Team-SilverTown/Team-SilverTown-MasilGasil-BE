package team.silvertown.masil.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.silvertown.masil.common.exception.ErrorCode;

@RequiredArgsConstructor
@Getter
public enum PostErrorCode implements ErrorCode {
    NULL_IS_PUBLIC(20210000, "산책로 포스트의 공개 여부를 확인할 수 없습니다"),

    THUMBNAIL_URL_TOO_LONG(20211000, "산책로 포스트 썸네일 URL 주소 길이가 제한을 초과했습니다"),
    TITLE_TOO_LONG(20211001, "산책로 포스트 제목 길이가 제한을 초과했습니다"),
    BLANK_TITLE(20211002, "산책로 포스트 제목이 입력되지 않았습니다"),

    NON_POSITIVE_DISTANCE(20212000, "산책로 포스트의 산책 거리는 양수여야 합니다"),
    NON_POSITIVE_TOTAL_TIME(202_12001, "산책로 포스트의 산책 시간은 양수여야 합니다"),

    POST_NOT_FOUND(20220400, "해당 아이디의 산책로 포스트를 찾을 수 없습니다"),

    NULL_MASIL(202_30000, "핀의 산책로 포스트를 확인할 수 없습니다"),
    NULL_IS_LIKE(202_30001, "좋아요 상태는 null이 될 수 없습니다"),
    PIN_OWNER_NOT_MATCHING(202_30300, "산책로 포스트의 사용자와 핀의 사용자가 다릅니다"),

    NULL_USER(202_90000, "산책로 포스트 사용자를 확인할 수 없습니다"),
    LOGIN_USER_NOT_FOUND(202_90400, "로그인한 사용자가 존재하지 않습니다"),
    AUTHOR_NOT_FOUND(202_90401, "산책로 포스트의 작성자를 찾을 수 없습니다"),

    INVALID_IP_ADDRESS(202_16001, "IP 주소가 올바르지 않습니다");

    private final int code;
    private final String message;
}
