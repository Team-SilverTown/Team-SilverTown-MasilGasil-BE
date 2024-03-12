package team.silvertown.masil.masil.dto.request;

import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;

public record CreateMasilPinRequest(
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public CreateMasilPinRequest {
        MasilValidator.notNull(point, MapErrorCode.NULL_KAKAO_POINT);
        MasilValidator.validateUrl(thumbnailUrl);
    }

}
