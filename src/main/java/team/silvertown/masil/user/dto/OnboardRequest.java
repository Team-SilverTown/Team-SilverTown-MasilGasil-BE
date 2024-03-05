package team.silvertown.masil.user.dto;

import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.exception.UserValidator;

public record OnboardRequest(
    String nickname,
    String sex,
    String birthDate,
    Integer height,
    Integer weight,
    String exerciseIntensity,
    Boolean isAllowingMarketing,
    Boolean isPersonalInfoConsented,
    Boolean isLocationInfoConsented,
    Boolean isUnderAgeConsentConfirmed
) {

    public OnboardRequest {
        // Essential
        UserValidator.validateNickname(nickname, UserErrorCode.INVALID_NICKNAME);
        UserValidator.validateIsAllowingMarketing(isAllowingMarketing,
            UserErrorCode.INVALID_ALLOWING_MARKETING);
        UserValidator.validateIsPersonalInfoConsented(isPersonalInfoConsented,
            UserErrorCode.INVALID_PERSONAL_INFO_CONSENTED);
        UserValidator.validateIsLocationInfoConsented(isLocationInfoConsented,
            UserErrorCode.INVALID_LOCATION_INFO_CONSENTED);
        UserValidator.validateIsUnderAgeConsentConfirmed(isUnderAgeConsentConfirmed,
            UserErrorCode.INVALID_UNDER_AGE_CONSENTED);

        // Not essential
        UserValidator.validateSex(sex, UserErrorCode.INVALID_SEX);
        UserValidator.validateBirthDate(birthDate, UserErrorCode.INVALID_BIRTH_DATE);
        UserValidator.validateHeight(height, UserErrorCode.INVALID_HEIGHT);
        UserValidator.validateWeight(weight, UserErrorCode.INVALID_WEIGHT);
        UserValidator.validateExerciseIntensity(exerciseIntensity);
    }

}
