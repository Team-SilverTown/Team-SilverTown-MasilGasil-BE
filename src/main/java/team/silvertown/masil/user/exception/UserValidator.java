package team.silvertown.masil.user.exception;

import java.util.List;
import java.util.Objects;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.UserAuthority;

public class UserValidator extends Validator {

    public static void validateAuthority(List<UserAuthority> authorities) {
        throwIf(authorities.isEmpty(),
            () -> new DataNotFoundException(UserErrorCode.AUTHORITY_NOT_FOUND));
    }

    public static void validateProvidedId(String providerId) {
        throwIf(Objects.isNull(providerId) || providerId.isEmpty(),
            () -> new InvalidAuthenticationException(UserErrorCode.INVALID_PROVIDER_ID));
    }

}
