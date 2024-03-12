package team.silvertown.masil.user.validator;

import java.util.List;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.DateValidator;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.exception.UserErrorCode;

public class UserValidator extends Validator {

    private static final String VALID_NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9_]+$";

    public static void validateAuthority(List<UserAuthority> authorities) {
        throwIf(authorities.isEmpty(),
            () -> new DataNotFoundException(UserErrorCode.AUTHORITY_NOT_FOUND));
    }

    public static void validateNickname(String nickname, UserErrorCode userErrorCode) {
        notBlank(nickname, userErrorCode);
        range(nickname.length(), 2, 12, userErrorCode);
        notMatching(nickname, VALID_NICKNAME_PATTERN, userErrorCode);
    }

    public static boolean validateSex(String sex, UserErrorCode userErrorCode) {
        if (sex != null) {
            notBlank(sex, userErrorCode);
            Sex.get(sex);
            return true;
        }

        return false;
    }

    public static boolean validateBirthDate(String birthDate, UserErrorCode userErrorCode) {
        if (birthDate != null) {
            notBlank(birthDate, userErrorCode);
            DateValidator.parseDate(birthDate, userErrorCode);

            return true;
        }

        return false;
    }

    public static boolean validateHeight(Integer height, UserErrorCode userErrorCode) {
        if (height != null) {
            notUnder(height, 1, userErrorCode);

            return true;
        }

        return false;
    }

    public static boolean validateWeight(Integer weight, UserErrorCode userErrorCode) {
        if (weight != null) {
            notUnder(weight, 1, userErrorCode);

            return true;
        }

        return false;
    }

    public static boolean validateExerciseIntensity(
        String exerciseIntensity
    ) {
        if (exerciseIntensity != null) {
            ExerciseIntensity.get(exerciseIntensity);
            return true;
        }

        return false;
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
        throwIf(!isPersonalInfoConsented, () -> new BadRequestException(userErrorCode));
    }

    public static void validateIsLocationInfoConsented(
        Boolean isLocationInfoConsented,
        UserErrorCode userErrorCode
    ) {
        notNull(isLocationInfoConsented, userErrorCode);
        throwIf(!isLocationInfoConsented, () -> new BadRequestException(userErrorCode));
    }

    public static void validateIsUnderAgeConsentConfirmed(
        Boolean isUnderAgeConsentConfirmed,
        UserErrorCode userErrorCode
    ) {
        notNull(isUnderAgeConsentConfirmed, userErrorCode);
        throwIf(!isUnderAgeConsentConfirmed, () -> new BadRequestException(userErrorCode));
    }

}
