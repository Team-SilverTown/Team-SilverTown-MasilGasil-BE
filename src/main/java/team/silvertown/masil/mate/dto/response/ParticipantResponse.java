package team.silvertown.masil.mate.dto.response;

import lombok.Builder;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.user.domain.User;

@Builder
public record ParticipantResponse(
    Long id,
    String message,
    Long userId,
    String nickname,
    String profileUrl,
    ParticipantStatus status
) {

    public static ParticipantResponse withMessageFrom(MateParticipant participant) {
        User user = participant.getUser();

        return ParticipantResponse.builder()
            .id(participant.getId())
            .message(participant.getMessage())
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileUrl(user.getProfileImg())
            .status(participant.getStatus())
            .build();
    }

    public static ParticipantResponse withoutMessageFrom(MateParticipant participant) {
        User user = participant.getUser();

        return ParticipantResponse.builder()
            .id(participant.getId())
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileUrl(user.getProfileImg())
            .status(participant.getStatus())
            .build();
    }

}
