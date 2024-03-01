package team.silvertown.masil.common.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.user.exception.UserErrorCode;

public class DateValidator {

    private static final String DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public static Date parseDate(String date, UserErrorCode errorCode) {
        if (!date.matches(DATE_FORMAT)) {
            throw new BadRequestException(errorCode);
        }

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        formatter.setLenient(false);

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new BadRequestException(errorCode);
        }
    }

}
