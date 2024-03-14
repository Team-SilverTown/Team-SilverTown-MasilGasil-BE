package team.silvertown.masil.mate.dto.response;

import java.time.OffsetDateTime;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.domain.Gathering;

@Builder
public record GatheringResponse(
    KakaoPoint point,
    String detail,
    OffsetDateTime gatherAt
) {

    public static GatheringResponse from(Gathering gathering) {
        return GatheringResponse.builder()
            .point(gathering.getKakaoPoint())
            .detail(gathering.getDetail())
            .gatherAt(gathering.getGatheringAt())
            .build();
    }

}
