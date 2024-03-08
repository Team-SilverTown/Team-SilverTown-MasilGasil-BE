package team.silvertown.masil.post.dto.request;

import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.post.validator.PostValidator;

public record CreatePinRequest(
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public CreatePinRequest {
        PostValidator.notNull(point, MapErrorCode.NULL_KAKAO_POINT);
        PostValidator.validateUrl(thumbnailUrl);
    }

}
