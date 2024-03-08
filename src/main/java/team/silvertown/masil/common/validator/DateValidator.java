package team.silvertown.masil.common.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.ErrorCode;

public class DateValidator extends Validator {

    private static final String DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public static LocalDate parseDate(String date, ErrorCode errorCode) {
        notMatching(date, DATE_FORMAT, errorCode);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(errorCode);
        }
    }

}
