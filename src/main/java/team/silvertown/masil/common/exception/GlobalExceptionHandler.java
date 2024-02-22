package team.silvertown.masil.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final int UNKNOWN_EXCEPTION_CODE = -1;
    private static final int NOT_YET_HANDLED_EXCEPTION_CODE = -2;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BaseException e) {
        log.error(e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(NOT_YET_HANDLED_EXCEPTION_CODE, e.getMessage());

        return ResponseEntity.badRequest()
            .body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.warn(e.getMessage(), e);

        ErrorResponse response = ErrorResponse.from(e);

        return ResponseEntity.badRequest()
            .body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
        ForbiddenException e
    ) {
        log.warn(e.getMessage(), e);

        ErrorResponse response = ErrorResponse.from(e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(response);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException e) {
        log.warn(e.getMessage(), e);

        ErrorResponse response = ErrorResponse.from(e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
        DuplicateResourceException e
    ) {
        log.warn(e.getMessage(), e);

        ErrorResponse response = ErrorResponse.from(e);

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        log.error(e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(UNKNOWN_EXCEPTION_CODE, e.getMessage());

        return ResponseEntity.internalServerError()
            .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException e) {
        Throwable rootCause = e.getRootCause();

        if (rootCause instanceof BadRequestException) {
            return handleBadRequestException((BadRequestException) rootCause);
        }
        
        return handleUnknownException((Exception) rootCause);
    }

}
