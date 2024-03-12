package team.silvertown.masil.post.dto.request;

import lombok.Builder;
import lombok.Getter;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.common.response.ScrollRequest;
import team.silvertown.masil.post.validator.PostValidator;

@Getter
public final class NormalListRequest {

    private final String depth1;
    private final String depth2;
    private final String depth3;
    private final ScrollRequest scrollRequest;

    @Builder
    private NormalListRequest(
        String depth1,
        String depth2,
        String depth3,
        String order,
        String cursor,
        int size
    ) {
        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
        this.scrollRequest = new ScrollRequest(order, cursor, size);
    }

    public int getSize() {
        return this.scrollRequest.getSize();
    }

    public void validateAddress() {
        PostValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        PostValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        PostValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
    }

}
