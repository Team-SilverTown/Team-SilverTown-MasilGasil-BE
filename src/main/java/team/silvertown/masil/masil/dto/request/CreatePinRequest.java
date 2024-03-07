package team.silvertown.masil.masil.dto.request;

import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;

public record CreatePinRequest(
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public CreatePinRequest {
        MasilValidator.notNull(point, MapErrorCode.NULL_KAKAO_POINT);
        MasilValidator.validateUrl(thumbnailUrl);
    }

}
