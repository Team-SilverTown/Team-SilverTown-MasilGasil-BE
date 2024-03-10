package team.silvertown.masil.post.dto.request;

import lombok.Builder;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.post.validator.PostValidator;

@Builder
public record NormalListRequest(
    String depth1,
    String depth2,
    String depth3,
    OrderType order,
    String cursor,
    int size
) {

    public NormalListRequest {
        PostValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        PostValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        PostValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        PostValidator.validateCursorFormat(cursor, order);
    }

}
