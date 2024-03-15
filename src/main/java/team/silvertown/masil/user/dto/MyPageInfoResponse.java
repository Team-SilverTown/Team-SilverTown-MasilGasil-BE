package team.silvertown.masil.user.dto;

import lombok.Builder;
import team.silvertown.masil.user.domain.User;

@Builder
public record MyPageInfoResponse(
    String nickname,
    String profileImg,
    Integer totalDistance,
    Integer totalCount,
    Integer totalCalories
) {

    public static MyPageInfoResponse from(User user) {
        return MyPageInfoResponse.builder()
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .totalDistance(user.getTotalDistance())
            .totalCount(user.getTotalCount())
            .totalCalories(user.getTotalCalories())
            .build();
    }

    public static MyPageInfoResponse fromPrivateUser(User user) {
        return MyPageInfoResponse.builder()
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .build();
    }

}
