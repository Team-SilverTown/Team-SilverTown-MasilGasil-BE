package team.silvertown.masil.common.scroll.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.silvertown.masil.common.exception.ErrorCode;

@RequiredArgsConstructor
@Getter
public enum ScrollErrorCode implements ErrorCode {
    INVALID_ORDER_TYPE(20311000, "올바르지 않은 정렬 기준입니다"),
    INVALID_CURSOR_FORMAT(20311001, "정렬 기준에 맞지 않은 커서 형식입니다");

    private final int code;
    private final String message;
<<<<<<< HEAD
=======

>>>>>>> 3f6963a (feat: 메이트 모집 목록 조회 요청 응답 dto 구현)
}
