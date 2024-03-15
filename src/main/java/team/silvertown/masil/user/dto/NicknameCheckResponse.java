package team.silvertown.masil.user.dto;

import lombok.Builder;

@Builder
public record NicknameCheckResponse(
    Boolean isDuplicated,
    String nickname
) {

}
