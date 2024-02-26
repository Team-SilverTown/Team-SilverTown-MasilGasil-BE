package team.silvertown.masil.user.dto;

import java.util.Date;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.exception.UserValidator;

public record UpdateInfoRequest(
    String nickname,
    String sex,
    Date birthDate,
    Integer height,
    Integer weight,
    String exerciseIntensity,
    Boolean isAllowingMarketing,
    Boolean isPersonalInfoConsented,
    Boolean isLocationInfoConsented,
    Boolean isUnderAgeConsentConfirmed
) {

    public UpdateInfoRequest {
        UserValidator.validateNickname(nickname, UserErrorCode.INVALID_NICKNAME);
        UserValidator.validateSex(sex, UserErrorCode.INVALID_SEX);
        UserValidator.validateBirthDate(birthDate, UserErrorCode.INVALID_BIRTH_DATE);
        UserValidator.validateHeight(height, UserErrorCode.INVALID_HEIGHT);
        UserValidator.validateWeight(weight, UserErrorCode.INVALID_WEIGHT);
        UserValidator.validateExerciseIntensity(exerciseIntensity,
            UserErrorCode.INVALID_EXERCISE_INTENSITY);
        UserValidator.validateIsAllowingMarketing(isAllowingMarketing,
            UserErrorCode.INVALID_ALLOWING_MARKETING);
        UserValidator.validateIsPersonalInfoConsented(isPersonalInfoConsented, UserErrorCode.INVALID_PERSONAL_INFO_CONSENTED);
        UserValidator.validateIsLocationInfoConsented(isLocationInfoConsented, UserErrorCode.INVALID_LOCATION_INFO_CONSENTED);
        UserValidator.validateIsUnderAgeConsentConfirmed(isUnderAgeConsentConfirmed, UserErrorCode.INVALID_UNDER_AGE_CONSENTED);
    }

}
