package team.silvertown.masil.common.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.user.exception.UserErrorCode;

public class DateValidator {
    public static Date parseDate(String date, UserErrorCode errorCode) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new BadRequestException(errorCode);
        }
    }

}
