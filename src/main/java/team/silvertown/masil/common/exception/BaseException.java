package team.silvertown.masil.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final int code;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        code = errorCode.getCode();
    }

}
