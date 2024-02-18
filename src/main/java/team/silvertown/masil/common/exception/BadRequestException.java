package team.silvertown.masil.common.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

}
