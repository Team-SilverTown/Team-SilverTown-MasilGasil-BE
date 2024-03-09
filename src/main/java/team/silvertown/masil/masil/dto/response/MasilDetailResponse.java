package team.silvertown.masil.masil.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.Masil;

@Builder
public record MasilDetailResponse(
    long id,
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    List<KakaoPoint> path,
    String content,
    int distance,
    int totalTime,
    int calories,
    OffsetDateTime startedAt,
    List<PinDetailResponse> pins,
    Long postId,
    String thumbnailUrl
) {

    public static MasilDetailResponse from(Masil masil, List<PinDetailResponse> pins) {
        return MasilDetailResponse.builder()
            .id(masil.getId())
            .depth1(masil.getDepth1())
            .depth2(masil.getDepth2())
            .depth3(masil.getDepth3())
            .depth4(masil.getDepth4())
            .path(masil.getKakaoPath())
            .content(masil.getContent())
            .distance(masil.getDistance())
            .totalTime(masil.getTotalTime())
            .calories(masil.getCalories())
            .startedAt(masil.getStartedAt())
            .pins(pins)
            .postId(masil.getPostId())
            .thumbnailUrl(masil.getThumbnailUrl())
            .build();
    }

}
