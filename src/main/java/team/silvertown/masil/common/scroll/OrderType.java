package team.silvertown.masil.common.scroll;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Objects;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.scroll.dto.ScrollErrorCode;

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
            .orElseThrow(() -> new BadRequestException(ScrollErrorCode.INVALID_ORDER_TYPE));
    }

    public static boolean isMostPopular(OrderType orderType) {
        return Objects.isNull(orderType) || orderType == MOST_POPULAR;
    }
}
