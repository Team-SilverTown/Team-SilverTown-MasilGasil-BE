package team.silvertown.masil.user.exception;

import java.util.List;
import java.util.Objects;
import team.silvertown.masil.auth.exception.AuthErrorCode;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.UserAuthority;

public class UserValidator extends Validator {

    public static void validateProvidedId(String providerId) {
        throwIf(Objects.isNull(providerId) || providerId.isEmpty(),
            () -> new InvalidAuthenticationException(AuthErrorCode.INVALID_PROVIDER_ID));
    }

}
