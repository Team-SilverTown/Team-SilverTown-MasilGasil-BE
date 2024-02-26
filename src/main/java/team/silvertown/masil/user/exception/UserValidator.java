package team.silvertown.masil.user.exception;

import static team.silvertown.masil.common.validator.DateValidator.parseDate;

import java.util.Arrays;
import java.util.List;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.UserAuthority;

public class UserValidator extends Validator {

    private static final String VALID_NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]+$";

    public static void validateAuthority(List<UserAuthority> authorities) {
        throwIf(authorities.isEmpty(),
            () -> new DataNotFoundException(UserErrorCode.AUTHORITY_NOT_FOUND));
    }

    public static void validateNickname(String nickname, UserErrorCode userErrorCode) {
        notBlank(nickname, userErrorCode);
        throwIf(nickname.length() <= 2 || nickname.length() > 12,
            () -> new BadRequestException(userErrorCode));
        notMatching(nickname, VALID_NICKNAME_PATTERN, userErrorCode);
    }

    public static void validateSex(String sex, UserErrorCode userErrorCode) {
        notBlank(sex, userErrorCode);
        Arrays.stream(Sex.values())
            .filter(value -> value.name()
                .equals(sex))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(UserErrorCode.INVALID_SEX));
    }

    public static void validateBirthDate(String birthDate, UserErrorCode userErrorCode) {
        notBlank(birthDate, userErrorCode);
        parseDate(birthDate, userErrorCode);
    }

    public static void validateHeight(Integer height, UserErrorCode userErrorCode) {
        notNull(height, userErrorCode);
        notUnder(height, 0, userErrorCode);
        notUnder(height, 0, userErrorCode);
    }

    public static void validateWeight(Integer weight, UserErrorCode userErrorCode) {
        notNull(weight, userErrorCode);
        notUnder(weight, 0, userErrorCode);
        notUnder(weight, 0, userErrorCode);
    }

    public static void validateExerciseIntensity(
        String exerciseIntensity,
        UserErrorCode userErrorCode
    ) {
        notNull(exerciseIntensity, userErrorCode);
        Arrays.stream(ExerciseIntensity.values())
            .filter(value -> value.name()
                .equals(exerciseIntensity))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(UserErrorCode.INVALID_EXERCISE_INTENSITY));
    }

    public static void validateIsAllowingMarketing(
        Boolean isAllowingMarketing,
        UserErrorCode userErrorCode
    ) {
        notNull(isAllowingMarketing, userErrorCode);
    }

    public static void validateIsPersonalInfoConsented(
        Boolean isPersonalInfoConsented,
        UserErrorCode userErrorCode
    ) {
        notNull(isPersonalInfoConsented, userErrorCode);
    }

    public static void validateIsLocationInfoConsented(
        Boolean isLocationInfoConsented,
        UserErrorCode userErrorCode
    ) {
        notNull(isLocationInfoConsented, userErrorCode);
    }

    public static void validateIsUnderAgeConsentConfirmed(
        Boolean isUnderAgeConsentConfirmed,
        UserErrorCode userErrorCode
    ) {
        notNull(isUnderAgeConsentConfirmed, userErrorCode);
    }

}
