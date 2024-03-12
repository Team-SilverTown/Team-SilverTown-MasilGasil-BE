package team.silvertown.masil.image.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.silvertown.masil.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    NOT_SUPPORTED_CONTENT(500_16000, "지원하지 않는 파일 형식 입니다"),
    FILE_IS_EMPTY(500_16000, "존재하지 않는 이미지 파일입니다.");

    private final int code;
    private final String message;
}
