package team.silvertown.masil.user.dto;

import java.time.LocalDate;
import team.silvertown.masil.user.domain.User;

public record UpdateResponse(
    String nickname,
    String sex,
    LocalDate birthDate,
    int height,
    int weight,
    String exerciseIntensity
) {

    public static UpdateResponse from(User user) {
        return new UpdateResponse(
            user.getNickname(),
            user.getSex()
                .name(),
            user.getBirthDate(),
            user.getHeight(),
            user.getWeight(),
            user.getExerciseIntensity()
                .name());
    }

}
