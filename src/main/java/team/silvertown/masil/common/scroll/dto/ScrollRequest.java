package team.silvertown.masil.common.scroll.dto;

import java.util.Objects;
import lombok.Getter;
import team.silvertown.masil.common.scroll.OrderType;
import team.silvertown.masil.common.validator.ScrollValidator;

@Getter
public final class ScrollRequest {

    private static final int DEFAULT_SIZE = 10;

    private final OrderType order;
    private final String cursor;
    private final int size;

    public ScrollRequest(
        String order,
        String cursor,
        Integer size
    ) {
        OrderType orderType = OrderType.get(order);

        ScrollValidator.validateCursorFormat(cursor, orderType);

        this.order = orderType;
        this.cursor = cursor;
        this.size = Objects.isNull(size) ? DEFAULT_SIZE : size;
    }

}
