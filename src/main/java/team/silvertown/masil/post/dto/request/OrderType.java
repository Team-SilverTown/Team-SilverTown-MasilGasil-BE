package team.silvertown.masil.post.dto.request;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Objects;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.post.exception.PostErrorCode;

public enum OrderType {
    LATEST,
    MOST_POPULAR;

    public static OrderType get(String order) {
        if (StringUtils.isBlank(order)) {
            return LATEST;
        }

        return Arrays.stream(OrderType.values())
            .filter(orderType -> order.equals(orderType.name()))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(PostErrorCode.INVALID_ORDER_TYPE));
    }

    public static boolean isMostPopular(OrderType orderType) {
        return Objects.equals(orderType, MOST_POPULAR);
    }
}
