package team.silvertown.masil.masil.dto;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.masil.domain.Masil;

@Builder
public record MasilResponse(
    long id,
    String title,
    String content,
    int distance,
    int totalTime,
    List<PinResponse> pins
) {

    public static MasilResponse from(Masil masil, List<PinResponse> pins) {
        return MasilResponse.builder()
            .id(masil.getId())
            .title(masil.getTitle())
            .content(masil.getContent())
            .distance(masil.getDistance())
            .totalTime(masil.getTotalTime())
            .pins(pins)
            .build();
    }

}
