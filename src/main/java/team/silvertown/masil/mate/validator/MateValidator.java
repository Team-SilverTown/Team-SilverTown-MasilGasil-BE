package team.silvertown.masil.mate.validator;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.mate.exception.MateErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MateValidator extends Validator {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_DETAIL_LENGTH = 50;
    private static final int MAX_CAPACITY = 10;

    public static void validateTitle(String title) {
        notBlank(title, MateErrorCode.BLANK_TITLE);
        notOver(title.length(), MAX_TITLE_LENGTH, MateErrorCode.TITLE_TOO_LONG);
    }

    public static void validateDetail(String detail) {
        notBlank(detail, MateErrorCode.BLANK_DETAIL);
        notOver(detail.length(), MAX_DETAIL_LENGTH, MateErrorCode.DETAIL_TOO_LONG);
    }

    public static void validateGatheringAt(OffsetDateTime gatheringAt) {
        OffsetDateTime now = OffsetDateTime.now();

        throwIf(gatheringAt.isBefore(now),
            () -> new BadRequestException(MateErrorCode.GATHER_AT_PAST));
    }

    public static void validateCapacity(Integer capacity) {
        notNull(capacity, MateErrorCode.NULL_CAPACITY);
        notUnder(capacity, 0, MateErrorCode.NON_POSITIVE_CAPACITY);
        notOver(capacity, MAX_CAPACITY, MateErrorCode.CAPACITY_TOO_LARGE);
    }

}
