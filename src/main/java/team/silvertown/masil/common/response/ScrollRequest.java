package team.silvertown.masil.common.response;

import lombok.Getter;
import team.silvertown.masil.common.validator.ScrollValidator;
import team.silvertown.masil.post.dto.request.PostOrderType;

@Getter
public final class ScrollRequest {

    private final PostOrderType order;
    private final String cursor;
    private final int size;

    public ScrollRequest(
        String order,
        String cursor,
        int size
    ) {
        PostOrderType orderType = PostOrderType.get(order);

        ScrollValidator.validateCursorFormat(cursor, orderType);

        this.order = orderType;
        this.cursor = cursor;
        this.size = size;
    }

}
