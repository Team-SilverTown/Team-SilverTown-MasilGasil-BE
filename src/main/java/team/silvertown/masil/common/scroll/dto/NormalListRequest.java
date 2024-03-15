package team.silvertown.masil.common.scroll.dto;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

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
        Integer size
    ) {
        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
        this.scrollRequest = new ScrollRequest(order, cursor, size);
    }

    public int getSize() {
        return this.scrollRequest.getSize();
    }

    public boolean isBasedOnAddress() {
        return Objects.nonNull(depth1) && Objects.nonNull(depth2) && Objects.nonNull(depth3);
    }

}
