package team.silvertown.masil.common.exception;

public class DataNotFoundException extends BaseException {

    public DataNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
