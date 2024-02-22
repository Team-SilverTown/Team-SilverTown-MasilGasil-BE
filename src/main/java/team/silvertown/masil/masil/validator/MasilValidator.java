package team.silvertown.masil.masil.validator;

import io.micrometer.common.util.StringUtils;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MasilValidator extends Validator {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_URL_LENGTH = 1024;

    public static void validateTitle(String title) {
        if (StringUtils.isNotBlank(title)) {
            notOver(title.length(), MAX_TITLE_LENGTH, MasilErrorCode.TITLE_TOO_LONG);
        }
    }

    public static void validateUrl(String url) {
        if (StringUtils.isNotBlank(url)) {
            notOver(url.length(), MAX_URL_LENGTH, MasilErrorCode.THUMBNAIL_URL_TOO_LONG);
        }
    }

    public static void validatePinOwner(Masil masil, Long userId) {
        User masilOwner = masil.getUser();

        throwIf(!Objects.equals(masilOwner.getId(), userId),
            () -> new BadRequestException(MasilErrorCode.OWNER_NOT_MATCHING));
    }

}
