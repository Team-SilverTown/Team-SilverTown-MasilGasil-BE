package team.silvertown.masil.user.domain;

import java.util.Arrays;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    RESTRICTED,
    NORMAL,
    ADMIN;

    public static Authority get(String name) {
        return Arrays.stream(Authority.values())
            .filter(authority -> name.equals(authority.name()))
            .findFirst()
            .orElseThrow();
    }

    @Override
    public String getAuthority() {
        return this.name();
    }

}
