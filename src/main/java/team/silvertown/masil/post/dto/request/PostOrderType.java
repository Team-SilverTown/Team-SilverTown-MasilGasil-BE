package team.silvertown.masil.post.dto.request;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Objects;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.post.exception.PostErrorCode;

public enum PostOrderType {
    LATEST,
    MOST_POPULAR;

    public static PostOrderType get(String order) {
        if (StringUtils.isBlank(order)) {
            return LATEST;
        }

        return Arrays.stream(PostOrderType.values())
            .filter(orderType -> order.equals(orderType.name()))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(PostErrorCode.INVALID_ORDER_TYPE));
    }

    public static boolean isMostPopular(PostOrderType postOrderType) {
        return Objects.equals(postOrderType, MOST_POPULAR);
    }
}
