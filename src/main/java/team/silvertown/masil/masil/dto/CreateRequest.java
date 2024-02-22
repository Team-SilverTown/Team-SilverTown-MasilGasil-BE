package team.silvertown.masil.masil.dto;


import java.time.OffsetDateTime;
import java.util.List;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;

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

    private static final int MIN_POINT_NUM = 3;

    public CreateRequest {
        MasilValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        MasilValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        MasilValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        MasilValidator.validateTitle(title);
        MasilValidator.validateUrl(thumbnailUrl);
        MasilValidator.notNull(distance, MasilErrorCode.NULL_DISTANCE);
        MasilValidator.notNull(totalTime, MasilErrorCode.NULL_TOTAL_TIME);
        MasilValidator.notNull(path, MapErrorCode.NULL_KAKAO_POINT);
        MasilValidator.notUnder(path.size(), MIN_POINT_NUM, MapErrorCode.INSUFFICIENT_PATH_POINTS);
    }

}
