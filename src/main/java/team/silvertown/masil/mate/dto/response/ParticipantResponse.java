package team.silvertown.masil.mate.dto.response;

import lombok.Builder;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;

@Builder
public record ParticipantResponse(
    Long id,
    String nickname,
    String profileUrl,
    ParticipantStatus status
) {

    public static ParticipantResponse from(MateParticipant participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .nickname(participant.getUser().getNickname())
            .profileUrl(participant.getUser().getProfileImg())
            .status(participant.getStatus())
            .build();
    }

}
