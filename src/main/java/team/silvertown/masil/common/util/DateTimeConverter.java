package team.silvertown.masil.common.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeConverter {

    private static final ZoneOffset KR_OFFSET = ZoneOffset.of("+09:00");

    public static OffsetDateTime toBeginningOfDay(LocalDate date) {
        return date.atTime(OffsetTime.of(LocalTime.MIDNIGHT, KR_OFFSET));
    }

    public static OffsetDateTime toEndOfDay(LocalDate date) {
        return date.atTime(OffsetTime.of(LocalTime.MAX, KR_OFFSET));
    }

}
