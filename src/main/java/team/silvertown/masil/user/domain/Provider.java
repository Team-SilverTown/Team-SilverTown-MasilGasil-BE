package team.silvertown.masil.user.domain;

import java.util.Arrays;
import lombok.Getter;
import team.silvertown.masil.user.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.exception.UserErrorCode;

@Getter
public enum Provider {
    KAKAO("kakao");

    private final String value;

    Provider(String value) {
        this.value = value;
    }

    public static Provider get(String value) {
        return Arrays.stream(Provider.values())
            .filter(provider -> provider.getValue()
                .equals(value))
            .findFirst()
            .orElseThrow(() -> new InvalidAuthenticationException(UserErrorCode.INVALID_PROVIDER));
    }
}
