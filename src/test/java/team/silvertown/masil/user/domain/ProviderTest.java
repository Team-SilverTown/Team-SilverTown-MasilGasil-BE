package team.silvertown.masil.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.user.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.exception.UserErrorCode;

class ProviderTest {

    @Test
    public void provider_정상_생성_테스트() throws Exception {
        //given
        String normalProvider = "kakao";

        //when
        Provider provider = Provider.returnProvider(normalProvider);

        //then
        assertThat(provider.name()).isEqualTo("KAKAO");
        assertThat(provider.getValue()).isEqualTo("kakao");

    }

    @Test
    public void 유효하지_않은_provider_예외호출_테스트() throws Exception {
        //given, when
        String invalidProvider = "bug";

        //then
        Assertions.assertThatThrownBy(() -> Provider.returnProvider(invalidProvider))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_PROVIDER.getMessage());

    }

}
