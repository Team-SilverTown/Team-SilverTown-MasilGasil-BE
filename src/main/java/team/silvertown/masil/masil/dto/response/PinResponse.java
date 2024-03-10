package team.silvertown.masil.masil.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;

@Builder
public record PinResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static PinResponse from(MasilPin pin) {
        return PinResponse.builder()
            .id(pin.getId())
            .point(pin.getSimplePoint())
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

    public static List<PinResponse> listFrom(Masil masil) {
        return masil.getMasilPins()
            .stream()
            .map(PinResponse::from)
            .toList();
    }

}
