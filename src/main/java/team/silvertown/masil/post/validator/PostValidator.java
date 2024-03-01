package team.silvertown.masil.post.validator;

import io.micrometer.common.util.StringUtils;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.ForbiddenException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostValidator extends Validator {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_URL_LENGTH = 1024;

    public static void validateTitle(String title) {
        int length = Objects.requireNonNullElse(title, "")
            .length();
        range(length, 0, MAX_TITLE_LENGTH, PostErrorCode.TITLE_TOO_LONG);
    }

    public static void validateUrl(String url) {
        if (StringUtils.isNotBlank(url)) {
            notOver(url.length(), MAX_URL_LENGTH, PostErrorCode.THUMBNAIL_URL_TOO_LONG);
        }
    }

    public static void validatePinOwner(Post post, Long userId) {
        User postOwner = post.getUser();

        throwIf(!Objects.equals(postOwner.getId(), userId),
            () -> new ForbiddenException(PostErrorCode.PIN_OWNER_NOT_MATCHING));
    }

}
