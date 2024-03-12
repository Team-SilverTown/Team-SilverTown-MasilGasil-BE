package team.silvertown.masil.user.util;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    private static final String ANONYMOUS_USER_PRINCIPLE = "anonymousUser";

    public static Long getUserId() {
        return (Long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    }

    public static boolean isLogin() {
        if (Objects.isNull(SecurityContextHolder
            .getContext()
            .getAuthentication())) {
            return false;
        }

        return !ANONYMOUS_USER_PRINCIPLE.equals(SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal());
    }

}
