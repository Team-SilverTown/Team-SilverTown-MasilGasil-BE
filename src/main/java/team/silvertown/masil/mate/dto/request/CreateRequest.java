package team.silvertown.masil.mate.dto.request;

import java.time.OffsetDateTime;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.validator.MateValidator;

public record CreateRequest(
    Long postId,
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    String title,
    String content,
    KakaoPoint gatheringPlacePoint,
    String gatheringPlaceDetail,
    // TODO: apply datetime parser
    OffsetDateTime gatheringAt,
    Integer capacity
) {

    public CreateRequest {
        MateValidator.notNull(postId, MateErrorCode.NULL_POST);
        MateValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        MateValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        MateValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        MateValidator.notNull(depth4, MapErrorCode.NULL_DEPTH4);
        MateValidator.validateTitle(title);
        MateValidator.notBlank(content, MateErrorCode.BLANK_CONTENT);
        MateValidator.notNull(gatheringPlacePoint, MapErrorCode.NULL_KAKAO_POINT);
        MateValidator.notBlank(gatheringPlaceDetail, MateErrorCode.BLANK_DETAIL);
        MateValidator.validateGatheringAt(gatheringAt);
        MateValidator.validateCapacity(capacity);
    }

}
