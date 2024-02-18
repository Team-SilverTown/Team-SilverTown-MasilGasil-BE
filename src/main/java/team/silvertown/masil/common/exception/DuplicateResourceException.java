package team.silvertown.masil.common.exception;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }

}
