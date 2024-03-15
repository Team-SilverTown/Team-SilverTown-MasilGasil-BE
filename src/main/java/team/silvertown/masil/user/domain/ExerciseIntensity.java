package team.silvertown.masil.user.domain;

import java.util.Arrays;
import java.util.Objects;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.user.exception.UserErrorCode;

public enum ExerciseIntensity {
    SUPER_LOW,
    LOW,
    MIDDLE,
    HIGH,
    SUPER_HIGH;

    public static ExerciseIntensity get(String value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return Arrays.stream(ExerciseIntensity.values())
            .filter(exerciseIntensity -> exerciseIntensity.name()
                .equals(value))
            .findFirst()
            .orElseThrow(
                () -> new BadRequestException(UserErrorCode.INVALID_EXERCISE_INTENSITY));
    }

}
