package team.silvertown.masil.masil.dto.request;


import java.time.OffsetDateTime;
import java.util.List;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;

public record CreateMasilRequest(
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    List<KakaoPoint> path,
    String content,
    Integer distance,
    Integer totalTime,
    Integer calories,
    OffsetDateTime startedAt,
    List<CreateMasilPinRequest> pins,
    String thumbnailUrl,
    Long postId
) {

    private static final int MIN_POINT_NUM = 3;

    public CreateMasilRequest {
        MasilValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        MasilValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        MasilValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        MasilValidator.notNull(depth4, MapErrorCode.NULL_DEPTH4);
        MasilValidator.validateUrl(thumbnailUrl);
        MasilValidator.notUnder(distance, 0, MasilErrorCode.NEGATIVE_DISTANCE);
        MasilValidator.notUnder(totalTime, 0, MasilErrorCode.NEGATIVE_TIME);
        MasilValidator.notUnder(calories, 0, MasilErrorCode.NEGATIVE_CALORIES);
        MasilValidator.notNull(path, MapErrorCode.NULL_PATH);
        MasilValidator.notUnder(path.size(), MIN_POINT_NUM, MapErrorCode.INSUFFICIENT_PATH_POINTS);
    }

}
