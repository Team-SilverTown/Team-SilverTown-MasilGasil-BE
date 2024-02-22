package team.silvertown.masil.user.exception;

import team.silvertown.masil.common.exception.BaseException;
import team.silvertown.masil.common.exception.ErrorCode;

public class InvalidAuthenticationException extends BaseException {

    public InvalidAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

}
