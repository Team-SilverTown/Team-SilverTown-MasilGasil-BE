package team.silvertown.masil.common.exception;

public record ErrorResponse(int code, String message) {

    public static ErrorResponse from(BaseException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

}
