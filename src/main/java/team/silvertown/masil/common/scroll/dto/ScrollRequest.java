package team.silvertown.masil.common.scroll.dto;

import lombok.Getter;
import team.silvertown.masil.common.scroll.OrderType;
import team.silvertown.masil.common.validator.ScrollValidator;

@Getter
public final class ScrollRequest {

    private final OrderType order;
    private final String cursor;
    private final int size;

    public ScrollRequest(
        String order,
        String cursor,
        int size
    ) {
        OrderType orderType = OrderType.get(order);

        ScrollValidator.validateCursorFormat(cursor, orderType);

        this.order = orderType;
        this.cursor = cursor;
        this.size = size;
    }

}
