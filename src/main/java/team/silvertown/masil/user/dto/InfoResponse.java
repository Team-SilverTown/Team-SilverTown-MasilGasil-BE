package team.silvertown.masil.user.dto;

import java.time.LocalDate;
import lombok.Builder;
import team.silvertown.masil.common.validator.DateValidator;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.exception.UserErrorCode;

@Builder
public record InfoResponse(
    Long userId,
    String nickname,
    String profileImg,
    Integer height,
    Integer weight,
    String birthDate,
    Sex sex,
    ExerciseIntensity exerciseIntensity
) {

    public static InfoResponse from(User user) {
        return InfoResponse.builder()
            .userId(user.getId())
            .nickname(user.getNickname())
            .profileImg(user.getProfileImg())
            .height(user.getHeight())
            .weight(user.getWeight())
            .birthDate(
                DateValidator.parseToString(user.getBirthDate(), UserErrorCode.INVALID_BIRTH_DATE))
            .sex(user.getSex())
            .exerciseIntensity(user.getExerciseIntensity())
            .build();
    }

}
