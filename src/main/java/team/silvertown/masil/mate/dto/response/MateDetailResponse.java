package team.silvertown.masil.mate.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.domain.Mate;

@Builder
public record MateDetailResponse(
    Long id,
    String title,
    String content,
    KakaoPoint gatheringPlacePoint,
    String gatheringPlaceDetail,
    OffsetDateTime gatheringAt,
    List<ParticipantResponse> participants,
    Integer capacity,
    Long authorId,
    String authorNickname,
    String authorProfileUrl
) {

    public static MateDetailResponse from(Mate mate, List<ParticipantResponse> participants) {
        return MateDetailResponse.builder()
            .id(mate.getId())
            .title(mate.getTitle())
            .content(mate.getContent())
            .gatheringPlacePoint(mate.getGatheringPlacePoint())
            .gatheringPlaceDetail(mate.getGatheringPlaceDetail())
            .gatheringAt(mate.getGatheringAt())
            .participants(participants)
            .capacity(mate.getCapacity())
            .authorId(mate.getAuthor().getId())
            .authorNickname(mate.getAuthor().getNickname())
            .authorProfileUrl(mate.getAuthor().getProfileImg())
            .build();
    }

}
