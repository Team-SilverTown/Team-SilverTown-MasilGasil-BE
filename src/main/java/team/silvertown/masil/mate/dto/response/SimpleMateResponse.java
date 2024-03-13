package team.silvertown.masil.mate.dto.response;

import java.time.OffsetDateTime;
import team.silvertown.masil.mate.domain.MateStatus;

public record SimpleMateResponse(
    Long id,
    String title,
    OffsetDateTime gatheringAt,
    MateStatus status,
    Integer capacity,
    Long authorId,
    String authorNickname,
    String authorProfileUrl
) {

}
