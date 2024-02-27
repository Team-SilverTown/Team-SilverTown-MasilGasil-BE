package team.silvertown.masil.user.exception;

import java.util.List;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.user.domain.UserAuthority;

public class UserValidator extends Validator {

    public static void validateAuthority(List<UserAuthority> authorities) {
        throwIf(authorities.isEmpty(),
            () -> new DataNotFoundException(UserErrorCode.AUTHORITY_NOT_FOUND));
    }

}
