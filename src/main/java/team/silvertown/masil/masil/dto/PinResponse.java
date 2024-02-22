package team.silvertown.masil.masil.dto;

import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.MasilPin;

@Builder
public record PinResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static PinResponse from(MasilPin pin) {
        KakaoPoint point = KakaoPoint.from(pin.getPoint());

        return PinResponse.builder()
            .id(pin.getId())
            .point(point)
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

}
