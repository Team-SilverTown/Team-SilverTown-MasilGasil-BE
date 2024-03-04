package team.silvertown.masil.user.dto;

import java.time.LocalDate;
import lombok.Builder;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;

@Builder
public record MeInfoResponse(
    Long userId,
    String nickname,
    String profileImg,
    Integer height,
    Integer weight,
    LocalDate age,
    Sex sex,
    ExerciseIntensity exerciseIntensity
) {

    public static MeInfoResponse from(User user) {
        return MeInfoResponse.builder()
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .height(user.getHeight())
            .weight(user.getWeight())
            .age(user.getBirthDate())
            .sex(user.getSex())
            .exerciseIntensity(user.getExerciseIntensity())
            .build();
    }

}
