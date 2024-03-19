package team.silvertown.masil.mate.dto.response;

import java.time.OffsetDateTime;
import lombok.Builder;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateStatus;
import team.silvertown.masil.user.domain.User;

@Builder
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

    public static SimpleMateResponse from(Mate mate) {
        User author = mate.getAuthor();

        return SimpleMateResponse.builder()
            .id(mate.getId())
            .title(mate.getTitle())
            .gatheringAt(mate.getGatheringAt())
            .status(mate.getStatus())
            .capacity(mate.getCapacity())
            .authorId(author.getId())
            .authorNickname(author.getNickname())
            .authorProfileUrl(author.getProfileImg())
            .build();
    }

}
