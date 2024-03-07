package team.silvertown.masil.alias;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class H2Alias {

    public static String formatDate(Date date, String mysqlFormatPattern) {
        String dateFormatPattern = mysqlFormatPattern
            .replace("%Y", "yyyy")
            .replace("%m", "MM")
            .replace("%d", "dd");

        return date.toInstant()
            .atOffset(ZoneOffset.of("+09:00"))
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(dateFormatPattern));
    }

}
