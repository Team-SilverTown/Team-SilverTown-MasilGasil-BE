package team.silvertown.masil.user.exception;

import java.util.Date;
import java.util.List;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.user.domain.UserAuthority;

public class UserValidator extends Validator {

    private static final String VALID_NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]+$";

    public static void validateAuthority(List<UserAuthority> authorities) {
        throwIf(authorities.isEmpty(),
            () -> new DataNotFoundException(UserErrorCode.AUTHORITY_NOT_FOUND));
    }

    public static void validateNickname(String nickname, UserErrorCode userErrorCode) {
        notBlank(nickname, userErrorCode);
        throwIf(nickname.length() <= 2 || nickname.length() > 12, () -> new BadRequestException(userErrorCode));
        notMatching(nickname, VALID_NICKNAME_PATTERN, userErrorCode);
    }

    public static void validateSex(String sex, UserErrorCode userErrorCode) {

    }

    public static void validateBirthDate(Date birthDate, UserErrorCode userErrorCode) {
    }

    public static void validateHeight(Integer height, UserErrorCode userErrorCode) {
    }

    public static void validateWeight(Integer weight, UserErrorCode userErrorCode) {
    }

    public static void validateExerciseIntensity(
        String exerciseIntensity,
        UserErrorCode userErrorCode
    ) {
    }

    public static void validateIsAllowingMarketing(
        Boolean isAllowingMarketing,
        UserErrorCode userErrorCode
    ) {

    }

    public static void validateIsPersonalInfoConsented(
        Boolean isPersonalInfoConsented,
        UserErrorCode userErrorCode
    ) {

    }

    public static void validateIsLocationInfoConsented(
        Boolean isLocationInfoConsented,
        UserErrorCode userErrorCode
    ) {

    }

    public static void validateIsUnderAgeConsentConfirmed(
        Boolean isUnderAgeConsentConfirmed,
        UserErrorCode userErrorCode
    ) {

    }

}
