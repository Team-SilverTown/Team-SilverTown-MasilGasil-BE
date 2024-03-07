package team.silvertown.masil.masil.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;

@Builder
public record PinDetailResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static PinDetailResponse from(MasilPin pin) {
        return PinDetailResponse.builder()
            .id(pin.getId())
            .point(pin.getSimplePoint())
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

    public static List<PinDetailResponse> listFrom(Masil masil) {
        return masil.getMasilPins()
            .stream()
            .map(PinDetailResponse::from)
            .toList();
    }

}
