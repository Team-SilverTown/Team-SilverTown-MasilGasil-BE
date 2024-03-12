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
    LocalDate birthDate,
    Sex sex,
    ExerciseIntensity exerciseIntensity,
    Boolean isPublic
) {

    public static MeInfoResponse from(User user) {
        return MeInfoResponse.builder()
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .height(user.getHeight())
            .weight(user.getWeight())
            .birthDate(user.getBirthDate())
            .sex(user.getSex())
            .exerciseIntensity(user.getExerciseIntensity())
            .isPublic(user.getIsPublic())
            .build();
    }

}
