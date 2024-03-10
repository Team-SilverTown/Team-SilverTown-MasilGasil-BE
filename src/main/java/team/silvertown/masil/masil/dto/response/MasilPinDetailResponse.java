package team.silvertown.masil.masil.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;

@Builder
public record MasilPinDetailResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static MasilPinDetailResponse from(MasilPin pin) {
        return MasilPinDetailResponse.builder()
            .id(pin.getId())
            .point(pin.getSimplePoint())
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

    public static List<MasilPinDetailResponse> listFrom(Masil masil) {
        return masil.getMasilPins()
            .stream()
            .map(MasilPinDetailResponse::from)
            .toList();
    }

}
