package team.silvertown.masil.common.validator;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.scroll.OrderType;
import team.silvertown.masil.common.scroll.dto.ScrollErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScrollValidator extends Validator {

    private static final int ID_CURSOR_LENGTH = 16;
    private static final String INITIAL_CURSUR = "0";

    public static void validateCursorFormat(String cursor, OrderType order) {
        if (StringUtils.isBlank(cursor) || cursor.equals(INITIAL_CURSUR)) {
            return;
        }

        if (OrderType.isMostPopular(order)) {
            throwIf(cursor.length() != ID_CURSOR_LENGTH,
                () -> new BadRequestException(ScrollErrorCode.INVALID_CURSOR_FORMAT));
            return;
        }

        notOver(cursor.length(), ID_CURSOR_LENGTH, ScrollErrorCode.INVALID_CURSOR_FORMAT);
    }

}
