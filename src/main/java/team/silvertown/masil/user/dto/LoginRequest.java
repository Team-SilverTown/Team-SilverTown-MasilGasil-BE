package team.silvertown.masil.user.dto;

import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.exception.UserValidator;

public record LoginRequest(
    String accessToken
) {

    public LoginRequest {
        UserValidator.notBlank(accessToken, UserErrorCode.INVALID_OAUTH2_TOKEN);
    }

}
