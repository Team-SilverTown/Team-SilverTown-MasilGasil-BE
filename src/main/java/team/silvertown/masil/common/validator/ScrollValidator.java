package team.silvertown.masil.common.validator;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.post.dto.request.PostOrderType;
import team.silvertown.masil.post.exception.PostErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScrollValidator extends Validator {

    private static final int ID_CURSOR_LENGTH = 11;

    public static void validateCursorFormat(String cursor, PostOrderType order) {
        if (StringUtils.isBlank(cursor)) {
            return;
        }

        if (PostOrderType.isMostPopular(order)) {
            notUnder(cursor.length(), ID_CURSOR_LENGTH, PostErrorCode.INVALID_CURSOR_FORMAT);
            return;
        }

        notOver(cursor.length(), ID_CURSOR_LENGTH, PostErrorCode.INVALID_CURSOR_FORMAT);
    }

}
