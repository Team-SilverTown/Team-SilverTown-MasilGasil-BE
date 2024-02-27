package team.silvertown.masil.masil.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import team.silvertown.masil.masil.domain.Masil;

@Builder
public record SimpleMasilResponse(
    long id,
    String thumbnailUrl,
    OffsetDateTime startedAt
) {

    public static SimpleMasilResponse from(Masil masil) {
        return SimpleMasilResponse.builder()
            .id(masil.getId())
            .startedAt(masil.getStartedAt())
            .thumbnailUrl(masil.getThumbnailUrl())
            .build();
    }

}
