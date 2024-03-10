package team.silvertown.masil.post.dto.request;

import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.post.validator.PostValidator;

public record CreatePostPinRequest(
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public CreatePostPinRequest {
        PostValidator.notNull(point, MapErrorCode.NULL_KAKAO_POINT);
        PostValidator.validateUrl(thumbnailUrl);
    }

}
