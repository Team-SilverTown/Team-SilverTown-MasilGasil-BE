package team.silvertown.masil.user.exception;

import org.springframework.security.oauth2.core.user.OAuth2User;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.user.domain.Provider;

public class OAuthValidator extends Validator {

    public static void validateSocialUser(OAuth2User oAuth2User) {
        throwIf(oAuth2User == null,
            () -> new InvalidAuthenticationException(UserErrorCode.INVALID_OAUTH2_TOKEN));
        throwIf(oAuth2User.getName() == null,
            () -> new InvalidAuthenticationException(UserErrorCode.INVALID_OAUTH2_TOKEN));
    }

    public static Provider validateProvider(String provider) {
        throwIf(provider == null,
            () -> new InvalidAuthenticationException(UserErrorCode.INVALID_PROVIDER));

        return Provider.returnProvider(provider);
    }

}
