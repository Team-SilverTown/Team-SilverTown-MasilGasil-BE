package team.silvertown.masil.masil.dto;


import java.time.OffsetDateTime;
import java.util.List;
import team.silvertown.masil.common.map.KakaoPoint;

public record CreateRequest(
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    List<KakaoPoint> path,
    String title,
    String content,
    Integer distance,
    Integer totalTime,
    OffsetDateTime startedAt,
    List<CreatePinRequest> pins,
    String thumbnailUrl,
    Long postId
) {

}
