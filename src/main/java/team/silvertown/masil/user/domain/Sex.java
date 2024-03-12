package team.silvertown.masil.user.domain;

import java.util.Arrays;
import lombok.Getter;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.user.exception.UserErrorCode;

@Getter
public enum Sex {
    MALE,
    FEMALE;

    public static Sex get(String value) {
        return Arrays.stream(Sex.values())
            .filter(sex -> sex.name()
                .equals(value))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(UserErrorCode.INVALID_SEX));
    }
}
