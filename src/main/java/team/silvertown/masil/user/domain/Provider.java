package team.silvertown.masil.user.domain;

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

    public static Provider returnProvider(String value) {
        Provider[] values = Provider.values();
        for (Provider provider : values) {
            if (provider.getValue()
                .equals(value)) {
                return provider;
            }
        }
        throw new InvalidAuthenticationException(UserErrorCode.INVALID_PROVIDER);
    }
}
